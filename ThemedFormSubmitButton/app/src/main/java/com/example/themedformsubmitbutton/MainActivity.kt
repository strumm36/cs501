package com.example.themedformsubmitbutton

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.themedformsubmitbutton.ui.theme.ThemedFormSubmitButtonTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ThemedFormSubmitButtonTheme {
                val scope = rememberCoroutineScope()
                val snackbarHostState = remember { SnackbarHostState() }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState)
                    },
                ) { innerPadding ->
                    var text by rememberSaveable() { mutableStateOf("") }
                    var text2 by rememberSaveable() { mutableStateOf("") }
                    Column(
                        Modifier.padding(16.dp),

                    ) {
                        TextField(
                            value = text,
                            onValueChange = { newText:String -> text = newText },
                            label = { Text("Username", style = MaterialTheme.typography.titleSmall) },
                            modifier = Modifier.padding(10.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.inversePrimary
                            )
                        )
                        TextField(
                            value = text2,
                            onValueChange = { newText:String -> text2 = newText },
                            label = { Text("Password", style = MaterialTheme.typography.titleSmall) },
                            modifier = Modifier.padding(10.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.inversePrimary
                            )
                        )
                        Button(
                            onClick = {
                                if (text.isEmpty() || text2.isEmpty()) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Empty text field, submission failed")
                                    }
                                } else {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Form submitted, no fields are empty")
                                    }
                                    text = ""
                                    text2 = ""
                                }
                            },
                        ) {
                            Text("Submit")
                        }
                    }
                }
            }
        }
    }
}


