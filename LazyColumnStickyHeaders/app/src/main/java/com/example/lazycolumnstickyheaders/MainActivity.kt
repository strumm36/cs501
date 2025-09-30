package com.example.lazycolumnstickyheaders

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lazycolumnstickyheaders.ui.theme.LazyColumnStickyHeadersTheme
import kotlinx.coroutines.launch

data class Contacts(val letter: String, val names: List<String>)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LazyColumnStickyHeadersTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ContactsList()
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContactsList(modifier: Modifier = Modifier) {

    val lazyListState = rememberLazyListState()

    val coroutineScope = rememberCoroutineScope()

    val showFab by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 10
        }
    }

    val contactsByLetter = remember {
        listOf(
            Contacts(
                "A", listOf(
                    "Abigail", "Adam", "Aiden", "Amelia", "Andrew", "Anna", "Anthony", "Ava"
                )
            ),
            Contacts(
                "B", listOf(
                    "Benjamin"
                )
            ),
            Contacts(
                "C", listOf(
                    "Caleb", "Cameron", "Charlotte", "Chloe"
                )
            ),
            Contacts(
                "D", listOf(
                    "Daniel", "David", "Dylan"
                )
            ),
            Contacts(
                "E", listOf(
                    "Ella", "Emily", "Ethan", "Evelyn"
                )
            ),
            Contacts(
                "G", listOf(
                    "Gabriel", "Grace"
                )
            ),
            Contacts(
                "H", listOf(
                    "Hannah", "Harper", "Henry"
                )
            ),
            Contacts(
                "I", listOf(
                    "Isabella"
                )
            ),
            Contacts(
                "J", listOf(
                    "Jack", "Jackson", "James", "Jayden", "Joseph"
                )
            ),
            Contacts(
                "L", listOf(
                    "Layla", "Leo", "Liam", "Lily", "Logan", "Lucas", "Lucy"
                )
            ),
            Contacts(
                "M", listOf(
                    "Mason", "Mia", "Michael"
                )
            ),
            Contacts(
                "N", listOf(
                    "Noah"
                )
            ),
            Contacts(
                "O", listOf(
                    "Oliver", "Olivia", "Owen"
                )
            ),
            Contacts(
                "S", listOf(
                    "Samuel", "Scarlett", "Sophia"
                )
            ),
            Contacts(
                "W", listOf(
                    "William"
                )
            ),
            Contacts(
                "Z", listOf(
                    "Zoe"
                )
            )
        )
    }
    Column(
        modifier = modifier.padding(horizontal = 8.dp), // Horizontal padding for the whole screen
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Scaffold(
            floatingActionButton = {
                AnimatedVisibility(
                    visible = showFab,
                ) {
                    FloatingActionButton(onClick = {
                        coroutineScope.launch {
                            lazyListState.animateScrollToItem(index = 0)
                        }
                    }) {
                        Text("Scroll to Top")
                    }
                }
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = lazyListState
            ) {
                contactsByLetter.forEach { section ->
                    stickyHeader(key = section.letter) {
                        Text(
                            section.letter,
                            Modifier
                                .background(Color.LightGray)
                                .fillMaxWidth()
                                .padding(5.dp),
                            fontSize = 20.sp
                        )
                    }

                    items(
                        items = section.names,
                        key = { name -> name }
                    ) { name ->
                        Text(name)
                    }
                }
            }
        }
    }
}
