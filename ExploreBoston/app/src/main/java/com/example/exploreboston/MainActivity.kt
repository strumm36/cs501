package com.example.exploreboston

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.exploreboston.ui.theme.ExploreBostonTheme

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Categories : Screen("categories")
    object List : Screen("list/{category}") {
        fun createRoute(category: String) = "list/$category"
    }
    object Detail : Screen("detail/{category}/{locationId}") {
        fun createRoute(category: String, locationId: Int) = "detail/$category/$locationId"
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExploreBostonTheme {
                val navController = rememberNavController()
                CityNavGraph(navController, Modifier.fillMaxSize())
            }
        }
    }
}


data class Location(
    val id: Int,
    val name: String,
    val description: String,
    val address: String
)

data class Category(
    val name: String,
    val locations: List<Location>
)


// Sample data is AI-generated
object CityData {

    val categories = listOf(
        Category(
            name = "Museums",
            locations = listOf(
                Location(
                    id = 1,
                    name = "MIT Museum",
                    description = "The MIT Museum showcases a wide variety of fascinating science and technology exhibits, including robotics, holography, and artificial intelligence demonstrations. Visitors can explore the intersection of innovation, research, and creativity.",
                    address = "265 Massachusetts Ave, Cambridge, MA 02139"
                ),
                Location(
                    id = 2,
                    name = "Museum of Fine Arts",
                    description = "One of the largest museums in the United States, the Museum of Fine Arts features artworks from around the globe, spanning thousands of years. The collection includes paintings, sculptures, textiles, and contemporary installations.",
                    address = "465 Huntington Ave, Boston, MA 02115"
                )
            )
        ),
        Category(
            name = "Parks",
            locations = listOf(
                Location(
                    id = 3,
                    name = "Boston Common",
                    description = "Boston Common is the oldest public park in the United States, offering expansive green spaces, walking paths, and historic monuments. It's a central gathering place for festivals, protests, and outdoor activities.",
                    address = "139 Tremont St, Boston, MA 02111"
                ),
                Location(
                    id = 4,
                    name = "Public Garden",
                    description = "Adjacent to Boston Common, the Public Garden features beautifully landscaped botanical gardens, iconic swan boats, and seasonal floral displays. It is a serene oasis in the middle of the city.",
                    address = "4 Charles St, Boston, MA 02116"
                )
            )
        ),
        Category(
            name = "Restaurants",
            locations = listOf(
                Location(
                    id = 5,
                    name = "Legal Sea Foods",
                    description = "Legal Sea Foods is a renowned seafood restaurant chain in Boston known for its fresh, high-quality seafood. The menu offers a variety of classic New England dishes, including clam chowder and lobster rolls.",
                    address = "270 Northern Ave, Boston, MA 02210"
                ),
                Location(
                    id = 6,
                    name = "Mike's Pastry",
                    description = "Located in the historic North End, Mike's Pastry is famous for its cannoli and wide variety of desserts. Tourists and locals alike flock here for authentic Italian pastries and sweet treats.",
                    address = "300 Hanover St, Boston, MA 02113"
                )
            )
        )
    )
}


@Composable
fun HomeScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Welcome to the Explore Boston application, where through navigating the app you can experience a tour of the city of Boston."
        )
        Button(
            onClick = {
                navController.navigate(Screen.Categories.route)
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Go to Categories")
        }
    }
}


@Composable
fun CategoriesScreen(navController: NavHostController) {
    val categories = CityData.categories

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Select a Category",
            style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        categories.forEach { category ->
            Button(
                onClick = {
                    // Navigate to ListScreen for the selected category
                    navController.navigate(Screen.List.createRoute(category.name))
                },
                modifier = Modifier
                    .padding(vertical = 8.dp)
            ) {
                Text(category.name)
            }
        }

        Text(
            text = "Back to Home",
            style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp, top=16.dp)
        )

        // Back to Home button
        Button(
            onClick = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            },
        ) {
            Text("Home")
        }
    }
}



@Composable
fun ListScreen(navController: NavHostController, categoryName: String) {
    val category = CityData.categories.find { it.name == categoryName }

    androidx.compose.foundation.layout.Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Locations in $categoryName",
            style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        category?.locations?.forEach { location ->
            Button(
                onClick = {
                    // Navigate to detail screen with category and location id
                    navController.navigate(Screen.Detail.createRoute(category.name, location.id))
                },
                modifier = Modifier
                    .padding(vertical = 8.dp)
            ) {
                Text(location.name)
            }
        }
    }
}

@Composable
fun DetailScreen(navController: NavHostController, categoryName: String, locationId: Int) {
    val category = CityData.categories.find { it.name == categoryName }
    val location = category?.locations?.find { it.id == locationId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (location != null) {
            Text(
                text = location.name,
                style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = location.description,
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Address: " + location.address,
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
            )
        } else {
            Text(
                text = "Location not found",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
            )
        }
    }
}








