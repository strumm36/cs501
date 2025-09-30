package com.example.rowcolumnweightsplitlayout

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.rowcolumnweightsplitlayout.ui.theme.RowColumnWeightSplitLayoutTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RowColumnWeightSplitLayoutTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    innerPadding ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(innerPadding)
                    ){
                        Text("25%", modifier = Modifier.weight(2.5f).background(Color.LightGray).height(200.dp))
                        Column(
                            modifier = Modifier.weight(7.5f).height(200.dp)
                        ) {
                            Text("2", modifier = Modifier.weight(2f).background(Color.Red).fillMaxWidth())
                            Text("3", modifier = Modifier.weight(3f).background(Color.Yellow).fillMaxWidth())
                            Text("5", modifier = Modifier.weight(5f).background(Color.Cyan).fillMaxWidth())
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RowColumnWeightSplitLayoutTheme {
        Greeting("Android")
    }
}