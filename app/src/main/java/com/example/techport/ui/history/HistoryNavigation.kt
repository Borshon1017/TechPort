package com.example.techport.ui.history

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun HistoryNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "history_list") {
        composable("history_list") {
            HistoryScreen()
        }
    }
}