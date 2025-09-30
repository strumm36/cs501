package com.example.scaffoldtopbarbottombarfab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scaffoldtopbarbottombarfab.ui.theme.ScaffoldTopBarBottomBarFABTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScaffoldTopBarBottomBarFABTheme {
                val scope = rememberCoroutineScope()
                val snackbarHostState = remember { SnackbarHostState() }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {Text("App Title")},
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer, // Background color.
                                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer, // Color for the title.
                            )
                        ) },
                    bottomBar = {
                        BottomAppBar(
                            containerColor = MaterialTheme.colorScheme.primaryContainer, // Background color.
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer, // Default color for content (icons).
                            actions = {
                                IconButton(onClick = { /*  */ }) {
                                    Icon(
                                        Icons.Filled.Home,
                                        contentDescription = "Home",
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                                IconButton(onClick = { /*  */ }) {
                                    Icon(
                                        Icons.Filled.Settings,
                                        contentDescription = "Settings",
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                                IconButton(onClick = { /*  */ }) {
                                    Icon(
                                        Icons.Filled.Person,
                                        contentDescription = "Profile",
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            },
                        )
                    },
                    snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState)
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = { scope.launch {
                                snackbarHostState.showSnackbar("Snackbar message")
                            } },
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        ) {
                            Text("FAB")
                        }
                    },
                ) { innerPadding ->
                }
            }
        }
    }
}