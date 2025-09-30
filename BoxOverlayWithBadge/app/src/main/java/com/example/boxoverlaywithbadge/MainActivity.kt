package com.example.boxoverlaywithbadge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.boxoverlaywithbadge.ui.theme.BoxOverlayWithBadgeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BoxOverlayWithBadgeTheme {
                Scaffold(modifier = Modifier.fillMaxSize().padding(30.dp)) { innerPadding ->
                    val badgeVisible = rememberSaveable {mutableStateOf(false)}
                    Button ({
                        badgeVisible.value = badgeVisible.value == false
                        }
                    ) {
                        Text("Toggle Badge")
                    }
                    Box (modifier = Modifier.fillMaxSize()) {
                        if (badgeVisible.value) {
                            NotificationBadge(
                                modifier = Modifier.align(Alignment.BottomEnd)
                            )
                        }

                    }
                }
            }
        }
    }
}

@Composable
fun NotificationBadge(modifier:Modifier) {
    Box(modifier = modifier) {
        Icon(
            modifier = Modifier.size(50.dp),
            imageVector = Icons.Filled.Notifications,
            contentDescription = "Notification Icon"
        )
        Badge() {
            Text(text = "1")
        }
    }
}