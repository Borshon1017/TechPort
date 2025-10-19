package com.example.techport.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

sealed class HomeRoute(val route: String) {
    object Home : HomeRoute("home")
    object ProductDetail : HomeRoute("product_detail/{productId}") {
        fun createRoute(productId: String) = "product_detail/$productId"
    }
    object Cart : HomeRoute("cart")
    object AddProduct : HomeRoute("add_product")
    object EditProduct : HomeRoute("edit_product/{productId}") {
        fun createRoute(productId: String) = "edit_product/$productId"
    }
}

@Composable
fun HomeNavigation(
    navController: NavHostController = rememberNavController(),
    viewModel: HomeViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = HomeRoute.Home.route
    ) {
        // Home Screen
        composable(HomeRoute.Home.route) {
            HomeScreen(
                onProductClick = { product ->
                    navController.navigate(HomeRoute.ProductDetail.createRoute(product.id))
                },
                onCartClick = {
                    navController.navigate(HomeRoute.Cart.route)
                },
                onAddProductClick = {
                    navController.navigate(HomeRoute.AddProduct.route)
                },
                viewModel = viewModel
            )
        }

        // Product Detail Screen
        composable(HomeRoute.ProductDetail.route) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            val product = productId?.let { viewModel.getProductById(it) }

            if (product != null) {
                ProductDetailScreen(
                    product = product,
                    onBackClick = { navController.popBackStack() },
                    onEditClick = {
                        navController.navigate(HomeRoute.EditProduct.createRoute(product.id))
                    },
                    viewModel = viewModel
                )
            } else {
                // Product not found, show error or go back
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Product not found")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { navController.popBackStack() }) {
                            Text("Go Back")
                        }
                    }
                }
            }
        }

        // Cart Screen
        composable(HomeRoute.Cart.route) {
            CartScreen(
                onBackClick = { navController.popBackStack() },
                viewModel = viewModel
            )
        }

        // Add Product Screen
        composable(HomeRoute.AddProduct.route) {
            AddProductScreen(
                onBackClick = { navController.popBackStack() },
                viewModel = viewModel
            )
        }

        // Edit Product Screen
        composable(HomeRoute.EditProduct.route) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            val product = productId?.let { viewModel.getProductById(it) }

            if (product != null) {
                EditProductScreen(
                    product = product,
                    onBackClick = { navController.popBackStack() },
                    viewModel = viewModel
                )
            } else {
                
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Product not found")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { navController.popBackStack() }) {
                            Text("Go Back")
                        }
                    }
                }
            }
        }
    }
}