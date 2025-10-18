package com.example.techport.ui.login

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
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
import com.example.techport.R
import com.example.techport.ui.login.authy.AuthEvent
import com.example.techport.ui.login.authy.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.launch

@Composable
fun LoginRoute(
    onLoginSuccess: () -> Unit,
    onForgotPassword: () -> Unit = {},
    onSignUp: () -> Unit
) {
    val vm: AuthViewModel = viewModel()
    val host = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showReset by remember { mutableStateOf(false) }
    var resetEmail by remember { mutableStateOf("") }

    // --- Google sign-in setup ---
    val context = LocalContext.current
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }
    val googleClient = remember { GoogleSignIn.getClient(context, gso) }

    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { res ->
        if (res.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(res.data)
            val account = task.result
            val idToken = account?.idToken
            if (!idToken.isNullOrEmpty()) {
                vm.loginWithGoogle(idToken)
            } else {
                scope.launch { host.showSnackbar("Google sign-in failed.") }
            }
        } else {
            scope.launch { host.showSnackbar("Google sign-in canceled.") }
        }
    }
    // --- end Google setup ---

    LaunchedEffect(Unit) {
        vm.events.collect { ev ->
            when (ev) {
                is AuthEvent.Loading -> host.currentSnackbarData?.dismiss()
                is AuthEvent.Success -> onLoginSuccess()
                is AuthEvent.Error   -> scope.launch { host.showSnackbar(ev.message) }
                is AuthEvent.Info    -> {
                    scope.launch { host.showSnackbar(ev.message) }
                    showReset = false
                    resetEmail = ""
                }
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(hostState = host) }) { padding ->
        Box(Modifier.padding(padding)) {
            LoginScreen(
                onLogin = { email, password ->
                    vm.login(email, password)
                },
                onForgotPassword = {
                    resetEmail = ""
                    showReset = true
                },
                onSignUp = onSignUp,
                onGoogleLogin = { googleLauncher.launch(googleClient.signInIntent) } // <- 4b
            )
        }
    }

    if (showReset) {
        AlertDialog(
            onDismissRequest = { showReset = false; resetEmail = "" },
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
                TextButton(onClick = { vm.resetPassword(resetEmail.trim()) }) {
                    Text("Send link")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReset = false; resetEmail = "" }) {
                    Text("Cancel")
                }
            }
        )
    }
}
