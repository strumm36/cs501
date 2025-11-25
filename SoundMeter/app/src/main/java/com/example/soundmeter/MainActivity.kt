package com.example.soundmeter

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.soundmeter.ui.theme.SoundMeterTheme
import kotlinx.coroutines.delay
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.sqrt


class MainActivity : ComponentActivity() {

    private var audioRecord: AudioRecord? = null
    private val sampleRate = 44100
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val bufferSize by lazy {
        AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
    }

    private val audioPermission = Manifest.permission.RECORD_AUDIO

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (checkSelfPermission(audioPermission) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(audioPermission), 101)
            return
        }

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            bufferSize
        )

        enableEdgeToEdge()

        setContent {
            SoundMeterTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    SoundMeterScreen(
                        modifier = Modifier.padding(innerPadding),
                        audioRecord = audioRecord!!,
                        bufferSize = bufferSize
                    )
                }
            }
        }
    }
}

@Composable
fun SoundMeterScreen(
    modifier: Modifier = Modifier,
    audioRecord: AudioRecord,
    bufferSize: Int
) {
    var decibel: Double by remember { mutableStateOf(0.0) }

    val threshold = 70f

    LaunchedEffect(Unit) {
        val buffer = ShortArray(bufferSize)

        audioRecord.startRecording()

        while (true) {
            val read = audioRecord.read(buffer, 0, buffer.size)
            if (read > 0) {
                var sum = 0.0
                for (i in 0 until read) sum += buffer[i] * buffer[i]

                val rms = sqrt(sum / read).toFloat().coerceAtLeast(1f) // <- prevents log10(0)
                val db = 20 * log10(rms / Short.MAX_VALUE.toFloat())
                val dbUI = (db + 60).coerceIn(0f, 100f)*(100f/60f)

                decibel = dbUI.toDouble()
            }

            delay(66) // ~15 fps for enough delay but smooth experience
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Sound Level: " + floor(decibel * 100) / 100 + " dB",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Progress bar
        LinearProgressIndicator(
            progress = { (decibel / 100).toFloat().coerceIn(0f, 1f) },
            modifier = Modifier
                            .fillMaxWidth()
                            .height(30.dp)
                            .clip(RoundedCornerShape(10.dp)),
            color = if (decibel > threshold) Color.Red else Color.Green,
            trackColor = ProgressIndicatorDefaults.linearTrackColor,
            strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Alert
        if (decibel > threshold) {
            Text(
                "Input above 70dB threshold",
                color = Color.Red,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
