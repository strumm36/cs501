package com.example.kotlinpracticescreen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.kotlinpracticescreen.ui.theme.KotlinPracticeScreenTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KotlinPracticeScreenTheme {
                KotlinPracticeScreen()
            }
        }
    }
}

@Composable
fun ResultInputString(input: String) {
    when (input) {
        "cat" -> Text("Input was cat")
        "dog" -> Text("Input was dog")
        "fish" -> Text("Input was fish")
        else -> Text("Something else")
    }
}

@Composable
fun MessageNotNullString(input: String?) {
    if (input != null) {
        Text("String is not null!")
    }
}

@Composable
fun ButtonIncrementLess() {
    val counter = rememberSaveable {mutableStateOf(0)}
    Button(onClick = { if (counter.value < 5) {counter.value++} }) {
        Text(counter.value.toString())
    }
}

@Composable
fun KotlinPracticeScreen() {
    Column {
        ResultInputString("fish")
        MessageNotNullString("Test")
        ButtonIncrementLess()
    }
}