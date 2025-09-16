package com.example.togglecard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.togglecard.ui.theme.ToggleCardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToggleCardTheme {
                Column{
                    ToggleCard("Toggle Card State 1",
                        "State 2 Toggle Card",
                        Modifier.size(width = 200.dp, height = 200.dp).padding(30.dp)
                    )
                    Text("Clickable Toggle Card", modifier=Modifier.padding(30.dp))
                }
            }
        }
    }
}

@Composable
fun ToggleCard(label1 : String, label2 : String, modifier : Modifier) {
    val cT = rememberSaveable {mutableStateOf(true)}
    Card(modifier=modifier.clickable{
        if (cT.value) {
            cT.value = false
        } else {
            cT.value = true
        }
    }) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (cT.value) {
                Text(text = label1, textAlign = TextAlign.Center)
            } else {
                Text(text = label2, textAlign = TextAlign.Center)
            }
        }
    }
}