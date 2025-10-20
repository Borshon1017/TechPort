package com.example.techport.ui.main

import androidx.compose.animation.core.FastOutSlowInEasing
import com.example.techport.ui.repair.RepairNavigation
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.techport.NavItem
import com.example.techport.ui.history.HistoryNavigation
import com.example.techport.ui.home.HomeNavigation
import com.example.techport.ui.map.MapScreen
import com.example.techport.ui.profile.ProfileScreen
import com.example.techport.ui.theme.TechPOrtTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.techport.ui.home.HomeViewModel

@Composable
fun MainScreen(onLogout: () -> Unit, viewModel: HomeViewModel = viewModel()) {
    val isAdmin = viewModel.userRole == "admin"

    val navItems = remember(isAdmin) {
        listOf(
            NavItem("Home", Icons.Default.Home, "home"),
            if (isAdmin) {
                NavItem("Repairs", Icons.Default.Build, "repairs")
            } else {
                NavItem("History", Icons.Default.History, "history")
            },
            NavItem("Map", Icons.Default.Place, "map"),
            NavItem("Profile", Icons.Default.Person, "profile")
        )
    }
    var selectedItem by remember { mutableStateOf(navItems.first()) }

    Scaffold(
        bottomBar = {
            BottomNavBar(navItems = navItems, selectedItem = selectedItem) { selectedItem = it }
        },
        containerColor = Color.White.copy(alpha = 0.05f)
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedItem.route) {
                "home" -> HomeNavigation()
                "repairs" -> RepairNavigation()
                "history" -> HistoryNavigation()
                "map" -> MapScreen()
                "profile" -> ProfileScreen(onLogout = onLogout)
            }
        }
    }
}

@Composable
fun BottomNavBar(
    navItems: List<NavItem>,
    selectedItem: NavItem,
    onItemSelected: (NavItem) -> Unit
) {
    Box(modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 24.dp, top = 8.dp)) {
        Surface(
            modifier = Modifier.fillMaxWidth().height(64.dp),
            shape = CircleShape,
            color = Color.Black,
            shadowElevation = 8.dp
        ) {
            val itemCount = navItems.size
            val barWidth = LocalConfiguration.current.screenWidthDp.dp - 48.dp
            val itemWidth = barWidth / itemCount
            val selectedIndex = navItems.indexOf(selectedItem)

            val indicatorOffset by animateDpAsState(
                targetValue = itemWidth * selectedIndex,
                animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
                label = "indicatorOffset"
            )

            Box(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .offset(x = indicatorOffset)
                        .width(itemWidth)
                        .fillMaxHeight()
                        .padding(vertical = 4.dp, horizontal = (itemWidth - 56.dp) / 2)
                        .background(Color.White, CircleShape)
                )

                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    navItems.forEach { item ->
                        BottomNavItem(
                            modifier = Modifier.weight(1f),
                            item = item,
                            isSelected = item.route == selectedItem.route,
                            onItemSelected = onItemSelected
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavItem(
    modifier: Modifier = Modifier,
    item: NavItem,
    isSelected: Boolean,
    onItemSelected: (NavItem) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onItemSelected(item) }
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            tint = if (isSelected) Color.Black else Color.Gray,
            modifier = Modifier.size(26.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    TechPOrtTheme {
        MainScreen(onLogout = {})
    }
}
