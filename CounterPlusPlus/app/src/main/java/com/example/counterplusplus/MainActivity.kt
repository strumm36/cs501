package com.example.counterplusplus

import CounterViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.counterplusplus.ui.theme.CounterPlusPlusTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CounterPlusPlusTheme {
                var screen by rememberSaveable {mutableStateOf(0)}
                val viewModel: CounterViewModel = viewModel()
                val coroutineScope = rememberCoroutineScope()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (screen == 0) {
                        Column{
                            CounterScreen(viewModel)
                            Button(onClick={
                                screen = 1}
                            ) {
                                Text("Settings")
                            }
                        }
                    } else {
                        Column{
                            SettingsScreen(viewModel, coroutineScope)
                            Button(onClick={
                                screen = 0}
                            ) {
                                Text("Back")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CounterScreen(viewModel: CounterViewModel) {
    Counter( { viewModel.increment() }, {viewModel.decrement()}, {viewModel.reset()}, viewModel.autoModeOn, {viewModel.toggleAutoMode()}, viewModel)
}
@Composable
fun Counter(onIncrement: () -> Unit, onDecrement: () -> Unit, onReset: () -> Unit, autoModeOn: Boolean, toggleAutoMode: () -> Unit, viewModel: CounterViewModel) {
    val coroutineScope = rememberCoroutineScope()
    Column {
        Row {
            Button(onClick = onIncrement) {
                Text("+1")
            }
            Button(onClick = onDecrement) {
                Text("-1")
            }
            Button(onClick = onReset) {
                Text("Reset")
            }
        }
        var current = viewModel.count
        Text("$current")
        if (viewModel.autoModeOn) {
            Text("Auto mode: ON")
        } else {
            Text("Auto mode: OFF")
        }
    }
}

@Composable
fun SettingsScreen(viewModel: CounterViewModel, coroutineScope: CoroutineScope) {
    Button(onClick = {
        viewModel.toggleAutoMode()
        if (viewModel.autoModeOn) {
            coroutineScope.launch {
                while (viewModel.autoModeOn) {
                    viewModel.autoMode()
                }
            }
        }
    }) {
        Text("Toggle Auto")
    }
    var current = viewModel.count
    Text("$current")
    if (viewModel.autoModeOn) {
        Text("Auto mode: ON")
    } else {
        Text("Auto mode: OFF")
    }
}