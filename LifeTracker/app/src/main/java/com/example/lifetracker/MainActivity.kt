package com.example.lifetracker

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lifetracker.ui.theme.LifeTrackerTheme
import android.util.Log
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    private val viewModel: LifeTrackerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            viewModel.addEvent("onCreate")
            LifeTrackerTheme {
                var screen by rememberSaveable {mutableStateOf(0)}
                var snackbarEnabled by rememberSaveable {mutableStateOf(true)}
                val scope = rememberCoroutineScope()
                val snackbarHostState = remember { SnackbarHostState() }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState)
                    }) { innerPadding ->
                    if (screen == 0) {
                        Column (Modifier.padding(innerPadding)) {
                            Button({ screen = 1 }) {
                                Text("Settings")
                            }
                            LazyColumn() {
                                items(viewModel.listItems) { item ->
                                        if ("onCreate" in item) {
                                            Text(item, color = Color.Green)
                                            LaunchedEffect(item) {
                                                if (snackbarEnabled) {
                                                    scope.launch {
                                                        snackbarHostState.showSnackbar(item)
                                                    }
                                                }
                                            }
                                        } else if ("onStart" in item) {
                                            Text(item, color = Color.Blue)
                                            LaunchedEffect(item) {
                                                if (snackbarEnabled) {
                                                    scope.launch {
                                                        snackbarHostState.showSnackbar(item)
                                                    }
                                                }
                                            }
                                        } else if ("onResume" in item) {
                                            Text(item, color = Color.Magenta)
                                            LaunchedEffect(item) {
                                                if (snackbarEnabled) {
                                                    scope.launch {
                                                        snackbarHostState.showSnackbar(item)
                                                    }
                                                }
                                            }
                                        } else if ("onPause" in item) {
                                            Text(item, color = Color.Gray)
                                            LaunchedEffect(item) {
                                                if (snackbarEnabled) {
                                                    scope.launch {
                                                        snackbarHostState.showSnackbar(item)
                                                    }
                                                }
                                            }
                                        } else if ("onStop" in item) {
                                            Text(item, color = Color.Red)
                                            LaunchedEffect(item) {
                                                if (snackbarEnabled) {
                                                    scope.launch {
                                                        snackbarHostState.showSnackbar(item)
                                                    }
                                                }
                                            }
                                        } else if ("onDestroy" in item) {
                                            Text(item, color = Color.Black)
                                            LaunchedEffect(item) {
                                                if (snackbarEnabled) {
                                                    scope.launch {
                                                        snackbarHostState.showSnackbar(item)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                    } else {
                        Column (Modifier.padding(innerPadding)) {
                            Button({ screen = 0 }) {
                                Text("Back")
                            }
                            if (snackbarEnabled) {
                                Button({ snackbarEnabled = false }) {
                                    Text( "Disable Snackbar")
                                }
                            } else {
                                Button({ snackbarEnabled = true }) {
                                    Text( "Enable Snackbar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    override fun onStart() {
        super.onStart()
        viewModel.addEvent("onStart")
        Log.i("INFO","onStart")
    }

    override fun onResume() {
        super.onResume()
        viewModel.addEvent("onResume")
        Log.i("INFO","onResume")
    }

    override fun onPause() {
        super.onPause()
        viewModel.addEvent("onPause")
        Log.i("INFO","onPause")
    }

    override fun onStop() {
        super.onStop()
        viewModel.addEvent("onStop")
        Log.i("INFO","onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.addEvent("onDestroy")
        Log.i("INFO","onDestroy")
    }
}
