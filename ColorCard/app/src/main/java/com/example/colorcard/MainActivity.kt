package com.example.colorcard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.colorcard.ui.theme.ColorCardTheme
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ColorCardTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column {
                        ColorCard(
                            color = Color.Red,
                            label = "Card 1",
                            modifier = Modifier.padding(innerPadding)
                                .size(width = 240.dp, height = 100.dp)
                        )
                        ColorCard(
                            color = Color.Transparent,
                            label = "Card 2",
                            modifier = Modifier.padding(innerPadding)
                                .size(width = 100.dp, height = 300.dp)
                                .border(4.dp, Color.Black, CircleShape)
                                .background(Color.LightGray, CircleShape)
                        )
                        ColorCard(
                            color = Color.Cyan,
                            label = "Card 3",
                            modifier = Modifier.padding(innerPadding)
                                .size(width = 100.dp, height = 100.dp)
                                .border(2.dp, Color.Black, RectangleShape)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ColorCard(color: Color, label: String, modifier: Modifier) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = color
        ),
        modifier=modifier.background(color=color))
    {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = label, textAlign = TextAlign.Center)
        }
    }
}