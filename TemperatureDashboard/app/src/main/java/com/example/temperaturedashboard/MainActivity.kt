package com.example.temperaturedashboard

import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.temperaturedashboard.ui.theme.TemperatureDashboardTheme
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.ui.semantics.Role.Companion.Button
import kotlin.getValue

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TemperatureDashboardTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    viewModel.recordTemp()
                    TemperatureList(viewModel)
                }
            }
        }
    }
}

@Composable
fun TemperatureList(viewModel: MainViewModel) {
    val temperatures by viewModel.eventFlow.collectAsState()
    val timestamps by viewModel.timestampFlow.collectAsState()
    val flowBool by viewModel.flowBool.collectAsState()

    Scaffold() { innerPadding ->
        Column(Modifier.padding(innerPadding)){
            Column {
                for (i in temperatures.indices) {
                    Text(text = "${temperatures[i]}°F — ${timestamps[i]}")
                }
            }

            Button(onClick={
                viewModel.toggleFlow()
            }){
                if (flowBool) {
                    Text("Pause")
                } else {
                    Text("Resume")
                }

            }

            val maxTemp = temperatures.maxOrNull()
            val minTemp = temperatures.minOrNull()
            val avgTemp = temperatures.average()
            Text("Maximum: $maxTemp")
            Text("Minimum: $minTemp")
            Text("Average: $avgTemp")
        }

    }
}