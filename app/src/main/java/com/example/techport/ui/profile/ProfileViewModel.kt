package com.example.techport.ui.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.launch
import androidx.core.net.toUri

@Suppress("DEPRECATION")
class ProfileViewModel : ViewModel() {

    // Use Firebase SDK singletons directly
    // Use fully-qualified SDK singletons to avoid potential ktx import resolution issues
    val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
    private val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()

    // expose profile photo URL as observable state so UI recomposes when it changes
    var profilePhotoUrl by mutableStateOf(auth.currentUser?.photoUrl?.toString())
    // Show Change Password option in the UI for all users (visibility-only).
    // Actual password update still requires reauthentication and will fail for OAuth-only accounts.
    var canChangePassword by mutableStateOf(true)

    init {
        // Keep Change Password visible by default; skip provider detection here.
        canChangePassword = true
    }

    /**
     * Explicit refresh of canChangePassword. Call this after auth state is ready.
     */
    fun refreshCanChangePassword() {
        // No-op refresh for visibility — keep Change Password enabled in UI.
        canChangePassword = true
    }

    // No special cleanup required

    // UI state is owned by the composables; keep only the profilePhotoUrl here

    fun logout() {
        auth.signOut()
    }

    /**
     * Change user password after re-authenticating with the current password.
     * This uses Firebase's EmailAuthProvider to reauthenticate then updatePassword.
     */
    fun changePassword(currentPassword: String, newPassword: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val user = auth.currentUser
        val email = user?.email
        if (user == null || email.isNullOrBlank()) {
            onError("No authenticated user")
            return
        }
        // First, if the current FirebaseUser already indicates a password provider, attempt
        // to reauthenticate directly using the provided current password. This avoids an
        // extra network call when not necessary.
        val hasPasswordProviderLocally = user.providerData.any { it.providerId == "password" }
        if (hasPasswordProviderLocally) {
            try {
                val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(email, currentPassword)
                user.reauthenticate(credential)
                    .addOnSuccessListener {
                        user.updatePassword(newPassword)
                            .addOnSuccessListener { onSuccess() }
                            .addOnFailureListener { e -> onError(e.message ?: "Failed to change password") }
                    }
                    .addOnFailureListener { e -> onError(e.message ?: "Reauthentication failed") }
            } catch (e: Exception) {
                onError(e.message ?: "Failed to change password")
            }
            return
        }

        // If local providerData doesn't show password, check with fetchSignInMethodsForEmail
        // to be sure (covers corner cases where providerData may not include password).
        auth.fetchSignInMethodsForEmail(email)
            .addOnSuccessListener { result ->
                val methods = result.signInMethods ?: emptyList()
                if (methods.contains(com.google.firebase.auth.EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD)) {
                    try {
                        val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(email, currentPassword)
                        user.reauthenticate(credential)
                            .addOnSuccessListener {
                                user.updatePassword(newPassword)
                                    .addOnSuccessListener { onSuccess() }
                                    .addOnFailureListener { e -> onError(e.message ?: "Failed to change password") }
                            }
                            .addOnFailureListener { e -> onError(e.message ?: "Reauthentication failed") }
                    } catch (e: Exception) {
                        onError(e.message ?: "Failed to change password")
                    }
                } else {
                    onError("Your account uses a different sign-in method. Please reauthenticate using your sign-in provider (e.g., Google) and try again.")
                }
            }
            .addOnFailureListener { e -> onError(e.message ?: "Failed to verify sign-in methods") }
    }

    // (displayName update not used in current app UI) If needed reintroduce later

    fun updateProfilePhoto(photoUrl: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val user = auth.currentUser
                val uid = user?.uid
                if (uid != null) {
                    // update Firestore user doc with photoUrl (merge)
                    val map = mapOf("photoUrl" to photoUrl)
                    db.collection("user").document(uid).set(map, SetOptions.merge())
                }

                // Update Firebase Auth profile photo
                val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                    .setPhotoUri(photoUrl.toUri())
                    .build()

                user?.updateProfile(profileUpdates)
                    ?.addOnSuccessListener {
                        // update observable state so UI refreshes immediately
                        profilePhotoUrl = photoUrl
                        onSuccess()
                    }
                    ?.addOnFailureListener { e -> onError(e.message ?: "Failed to update profile photo") }
            } catch (e: Exception) {
                onError(e.message ?: "Failed to update profile photo")
            }
        }
    }

    // no external image host used; presets only
}