package com.example.polylinepolygon

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.LatLng
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.polylinepolygon.ui.theme.PolylinePolygonTheme
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            enableEdgeToEdge()
            PolylinePolygonTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TrailMap()
                }
            }
        }
    }
}

@Composable
fun TrailMap() {
    val context = LocalContext.current

    // Polygon: Central Park's Reservoir
    val centralParkBounds = listOf(
        LatLng(40.785077, -73.966792),
        LatLng(40.783777, -73.963960),
        LatLng(40.782045, -73.961333),
        LatLng(40.786607, -73.957722),
        LatLng(40.788921, -73.957926),
        LatLng(40.788122, -73.965899),
    )

    // Polyline: Random path through the park
    val trailPath = listOf(
        LatLng(40.785252, -73.969326),
        LatLng(40.782584, -73.966487),
        LatLng(40.780167, -73.964559),
        LatLng(40.778049, -73.964725),
        LatLng(40.777217, -73.963647),
    )

    var polylineColor by remember { mutableStateOf(Color.Magenta) }
    var polylineWidth by remember { mutableFloatStateOf(10f) }

    var polygonColor by remember { mutableStateOf(Color.DarkGray.copy(alpha = 0.5f)) }
    var polygonStrokeColor by remember { mutableStateOf(Color.DarkGray) }
    var polygonStrokeWidth by remember { mutableFloatStateOf(5f) }

    val trailColors = listOf(Color.Blue, Color.Red, Color.Green, Color.DarkGray, Color.Magenta)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(40.785091, -73.968285), 14f) // Center on Central Park
    }

    Box(modifier = Modifier.fillMaxSize()) {

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = false)
        ) {
            Polyline(
                points = trailPath,
                color = polylineColor,
                width = polylineWidth,
                clickable = true,
                onClick = {
                    Toast.makeText(
                        context,
                        "This is a random trail through Central Park, one of many to traverse it.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )

            Polygon(
                points = centralParkBounds,
                fillColor = polygonColor,
                strokeColor = polygonStrokeColor,
                strokeWidth = polygonStrokeWidth,
                clickable = true,
                onClick = {
                    Toast.makeText(
                        context,
                        "This is the Jackie Kennedy Reservoir, with nice views and a gravel path.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
        }

        // User customization options
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.7f), shape = RoundedCornerShape(8.dp))
                .padding(16.dp)
                .align(Alignment.TopCenter)
        ) {
            Text("Customize Polyline & Polygon", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Polyline Width:")
                Slider(
                    value = polylineWidth,
                    onValueChange = { polylineWidth = it },
                    valueRange = 1f..20f,
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                )
                Text("${polylineWidth.toInt()} px")
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Polyline Color: ")
                var currentColorIndex by remember { mutableIntStateOf(0) }
                Box(
                    Modifier
                        .size(24.dp)
                        .background(polylineColor)
                        .clickable {
                            currentColorIndex = (currentColorIndex + 1) % trailColors.size
                            polylineColor = trailColors[currentColorIndex]
                        }
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Polygon Width:")
                Slider(
                    value = polygonStrokeWidth,
                    onValueChange = { polygonStrokeWidth = it },
                    valueRange = 1f..20f,
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                )
                Text("${polygonStrokeWidth.toInt()} px")
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Polygon Color: ")
                var currentColorIndex by remember { mutableIntStateOf(0) }
                Box(
                    Modifier
                        .size(24.dp)
                        .background(polygonStrokeColor)
                        .clickable {
                            currentColorIndex = (currentColorIndex + 1) % trailColors.size
                            polygonColor = trailColors[currentColorIndex].copy(alpha = 0.5f)
                            polygonStrokeColor = trailColors[currentColorIndex]
                        }
                )
            }
        }
    }
}