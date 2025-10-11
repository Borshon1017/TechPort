package com.example.techport.ui.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.techport.ui.login.authy.AuthEvent
import com.example.techport.ui.login.authy.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun SignUpRoute(
    onSignedUp: () -> Unit,
    onAlready: () -> Unit,
    onForgot: () -> Unit
) {
    val vm: AuthViewModel = viewModel()
    val host = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        vm.events.collect { ev ->
            when (ev) {
                is AuthEvent.Loading -> host.currentSnackbarData?.dismiss()
                is AuthEvent.Success -> onSignedUp()
                is AuthEvent.Error   -> scope.launch { host.showSnackbar(ev.message) }
                else -> {} // e.g., events you don't handle here (ResetSent, etc.)
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(hostState = host) }) { padding ->
        Box(Modifier.padding(padding)) {
            SignUpScreen(
                onSignUp = { first, last, nick, email, pass ->
                    vm.signUp(first.trim(), last.trim(), nick.trim(), email.trim(), pass)
                },
                onAlreadyHaveAccount = onAlready,
                onForgotPassword = onForgot
            )



        }
    }
}
