package com.example.mydailytasks

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mydailytasks.ui.theme.MyDailyTasksTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyDailyTasksTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen()
                }
            }
        }
    }
}

/**
 * A sealed class to define the screens in our app. This is a best practice
 * for type-safe navigation and for keeping all screen-related information
 * (route, title, icon) in one place.
 */
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Notes : Screen("notes", "Notes", Icons.Default.Edit)
    data object Tasks : Screen("tasks", "Tasks", Icons.Default.Check)
    data object Calendar : Screen("calendar", "Calendar", Icons.Default.DateRange)
}

// A list of all our screens to easily iterate over for the navigation bar.
val screens = listOf(
    Screen.Notes,
    Screen.Tasks,
    Screen.Calendar
)

/**
 * This is the main composable that sets up the UI, including the Scaffold,
 * NavHost, and the Bottom Navigation Bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val viewModel: TasksViewModel = viewModel()
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Daily Tasks") },
                navigationIcon = {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    Log.d("NavDebug", "Current route: $currentRoute")

                    if (currentRoute != Screen.Notes.route) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                }
            )

        },
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                screens.forEach { screen ->
                    NavigationBarItem(
                        label = { Text(screen.title) },
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            if (screen.route != navController.currentBackStackEntry?.destination?.route) {
                                navController.navigate(screen.route) {
                                    launchSingleTop = true
                                    if (screen.route == Screen.Notes.route) {
                                        popUpTo(Screen.Notes.route) { inclusive = false }
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Notes.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Notes.route) { NotesScreen(viewModel) }
            composable(Screen.Tasks.route) { TasksScreen(viewModel) }
            composable(Screen.Calendar.route) { GenericScreen(screen = Screen.Calendar) }
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
fun GenericScreen(screen: Screen) {
    // Box is a simple layout composable that can be used to stack or align its children.
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center // Center the content horizontally and vertically.
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = screen.icon, contentDescription = null, modifier = Modifier.padding(bottom = 8.dp))
            Text(text = screen.title, style = MaterialTheme.typography.headlineMedium)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(viewModel: TasksViewModel) {
    var newNote by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Notes") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Notes list at the top
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .weight(1f) // takes all available space
                    .fillMaxWidth()
            ) {
                items(viewModel.notes) { note ->
                    Text(note, style = MaterialTheme.typography.bodyLarge)
                }
            }

            Divider()

            // Add Note input below
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = newNote,
                    onValueChange = { newNote = it },
                    label = { Text("New Note") },
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {
                        if (newNote.isNotBlank()) {
                            viewModel.addNote(newNote.trim())
                            newNote = ""
                        }
                    }
                ) {
                    Text("Add")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(viewModel: TasksViewModel) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Tasks") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f).fillMaxWidth()
            ) {
                itemsIndexed(viewModel.tasks) { index, task ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = task.isDone,
                            onCheckedChange = { viewModel.toggleTask(index) }
                        )
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            var newTaskTitle by rememberSaveable { mutableStateOf("") }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = newTaskTitle,
                    onValueChange = { newTaskTitle = it },
                    label = { Text("New Task") },
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {
                        if (newTaskTitle.isNotBlank()) {
                            viewModel.addTask(newTaskTitle)
                            newTaskTitle = ""
                        }
                    }
                ) {
                    Text("Add")
                }
            }
        }
    }
}
