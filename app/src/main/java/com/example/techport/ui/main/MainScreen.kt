package com.example.techport.ui.main

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.techport.NavItem
import com.example.techport.ui.home.HomeScreen
import com.example.techport.ui.map.MapScreen
import com.example.techport.ui.profile.ProfileScreen
import com.example.techport.ui.theme.TechPOrtTheme

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
            "map" -> MapScreen()
            "profile" -> ProfileScreen()
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainScreenPreview() {
    TechPOrtTheme {
        MainScreen()
    }
}
