package com.example.techport.ui.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    val auth = Firebase.auth

    var showChangePasswordDialog by mutableStateOf(false)
    var showEditProfileDialog by mutableStateOf(false)

    fun logout() {
        auth.signOut()
    }

    fun changePassword(password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            auth.currentUser?.updatePassword(password)
                ?.addOnSuccessListener {
                    onSuccess()
                }
                ?.addOnFailureListener { 
                    onError(it.message ?: "Failed to change password")
                }
        }
    }

    fun updateProfile(displayName: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build()

            auth.currentUser?.updateProfile(profileUpdates)
                ?.addOnSuccessListener { 
                    onSuccess()
                }
                ?.addOnFailureListener { 
                    onError(it.message ?: "Failed to update profile")
                }
        }
    }
}