package com.example.locationinformation

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import com.example.locationinformation.ui.theme.LocationInformationTheme
import com.google.android.gms.maps.model.LatLng
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locationRequest = LocationRequest.Builder(5000L)
        .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
        .build()
    private val userLocationState = mutableStateOf<LatLng?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            enableEdgeToEdge()
            LocationInformationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Permission handling moved here
                    LocationPermissionWrapper(
                        userLocationState = userLocationState
                    )
                }
            }
        }
    }

    @Suppress("MissingPermission")
    fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    result.lastLocation?.let {
                        userLocationState.value = LatLng(it.latitude, it.longitude)
                    }
                }
            },
            mainLooper
        )
    }

    @Suppress("MissingPermission")
    fun getLastLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                userLocationState.value = LatLng(it.latitude, it.longitude)
            }
        }
    }
}

@Composable
fun LocationPermissionWrapper(
    userLocationState: MutableState<LatLng?>
) {
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            (context as MainActivity).startLocationUpdates()
            context.getLastLocation()
        }
    }

    // Request permission if not granted
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            (context as MainActivity).startLocationUpdates()
            context.getLastLocation()
        }
    }

    MarkerMap(userLocationState)
}


// MarkerMap.kt
@Composable
fun MarkerMap(
    userLocationState: State<LatLng?>,
) {
    val context = LocalContext.current
    val customMarkers = remember { mutableStateListOf<MarkerData>() }
    var userAddress by remember { mutableStateOf("") }

    // Loading animation until user location is received
    if (userLocationState.value == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    // Camera focused on user location
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLocationState.value!!, 15f)
    }

    // Reverse geocode user location whenever it changes
    LaunchedEffect(userLocationState.value) {
        userLocationState.value?.let { latLng ->
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

            val geocoder = Geocoder(context, Locale.getDefault())
            userAddress = try {
                geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                    ?.firstOrNull()?.getAddressLine(0) ?: "Unknown location"
            } catch (e: Exception) {
                "Unknown location"
            }
        }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = true),
        onMapClick = { latLng ->
            // Add custom marker at tap location
            val geocoder = Geocoder(context, Locale.getDefault())
            val address = try {
                geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                    ?.firstOrNull()?.getAddressLine(0) ?: "Unknown location"
            } catch (e: Exception) {
                "Unknown location"
            }
            customMarkers.add(MarkerData(latLng, address))
        }
    ) {
        // Marker at user location with address
        userLocationState.value?.let { latLng ->
            val userMarkerState = remember { MarkerState(latLng) }
            Marker(
                state = userMarkerState,
                title = "You are here",
                snippet = userAddress
            )
        }

        // Custom markers
        customMarkers.forEach { marker ->
            Marker(
                state = remember { MarkerState(marker.position) },
                title = marker.title
            )
        }
    }
}

data class MarkerData(
    val position: LatLng,
    val title: String
)