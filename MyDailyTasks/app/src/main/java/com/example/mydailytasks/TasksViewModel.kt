package com.example.mydailytasks

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

data class Task(
    val description: String,
    var isDone: Boolean = false
)

class TasksViewModel : ViewModel() {
    var notes = mutableStateListOf<String>()
        private set

    var tasks = mutableStateListOf<Task>()
        private set

    fun addNote(note: String) {
        notes.add(note)
    }

    fun addTask(description: String) {
        tasks.add(Task(description))
    }

    init {
        // Sample notes
        notes.addAll(
            listOf(
                "Test note A",
                "Test note B",
                "Test note C"
            )
        )

        // Sample tasks
        tasks.addAll(
            listOf(
                Task("Test task A"),
                Task("Test task B"),
                Task("Test task C")
            )
        )
    }

    fun toggleTask(index: Int) {
        if (index in tasks.indices) {
            val current = tasks[index]
            tasks[index] = current.copy(isDone = !current.isDone)
        }
    }
}
