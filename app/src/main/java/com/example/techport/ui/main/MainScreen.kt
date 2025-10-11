package com.example.techport.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.techport.NavItem
import com.example.techport.ui.home.HomeScreen
import com.example.techport.ui.map.MapScreen
import com.example.techport.ui.profile.ProfileScreen

@Composable
fun MainScreen() {
    val navItems = listOf(
        NavItem("Home", Icons.Default.Home, "home"),
        NavItem("Map", Icons.Default.LocationOn, "map"),
        NavItem("Profile", Icons.Default.Info, "profile")
    )
    var selectedItem by remember { mutableStateOf(navItems.first()) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                navItems.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = selectedItem.route == item.route,
                        onClick = { selectedItem = item }
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedItem.route) {
            "home" -> HomeScreen()
            "map" -> Box(Modifier.padding(innerPadding)) { MapScreen() } // apply padding outside
            "profile" -> ProfileScreen()
        }
    }
}
