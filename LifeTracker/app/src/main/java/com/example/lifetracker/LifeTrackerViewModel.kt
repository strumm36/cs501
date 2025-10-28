package com.example.lifetracker
import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import java.util.Date


class LifeTrackerViewModel : ViewModel() {
    var listItems = mutableStateListOf<String>()

    @SuppressLint("SimpleDateFormat")
    fun addEvent (s: String) {
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())
        listItems.add("$currentDate: $s")
    }
}