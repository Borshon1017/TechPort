package com.example.techport.ui.login.authy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import com.google.firebase.auth.GoogleAuthProvider


sealed class AuthEvent {
    data object Loading : AuthEvent()
    data object Success : AuthEvent()
    data class Error(val message: String) : AuthEvent()
    data class Info(val message: String) : AuthEvent()
}

// com.example.techport.ui.login.authy.AuthViewModel
class AuthViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _events = Channel<AuthEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun signUp(first: String, last: String, nick: String?, email: String, pass: String) {
        viewModelScope.launch {
            _events.send(AuthEvent.Loading)
            auth.createUserWithEmailAndPassword(email.trim(), pass)
                .addOnCompleteListener { task ->
                    viewModelScope.launch {
                        if (task.isSuccessful) {
                            val display = (nick?.takeIf { it.isNotBlank() }
                                ?: listOf(first, last).filter { it.isNotBlank() }.joinToString(" ")).trim()
                            if (display.isNotEmpty()) {
                                auth.currentUser?.updateProfile(
                                    com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                        .setDisplayName(display)
                                        .build()
                                )
                            }
                            _events.send(AuthEvent.Success)
                        } else {
                            _events.send(AuthEvent.Error(task.exception?.localizedMessage ?: "Sign up failed"))
                        }
                    }
                }
        }
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _events.send(AuthEvent.Loading)
            auth.signInWithEmailAndPassword(email.trim(), pass)
                .addOnCompleteListener { task ->
                    viewModelScope.launch {
                        if (task.isSuccessful) _events.send(AuthEvent.Success)
                        else _events.send(AuthEvent.Error(task.exception?.localizedMessage ?: "Login failed"))
                    }
                }
        }
    }
    fun resetPassword(email: String) {
        viewModelScope.launch {
            val e = email.trim()
            if (e.isBlank()) {
                _events.send(AuthEvent.Error("Please enter your email."))
                return@launch
            }
            _events.send(AuthEvent.Loading)
            auth.sendPasswordResetEmail(e)
                .addOnCompleteListener { task ->
                    viewModelScope.launch {
                        if (task.isSuccessful) {
                            _events.send(AuthEvent.Info("Password reset link sent to $e"))
                        } else {
                            _events.send(
                                AuthEvent.Error(task.exception?.localizedMessage ?: "Couldn’t send reset email")
                            )
                        }
                    }
                }
        }
    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _events.send(AuthEvent.Loading)
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    viewModelScope.launch {
                        if (task.isSuccessful) {
                            _events.send(AuthEvent.Success)
                        } else {
                            _events.send(
                                AuthEvent.Error(task.exception?.localizedMessage ?: "Google sign-in failed")
                            )
                        }
                    }
                }
        }
    }

}

