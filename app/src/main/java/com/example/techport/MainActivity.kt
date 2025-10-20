package com.example.techport

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.techport.ui.login.LoginRoute
import com.example.techport.ui.login.SignUpRoute
import com.example.techport.ui.main.MainScreen
import com.example.techport.ui.map.MapScreen
import com.example.techport.ui.theme.TechPOrtTheme

private object Routes {
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val HOME = "home"
    const val MAP = "map"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TechPOrtTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = Routes.LOGIN) {

        composable(Routes.LOGIN) {
            LoginRoute(
                onLoginSuccess = {
                    nav.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onForgotPassword = { /* TODO */ },
                onSignUp = { nav.navigate(Routes.SIGNUP) }
            )
        }

        composable(Routes.SIGNUP) {
            SignUpRoute(
                onSignedUp = { nav.popBackStack(Routes.LOGIN, inclusive = false) },
                onAlready = { nav.popBackStack() },
                onForgot = { /* TODO */ }
            )
        }

        composable(Routes.HOME) { 
            MainScreen(onLogout = {
                nav.navigate(Routes.LOGIN) {
                    popUpTo(Routes.HOME) { inclusive = true }
                }
            })
         }
        composable(Routes.MAP)  { MapScreen() }
    }
}
