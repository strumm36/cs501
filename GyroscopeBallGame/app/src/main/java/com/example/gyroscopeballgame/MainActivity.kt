package com.example.gyroscopeballgame

import android.R.attr.textSize
import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview
import com.example.gyroscopeballgame.ui.theme.GyroscopeBallGameTheme
import kotlin.math.abs
import kotlin.math.sqrt

class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var gyroscope: Sensor? = null
    private var _accuracy by mutableStateOf("Unknown")

    private var _g1 by mutableStateOf(0f)
    private var _g2 by mutableStateOf(0f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        enableEdgeToEdge()
        setContent {
            GyroscopeBallGameTheme {
                GyroBallGameScreen(
                    gx = _g1,
                    gy = _g2
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        gyroscope?.let() {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            _g1 = it.values[0]
            _g2 = it.values[1]
            // Z not needed for this game
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        _accuracy = when (accuracy) {
            SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> "High"
            SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> "Medium"
            SensorManager.SENSOR_STATUS_ACCURACY_LOW -> "Low"
            SensorManager.SENSOR_STATUS_UNRELIABLE -> "Unreliable"
            else -> "Unknown"
        }
    }
}

data class Rect(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
) {
    val width get() = right - left
    val height get() = bottom - top
}

@Composable
fun GyroBallGameScreen(gx: Float, gy: Float) {
    // Ball state
    var x by rememberSaveable { mutableStateOf(100f) }
    var y by rememberSaveable { mutableStateOf(100f) }
    var vx by rememberSaveable { mutableStateOf(0f) }
    var vy by rememberSaveable { mutableStateOf(0f) }

    val ballRadius = 40f
    val friction = 0.98f
    val tiltMultiplier = 30f // stronger = faster

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Canvas(modifier = Modifier.fillMaxSize()) {

            // Obstacles
            val obstacles = listOf(
                Rect(0f, 200f, 300f, 220f),
                Rect(600f, 0f, 620f, 500f),
                Rect(0f, 800f, 600f, 820f),
                Rect(300f, 500f, 620f, 520f),
                Rect(300f, 1100f, 1100f, 1120f),
                Rect(0f, 1500f, 600f, 1520f),
                Rect(300f, 1800f, 1100f, 1820f),
            )

            // Bounds
            drawRect(
                color = Color.Black,
                topLeft = Offset.Zero,
                size = size,
                style = Stroke(width = 20f)
            )


            // Physics
            val ax = gy * tiltMultiplier
            val ay = gx * tiltMultiplier

            vx += ax
            vy += ay

            x += vx * (1 / 60f)
            y += vy * (1 / 60f)

            val tiltMagnitude = sqrt(ax * ax + ay * ay)

            if (tiltMagnitude < 0.5f) {
                vx *= friction
                vy *= friction
            }


            // Boundary collisions
            if (x - ballRadius < 20) {
                x = 20 + ballRadius
                vx = -vx * 0.4f
            }
            if (x + ballRadius > size.width - 20) {
                x = size.width - 20 - ballRadius
                vx = -vx * 0.4f
            }
            if (y - ballRadius < 20) {
                y = 20 + ballRadius
                vy = -vy * 0.4f
            }
            if (y + ballRadius > size.height - 20) {
                y = size.height - 20 - ballRadius
                vy = -vy * 0.4f
            }

            // Obstacle collision
            obstacles.forEach { rect ->

                // Clamp ball center to rectangle bounds
                val nearestX = x.coerceIn(rect.left, rect.right)
                val nearestY = y.coerceIn(rect.top, rect.bottom)

                val distX = x - nearestX
                val distY = y - nearestY
                val distSq = distX * distX + distY * distY

                if (distSq < ballRadius * ballRadius) {
                    // collision happened → push ball out
                    if (abs(distX) > abs(distY)) {
                        if (distX > 0) x = rect.right + ballRadius
                        else x = rect.left - ballRadius
                        vx = -vx * 0.4f
                    } else {
                        if (distY > 0) y = rect.bottom + ballRadius
                        else y = rect.top - ballRadius
                        vy = -vy * 0.4f
                    }
                }
            }

            // Draw obstacles
            obstacles.forEach { rect ->
                drawRect(
                    color = Color.DarkGray,
                    topLeft = Offset(rect.left, rect.top),
                    size = androidx.compose.ui.geometry.Size(
                        rect.width,
                        rect.height
                    )
                )
            }

            // Draw player
            drawCircle(
                color = Color.Red,
                center = Offset(x, y),
                radius = ballRadius
            )
        }

        // Keep recomposing at ~60 FPS
        LaunchedEffect(Unit) {
            while (true) {
                withFrameNanos { }
            }
        }
    }
}