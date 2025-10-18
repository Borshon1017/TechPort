package com.example.techport.ui.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.techport.NavItem
import com.example.techport.ui.home.HomeScreen
import com.example.techport.ui.home.Product
import com.example.techport.ui.home.ProductDetailScreen
import com.example.techport.ui.map.MapScreen
import com.example.techport.ui.profile.ProfileScreen
import com.example.techport.ui.theme.TechPOrtTheme

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainScreen() {
    val navItems = listOf(
        NavItem("Home", Icons.Default.Home, "home"),
        NavItem("Map", Icons.Default.Place, "map"),
        NavItem("Profile", Icons.Default.Person, "profile")
    )

    var selectedItem by remember { mutableStateOf(navItems.first()) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    Scaffold(
        bottomBar = {
            if (selectedProduct == null) { // hide bottom bar when in product detail
                BottomNavBar(
                    navItems = navItems,
                    selectedItem = selectedItem,
                    onItemSelected = { selectedItem = it }
                )
            }
        },
        containerColor = Color.White.copy(alpha = 0.05f)
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {

            // Animated transition between Home and ProductDetail
            AnimatedContent(
                targetState = selectedProduct,
                transitionSpec = {
                    if (targetState != null) {
                        slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(400, easing = FastOutSlowInEasing)
                        ) + fadeIn() togetherWith
                                slideOutHorizontally(
                                    targetOffsetX = { -it / 2 },
                                    animationSpec = tween(300)
                                ) + fadeOut()
                    } else {
                        slideInHorizontally(
                            initialOffsetX = { -it / 2 },
                            animationSpec = tween(400, easing = FastOutSlowInEasing)
                        ) + fadeIn() togetherWith
                                slideOutHorizontally(
                                    targetOffsetX = { it },
                                    animationSpec = tween(300)
                                ) + fadeOut()
                    }
                },
                label = "home_product_transition"
            ) { targetProduct ->
                if (targetProduct == null) {
                    // Normal navigation (home, map, profile)
                    when (selectedItem.route) {
                        "home" -> HomeScreen(
                            userName = "Jade",
                            onProductClick = { product -> selectedProduct = product },
                            onProfileClick = {
                                selectedItem = navItems.find { it.route == "profile" }!!
                            }
                        )
                        "map" -> MapScreen()
                        "profile" -> ProfileScreen()
                    }
                } else {
                    // Product detail screen
                    ProductDetailScreen(
                        product = targetProduct,
                        onBackClick = { selectedProduct = null },
                        onAddToCart = { product ->
                            println("Added to cart: ${product.name}")
                        }
                    )
                }
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
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
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
        MainScreen()
    }
}
