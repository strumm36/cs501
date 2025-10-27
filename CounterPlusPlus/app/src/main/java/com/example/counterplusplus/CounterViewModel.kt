import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay

class CounterViewModel : ViewModel() {
    var count by mutableStateOf(0)
        private set
    var autoModeOn by mutableStateOf(false)
    fun increment() {
        count++
    }
    fun decrement() {
        count--
    }
    fun reset() {
        count = 0
    }

    fun toggleAutoMode() {
        autoModeOn = !autoModeOn
    }

    suspend fun autoMode() {
        count++
        delay(3000)
    }
}