package com.example.techport.ui.about

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

// Define a route constant for About
const val ABOUT_ROUTE = "about"

fun NavGraphBuilder.aboutNavigation(onBackClick: () -> Unit) {
    composable(ABOUT_ROUTE) {
        AboutScreen(onBackClick = onBackClick)
    }
}
