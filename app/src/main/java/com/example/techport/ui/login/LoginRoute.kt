package com.example.techport.ui.login

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.techport.R
import com.example.techport.ui.login.authy.AuthEvent
import com.example.techport.ui.login.authy.AuthViewModel
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
@Composable
fun LoginRoute(
    onLoginSuccess: () -> Unit,
    onForgotPassword: () -> Unit,
    onSignUp: () -> Unit,
    onAbout: () -> Unit = {}   // ← keep this
) {
    val vm: AuthViewModel = viewModel()
    val host = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showReset by remember { mutableStateOf(false) }
    var resetEmail by remember { mutableStateOf("") }

    val context = LocalContext.current
    val oneTapClient = remember { Identity.getSignInClient(context) }
    val signInIntentRequest = remember {
        com.google.android.gms.auth.api.identity.GetSignInIntentRequest.builder()
            .setServerClientId(context.getString(R.string.default_web_client_id))
            .build()
    }

    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            try {
                val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
                val idToken = credential.googleIdToken
                if (idToken != null) {
                    vm.loginWithGoogle(idToken)
                } else scope.launch { host.showSnackbar("Google sign-in failed: No ID token found.") }
            } catch (e: ApiException) {
                val message = when (e.statusCode) {
                    CommonStatusCodes.CANCELED -> "Google sign-in canceled."
                    else -> "Error: ${e.localizedMessage}"
                }
                scope.launch { host.showSnackbar(message) }
            }
        } else scope.launch { host.showSnackbar("Google sign-in failed.") }
    }

    LaunchedEffect(Unit) {
        vm.events.collect { ev ->
            when (ev) {
                is AuthEvent.Success -> onLoginSuccess()
                is AuthEvent.Error -> scope.launch { host.showSnackbar(ev.message) }
                is AuthEvent.Info -> {
                    scope.launch { host.showSnackbar(ev.message) }
                    showReset = false
                }
                else -> {}
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(hostState = host) }) { padding ->
        Box(Modifier.padding(padding)) {
            LoginScreen(
                onLogin = { email, pass -> vm.login(email, pass) },
                onForgotPassword = {
                    resetEmail = ""
                    showReset = true
                },
                onRegister = onSignUp,
                onAbout = onAbout,
                onGoogleLogin = {
                    oneTapClient.getSignInIntent(signInIntentRequest)
                        .addOnSuccessListener { pendingIntent ->
                            googleLauncher.launch(IntentSenderRequest.Builder(pendingIntent.intentSender).build())
                        }
                        .addOnFailureListener { e ->
                            Log.e("LoginRoute", "Google sign-in intent failed", e)
                            scope.launch { host.showSnackbar("Google sign-in failed: ${e.localizedMessage}") }
                        }
                }
            )
        }
    }

    if (showReset) {
        AlertDialog(
            onDismissRequest = { showReset = false },
            title = { Text("Reset password") },
            text = {
                OutlinedTextField(
                    value = resetEmail,
                    onValueChange = { resetEmail = it },
                    label = { Text("Email") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
            },
            confirmButton = {
                TextButton(onClick = { vm.resetPassword(resetEmail.trim()) }) { Text("Send link") }
            },
            dismissButton = {
                TextButton(onClick = { showReset = false }) { Text("Cancel") }
            }
        )
    }
}
