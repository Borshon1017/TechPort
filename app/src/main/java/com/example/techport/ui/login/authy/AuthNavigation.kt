package com.example.techport.ui.login.authy

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.techport.ui.about.AboutScreen
import com.example.techport.ui.login.LoginScreen
import com.example.techport.ui.login.SignUpScreen

sealed class AuthRoute(val route: String) {
    object Login : AuthRoute("login")
    object Register : AuthRoute("register")
    object About : AuthRoute("about")
}

@Composable
fun AuthNavigation(
    navController: NavHostController = rememberNavController(),
    onLoginSuccess: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = AuthRoute.Login.route
    ) {
        // LOGIN SCREEN
        composable(AuthRoute.Login.route) {
            LoginScreen(
                onLogin = { _, _ -> onLoginSuccess() },
                onRegister = { navController.navigate(AuthRoute.Register.route) },
                onAbout = { navController.navigate(AuthRoute.About.route) }
            )
        }

        // SIGN-UP SCREEN
        composable(AuthRoute.Register.route) {
            SignUpScreen(
                onSignUp = { _, _, _, _, _ ->
                    navController.popBackStack() // return to login
                },
                onAlreadyHaveAccount = { navController.popBackStack() },
                onForgotPassword = {}
            )
        }

        // ABOUT SCREEN
        composable(AuthRoute.About.route) {
            AboutScreen(onBackClick = { navController.popBackStack() })
        }
    }
}
