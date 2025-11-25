package com.example.compassdigitallevel

import android.annotation.SuppressLint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.Choreographer
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compassdigitallevel.ui.theme.CompassDigitalLevelTheme
import kotlin.math.cos
import kotlin.math.sin
import androidx.core.graphics.withSave
import kotlin.math.atan2
import kotlin.math.sqrt

class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var magnetometer: Sensor? = null
    private var gyroscope: Sensor? = null


    private var _acc1 by mutableStateOf(0f)
    private var _acc2 by mutableStateOf(0f)
    private var _acc3 by mutableStateOf(0f)

    private var _gyro1 by mutableStateOf(0f)
    private var _gyro2 by mutableStateOf(0f)
    private var _gyro3 by mutableStateOf(0f)

    private var _mag1 by mutableStateOf(0f)
    private var _mag2 by mutableStateOf(0f)
    private var _mag3 by mutableStateOf(0f)


    private var _accuracy by mutableStateOf("Unknown")

    private var lastFrameTime = 0L
    private val dtState = mutableStateOf(0f)

    private fun startDTLoop() {
        Choreographer.getInstance().postFrameCallback { timeNanos ->

            if (lastFrameTime != 0L) {
                val dt = (timeNanos - lastFrameTime) / 1_000_000_000f
                dtState.value = dt
            }

            lastFrameTime = timeNanos

            // keep looping every frame
            startDTLoop()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Sensor Manager
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        enableEdgeToEdge()
        setContent {
            CompassDigitalLevelTheme {
                Scaffold(modifier = Modifier.fillMaxSize(), containerColor = Color.hsv(60f, 0.2f, 1f)) { innerPadding ->

                    // Rotation matrix
                    val rotationMatrix = FloatArray(9)

                    // Calculation
                    SensorManager.getRotationMatrix(
                        rotationMatrix,
                        null,
                        floatArrayOf(_acc1, _acc2, _acc3),
                        floatArrayOf(_mag1, _mag2, _mag3)
                    )

                    val orientationAngles = FloatArray(3)

                    SensorManager.getOrientation(
                        rotationMatrix,
                        orientationAngles
                    )

                    val azimuthRadians = orientationAngles[0]

                    val azimuthDegrees = Math.toDegrees(azimuthRadians.toDouble()).toFloat()



                    Column (
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Compass(azimuthDegrees)
                        DigitalLevel(_acc1, _acc2, _acc3, _gyro1, _gyro2, _gyro3, dtState.value)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.let() {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        magnetometer?.let() {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
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
            if (event.sensor == accelerometer) {
                _acc1 = it.values[0]
                _acc2 = it.values[1]
                _acc3 = it.values[2]
            } else if (event.sensor == magnetometer) {
                _mag1 = it.values[0]
                _mag2 = it.values[1]
                _mag3 = it.values[2]
            } else if (event.sensor == gyroscope) {
                _gyro1 = it.values[0]
                _gyro2 = it.values[1]
                _gyro3 = it.values[2]
            }
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

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun Compass(angle: Float = 0f) {
    val configuration = LocalConfiguration.current
    val screenHeight = ((configuration.screenHeightDp)/2.5).dp
    Canvas(modifier = Modifier.size(screenHeight)) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = minOf(centerX, centerY) * 0.8f

        // Filled circle
        drawCircle(
            color = Color.White,
            center = Offset(centerX, centerY),
            radius = radius
        )

        // Circle outline
        drawCircle(
            color = Color.DarkGray,
            center = Offset(centerX, centerY),
            radius = radius,
            style = Stroke(width = 4.dp.toPx())
        )

        val textPaint = Paint().asFrameworkPaint().apply {
            isAntiAlias = true
            textSize = 24.sp.toPx()
            color = android.graphics.Color.BLACK
            textAlign = android.graphics.Paint.Align.CENTER
        }

        drawContext.canvas.nativeCanvas.apply {
            withSave {
                translate(centerX, centerY)
                rotate(angle)

                val textDist = radius * 0.9f
                val textOffset = textPaint.textSize / 3f

                // North
                drawText("N", 0f, -textDist + textOffset, textPaint)
                // East
                drawText("E", textDist - textOffset, textOffset, textPaint)
                // South
                drawText("S", 0f, textDist + textOffset, textPaint)
                // West
                drawText("W", -textDist + textOffset, textOffset, textPaint)
            }
        }

        // Tick lines
        val tickPaint = Paint().asFrameworkPaint().apply {
            isAntiAlias = true
            strokeWidth = 3f
            color = android.graphics.Color.BLACK
            style = android.graphics.Paint.Style.STROKE
        }

        drawContext.canvas.nativeCanvas.apply {
            withSave {
                translate(centerX, centerY)
                rotate(angle)

                val outer = radius
                val len = 20f

                for (deg in 0 until 360 step 15) {
                    val angleRad = Math.toRadians(deg.toDouble())

                    val sin = sin(angleRad).toFloat()
                    val cos = cos(angleRad).toFloat()

                    // Outer end
                    val xOuter = cos * outer
                    val yOuter = sin * outer

                    // Inner end
                    val xInner = cos * (outer - len)
                    val yInner = sin * (outer - len)

                    drawLine(xInner, yInner, xOuter, yOuter, tickPaint)
                }

            }
        }

        // Pointer
        val pointerLength = radius * 0.7f
        val pointerWidth = 8.dp.toPx()

        drawContext.canvas.nativeCanvas.apply {
            withSave {
                translate(centerX, centerY)
                drawLine(
                    color = Color.Red,
                    start = Offset(0f, -pointerLength),
                    end = Offset(0f, 0f),
                    strokeWidth = pointerWidth
                )
            }
        }
    }
}

@Composable
fun DigitalLevel(
    ax: Float, ay: Float, az: Float,
    gx: Float, gy: Float, gz: Float,
    dt: Float,
    alpha: Float = 0.98f
) {

    var pitch: Float by remember { mutableStateOf(0f) }
    var roll: Float by remember { mutableStateOf(0f) }
    var initialized by remember { mutableStateOf(false) }

    LaunchedEffect(ax, ay, az, gx, gy, gz, dt) {

        if (!initialized) {
            pitch = Math.toDegrees(
                atan2(-ax, sqrt(ay * ay + az * az)).toDouble()
            ).toFloat()

            roll = Math.toDegrees(
                atan2(ay, az).toDouble()
            ).toFloat()

            initialized = true
            return@LaunchedEffect
        }

        val gyroPitch = pitch + gx * dt * 180f / Math.PI.toFloat()
        val gyroRoll  = roll  + gy * dt * 180f / Math.PI.toFloat()

        val accelPitch = Math.toDegrees(
            atan2(-ax, sqrt(ay * ay + az * az)).toDouble()
        )

        val accelRoll = Math.toDegrees(
            atan2(ay, az).toDouble()
        )

        pitch = (alpha * gyroPitch + (1f - alpha) * accelPitch).toFloat()
        roll  = (alpha * gyroRoll  + (1f - alpha) * accelRoll).toFloat()
    }

    Column{
        Text(text = "Pitch: " + pitch.toString(), fontSize = 25.sp)
        Text(text = "Roll: " + roll.toString(), fontSize = 25.sp)
    }
}