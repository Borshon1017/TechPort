package com.example.techport.ui.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.launch
import androidx.core.net.toUri

class ProfileViewModel : ViewModel() {

    // Use Firebase SDK singletons directly
    // Use fully-qualified SDK singletons to avoid potential ktx import resolution issues
    val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
    private val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()

    // expose profile photo URL as observable state so UI recomposes when it changes
    var profilePhotoUrl by mutableStateOf(auth.currentUser?.photoUrl?.toString())

    // UI state is owned by the composables; keep only the profilePhotoUrl here

    fun logout() {
        auth.signOut()
    }

    fun changePassword(password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            auth.currentUser?.updatePassword(password)
                ?.addOnSuccessListener { onSuccess() }
                ?.addOnFailureListener { e -> onError(e.message ?: "Failed to change password") }
        }
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