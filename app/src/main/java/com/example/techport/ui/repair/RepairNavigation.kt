package com.example.techport.ui.repair

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
import com.example.techport.ui.home.HomeViewModel

sealed class RepairRoute(val route: String) {
    object RepairList : RepairRoute("repair_list")
    object AddRepair : RepairRoute("add_repair")
    object RepairDetail : RepairRoute("repair_detail/{repairId}") {
        fun createRoute(repairId: String) = "repair_detail/$repairId"
    }
}

@Composable
fun RepairNavigation(
    navController: NavHostController = rememberNavController(),
    homeViewModel: HomeViewModel = viewModel(),
    repairViewModel: RepairViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = RepairRoute.RepairList.route
    ) {
        // Repair List Screen
        composable(RepairRoute.RepairList.route) {
            RepairListScreen(
                onRepairClick = { repair ->
                    navController.navigate(RepairRoute.RepairDetail.createRoute(repair.id))
                },
                onAddRepairClick = {
                    navController.navigate(RepairRoute.AddRepair.route)
                },
                viewModel = repairViewModel
            )
        }

        // Add Repair Screen
        composable(RepairRoute.AddRepair.route) {
            AddRepairScreen(
                onBackClick = { navController.popBackStack() },
                homeViewModel = homeViewModel,
                repairViewModel = repairViewModel
            )
        }

        // Repair Detail Screen
        composable(RepairRoute.RepairDetail.route) { backStackEntry ->
            val repairId = backStackEntry.arguments?.getString("repairId")
            val repair = repairId?.let { repairViewModel.getRepairById(it) }

            if (repair != null) {
                RepairDetailScreen(
                    repair = repair,
                    onBackClick = { navController.popBackStack() },
                    viewModel = repairViewModel
                )
            } else {
                // Repair not found
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Repair request not found")
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