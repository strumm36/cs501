package com.example.recipebrowser

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.recipebrowser.ui.theme.RecipeBrowserTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import kotlin.getValue
import kotlin.ranges.contains

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecipeBrowserTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen()
                }
            }
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Home : Screen("home", "Home", Icons.Default.Home)
    data object Add : Screen("add", "Add", Icons.Default.Add)
    data object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

// A list of all our screens to easily iterate over for the navigation bar.
val screens = listOf(
    Screen.Home,
    Screen.Add,
    Screen.Settings
)

/**
 * This is the main composable that sets up the UI, including the Scaffold,
 * NavHost, and the Bottom Navigation Bar.
 */
@Composable
fun MainScreen() {

    val viewModel: RecipeViewModel = viewModel()
    // 1. Create a NavController. `rememberNavController()` creates and remembers it
    // across recompositions. This is the heart of our navigation system.
    val navController = rememberNavController()

    // 2. Use Scaffold, a layout that provides slots for top bars, bottom bars,
    // floating action buttons, and the main content.
    Scaffold(
        bottomBar = {
            // Our custom bottom navigation bar.
            NavigationBar {
                // 3. Get the current back stack entry. This tells us which screen is currently displayed.
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                // 4. Iterate over our list of screens to create a navigation item for each one.
                screens.forEach { screen ->
                    NavigationBarItem(
                        label = { Text(screen.title) }, // The text label for the item.
                        icon = { Icon(screen.icon, contentDescription = screen.title) }, // The icon for the item.

                        // 5. Determine if this item is currently selected.
                        // We check if the current route is part of the destination's hierarchy.
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,

                        // 6. Define the click action for the item.
                        onClick = {
                            // This is the core navigation logic.
                            navController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items.
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true // Save the state of the screen you're leaving.
                                }
                                // Avoid multiple copies of the same destination when re-selecting the same item.
                                launchSingleTop = true
                                // Restore state when re-selecting a previously selected item.
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        // 7. Define the NavHost, which is the container for our screen content.
        // The content of the NavHost changes based on the current route.
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen(screen = Screen.Home, viewModel, navController) }
            composable(Screen.Add.route) { AddScreen(screen = Screen.Add, navController, viewModel) }
            composable(Screen.Settings.route) { SettingsScreen(screen = Screen.Settings) }

            // Detail route with an argument
            composable("detail/{index}") { backStackEntry ->
                val index = backStackEntry.arguments?.getString("index")?.toIntOrNull()
                if (index != null && index in viewModel.listItems.indices) {
                    val recipe = viewModel.listItems[index]
                    DetailScreen(navController, recipe = recipe)
                } else {
                    Text("Recipe not found")
                }
            }
        }
    }
}

/**
 * A generic, reusable screen composable to avoid repetitive code.
 * It simply displays the title of the screen passed to it.
 *
 * @param screen The Screen object containing the title to display.
 */

@Composable
fun HomeScreen(screen: Screen, viewModel: RecipeViewModel, navController: NavHostController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        itemsIndexed(viewModel.listItems) { index, item ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable {
                        navController.navigate("detail/$index")
                    }
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(navController: NavHostController, recipe: Recipe) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(recipe.title) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Ingredients:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = recipe.ingredients,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Steps:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(recipe.steps)
        }
    }
}

@Composable
fun SettingsScreen(screen: Screen) {
    Text("Settings")
}

@Composable
fun AddScreen(
    screen: Screen,
    navController: NavHostController,
    viewModel: RecipeViewModel
) {
    var title by rememberSaveable { mutableStateOf("") }
    var ingredients by rememberSaveable { mutableStateOf("") }
    var steps by rememberSaveable { mutableStateOf("") }

    Scaffold() { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            TextField(
                value = ingredients,
                onValueChange = { ingredients = it },
                label = { Text("Ingredients (one per line)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                maxLines = 6
            )

            TextField(
                value = steps,
                onValueChange = { steps = it },
                label = { Text("Steps (one per line)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                maxLines = 6
            )

            Button(
                onClick = {
                    // Only add if not blank
                    if (title.isNotBlank()) {
                        viewModel.addRecipe(
                            title = title.trim(),
                            ingredients = ingredients.trim(),
                            steps = steps.trim()
                        )
                        // Navigate back to home
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit Recipe")
            }
        }
    }
}