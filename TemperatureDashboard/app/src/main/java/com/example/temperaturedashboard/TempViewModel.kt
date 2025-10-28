package com.example.temperaturedashboard

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope // Provides a CoroutineScope tied to the ViewModel's lifecycle
import kotlinx.coroutines.delay // Suspending function to pause coroutine execution
import kotlinx.coroutines.flow.Flow // Base type for asynchronous data streams
import kotlinx.coroutines.flow.MutableSharedFlow // A SharedFlow that allows emitting values
import kotlinx.coroutines.flow.MutableStateFlow // A StateFlow that allows updating its value
import kotlinx.coroutines.flow.StateFlow // A flow that represents a read-only state
import kotlinx.coroutines.flow.asSharedFlow // Converts MutableSharedFlow to an immutable SharedFlow
import kotlinx.coroutines.flow.asStateFlow // Converts MutableStateFlow to an immutable StateFlow
import kotlinx.coroutines.flow.flow // Builder for creating cold Flows
import kotlinx.coroutines.launch // Launches a new coroutine without blocking the current thread
import java.util.Date
import kotlin.random.Random

class MainViewModel : ViewModel() {
    private val _eventFlow = MutableStateFlow<List<Int>>(emptyList())
    val eventFlow: StateFlow<List<Int>> = _eventFlow.asStateFlow()

    private val _timestampFlow = MutableStateFlow<List<String>>(emptyList())
    val timestampFlow: StateFlow<List<String>> = _timestampFlow.asStateFlow()

    private val _flowBool = MutableStateFlow(true)
    var flowBool: StateFlow<Boolean> = _flowBool.asStateFlow()

    fun toggleFlow() {
        _flowBool.value = !_flowBool.value
    }

    @SuppressLint("SimpleDateFormat")
    fun recordTemp() {
        viewModelScope.launch {
            while (true) {
                if (_flowBool.value) {
                    val randomValue = Random.nextInt(65, 85)
                    _eventFlow.value = (_eventFlow.value + randomValue).takeLast(20)

                    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                    val currentDate = sdf.format(Date())
                    _timestampFlow.value = (_timestampFlow.value + currentDate).takeLast(20)
                    if (_eventFlow.value.size>10) {
                        delay(2000)
                    }
                } else {
                    delay(100)
                }

            }
        }
    }
}