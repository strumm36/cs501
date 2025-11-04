package com.example.exploreboston

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityNavGraph(navController: NavHostController, modifier: Modifier = Modifier) {

    // Observe back stack for dynamic title and back button
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val title = when {
                        currentRoute?.startsWith("list") == true -> "List"
                        currentRoute?.startsWith("detail") == true -> "Detail"
                        currentRoute?.startsWith("categories") == true -> "Categories"
                        else -> "Home"
                    }
                    Text(title)
                },
                navigationIcon = {
                    // Show back button only if not on Home
                    if (currentRoute != Screen.Home.route) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen(navController) }

            composable(Screen.Categories.route) { CategoriesScreen(navController) }

            composable(
                Screen.List.route,
                arguments = listOf(navArgument("category") { type = NavType.StringType })
            ) { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category") ?: "Unknown"
                ListScreen(navController, category)
            }

            composable(
                Screen.Detail.route,
                arguments = listOf(
                    navArgument("category") { type = NavType.StringType },
                    navArgument("locationId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category") ?: "Unknown"
                val locationId = backStackEntry.arguments?.getInt("locationId") ?: -1
                DetailScreen(navController, category, locationId)
            }
        }
    }
}
