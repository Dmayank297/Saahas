package com.example.saahas.ui.Screens.Location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.saahas.Models.LocationData
import com.example.saahas.Models.Room.VoiceRecordingDatabase
import com.example.saahas.Models.UnsafeLocation
import com.example.saahas.PermissionManager
import com.example.saahas.R
import com.example.saahas.Service.LocationRepository
import com.example.saahas.Service.UnsafeAreaDetector
import com.example.saahas.Utils.Location.LocationUtils
import com.example.saahas.Voice.Service.BuzzerService
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("InlinedApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationMapScreen(
    permission: PermissionManager,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val location = LocationUtils(context)
    val viewModel: LocationViewModel = viewModel(
        factory = LocationViewModelFactory(VoiceRecordingDatabase.getDatabase(context).contactDao())
    )
    var selectedFriends by remember { mutableStateOf(listOf<String>()) }
    var duration by remember { mutableStateOf("Always") }
    val locationData by viewModel.locationLiveData.observeAsState()
    var isSharing by remember { mutableStateOf(false) }
    val nearbyPlaces by viewModel.nearbyPlaces.observeAsState(emptyList())
    val routes by viewModel.routes.observeAsState(emptyList())
    val apiKey = "AIzaSyDbl21qYqGbC4SVm0Fzsu9ajFcOHTo-c8E"

    val errorMessage by viewModel.errorMessage.observeAsState()
    val scope = rememberCoroutineScope()

    val locationRepository = remember { LocationRepository() }
    val buzzerService = remember { BuzzerService() }
    val unsafeLocations = remember { mutableStateOf<List<UnsafeLocation>>(emptyList()) }
    val unsafeAreaDetector = remember { UnsafeAreaDetector(context, locationRepository, buzzerService) }

    // Button scale animations
    var shareButtonScale by remember { mutableFloatStateOf(1f) }
    var hospitalButtonScale by remember { mutableFloatStateOf(1f) }
    var policeButtonScale by remember { mutableFloatStateOf(1f) }
    var unsafeButtonScale by remember { mutableFloatStateOf(1f) }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        if (location.hasLocationPermission(context)) {
            location.requestLocationUpdates(viewModel)
            unsafeAreaDetector.startMonitoring()
            while (true) {
                unsafeLocations.value = locationRepository.getUnsafeLocations()
                delay(3000)
            }
        } else {
            permission.checkAndRequestPermission(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.SEND_SMS
            )
        }
    }

    LaunchedEffect(locationData) {
        scope.launch {
            while (isSharing) {
                delay(5_000)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Safe Marking",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Controls Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Duration Dropdown
                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        OutlinedButton(
                            onClick = { expanded = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Duration: $duration",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = "Select Duration",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surface)
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            listOf("Always", "1 Hour", "8 Hours").forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option, style = MaterialTheme.typography.bodyLarge) },
                                    onClick = { duration = option; expanded = false },
                                    modifier = Modifier.animateContentSize()
                                )
                            }
                        }
                    }

                    // Share Location Button
                    Button(
                        onClick = {
                            shareButtonScale = 0.95f
                            if (location.hasLocationPermission(context)) {
                                if (!isSharing) {
                                    location.requestLocationUpdates(viewModel)
                                    viewModel.startLocationSharing(context)
                                    isSharing = true
                                } else {
                                    location.stopLocationUpdates()
                                    viewModel.stopLocationSharing()
                                    isSharing = false
                                }
                            } else {
                                permission.checkAndRequestPermission(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.SEND_SMS
                                )
                            }
                            shareButtonScale = 1f
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .scale(shareButtonScale)
                            .animateContentSize(animationSpec = tween(200)),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = if (isSharing) "Stop Live Location" else "Start Live Location",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    // Emergency Services Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                hospitalButtonScale = 0.95f
                                if (location.hasLocationPermission(context)) {
                                    viewModel.findNearbyEmergencyServices(context, apiKey, "Hospital")
                                } else {
                                    permission.checkAndRequestPermission(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION,
                                        Manifest.permission.FOREGROUND_SERVICE
                                    )
                                }
                                hospitalButtonScale = 1f
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .scale(hospitalButtonScale)
                                .animateContentSize(animationSpec = tween(200)),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Text(
                                text = "Find Hospital",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSecondary
                            )
                        }
                        Button(
                            onClick = {
                                policeButtonScale = 0.95f
                                if (location.hasLocationPermission(context)) {
                                    viewModel.findNearbyEmergencyServices(context, apiKey, "Police Station")
                                } else {
                                    permission.checkAndRequestPermission(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION,
                                        Manifest.permission.FOREGROUND_SERVICE
                                    )
                                }
                                policeButtonScale = 1f
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .scale(policeButtonScale)
                                .animateContentSize(animationSpec = tween(200)),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Text(
                                text = "Find Police",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSecondary
                            )
                        }
                    }

                    // Mark Unsafe Button
                    Button(
                        onClick = {
                            unsafeButtonScale = 0.95f
                            if (location.hasLocationPermission(context)) {
                                locationData?.let { data ->
                                    scope.launch {
                                        val success = locationRepository.markUnsafeLocation(
                                            data.latitude,
                                            data.longitude
                                        )
                                        Toast.makeText(
                                            context,
                                            if (success) "Location marked as unsafe" else "Failed to mark unsafe",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        unsafeLocations.value = locationRepository.getUnsafeLocations()
                                    }
                                } ?: run {
                                    Toast.makeText(
                                        context,
                                        "Location not available yet",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                permission.checkAndRequestPermission(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            }
                            unsafeButtonScale = 1f
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .scale(unsafeButtonScale)
                            .animateContentSize(animationSpec = tween(200)),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text(
                            text = "Mark Unsafe",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onError
                        )
                    }
                }
            }

            // Map Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .animateContentSize(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (locationData == null) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        AndroidView(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp)),
                            factory = { context ->
                                MapView(context).apply {
                                    onCreate(null)
                                    onResume()
                                    getMapAsync { googleMap ->
                                        if (location.hasLocationPermission(context)) {
                                            googleMap.isMyLocationEnabled = true
                                        }
                                        updateMap(
                                            googleMap,
                                            locationData!!,
                                            nearbyPlaces,
                                            routes,
                                            unsafeLocations.value,
                                            context
                                        )
                                    }
                                }
                            },
                            update = { mapView ->
                                mapView.getMapAsync { googleMap ->
                                    updateMap(
                                        googleMap,
                                        locationData!!,
                                        nearbyPlaces,
                                        routes,
                                        unsafeLocations.value,
                                        context
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            if (isSharing) {
                location.stopLocationUpdates()
                viewModel.stopLocationSharing()
            }
            unsafeAreaDetector.stopMonitoring()
        }
    }
}

private fun updateMap(
    googleMap: com.google.android.gms.maps.GoogleMap,
    locationData: LocationData,
    nearbyPlaces: List<Pair<LatLng, String>>,
    routes: List<List<LatLng>>,
    unsafeLocations: List<UnsafeLocation>,
    context: Context
) {
    googleMap.clear()
    locationData?.let { data ->
        val latLng = LatLng(data.latitude, data.longitude)
        googleMap.addMarker(MarkerOptions().position(latLng).title("You"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }
    nearbyPlaces.forEach { (latLng, title) ->
        val markerOptions = MarkerOptions().position(latLng).title(title)
        when (title) {
            "Hospital" -> markerOptions.icon(
                bitmapDescriptorFromVector(context, R.drawable.ic_hospital_red)
            )
            "Police Station" -> markerOptions.icon(
                bitmapDescriptorFromVector(context, R.drawable.ic_police_blue)
            )
        }
        googleMap.addMarker(markerOptions)
    }
    routes.forEach { route ->
        val color = if (nearbyPlaces.firstOrNull()?.second == "Hospital") {
            android.graphics.Color.RED
        } else {
            android.graphics.Color.BLUE
        }
        googleMap.addPolyline(PolylineOptions().addAll(route).color(color))
    }
    unsafeLocations.forEach { unsafe ->
        val latLng = LatLng(unsafe.lat, unsafe.lng)
        googleMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .title("Unsafe")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        )
    }
}

private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
    val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
    vectorDrawable?.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
    val bitmap = Bitmap.createBitmap(
        vectorDrawable?.intrinsicWidth ?: 48,
        vectorDrawable?.intrinsicHeight ?: 48,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    vectorDrawable?.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}