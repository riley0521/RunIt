@file:OptIn(MapsComposeExperimentalApi::class, DelicateCoroutinesApi::class)

package com.rfdotech.run.presentation.active_run.maps

import android.graphics.Bitmap
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.google.maps.android.ktx.awaitSnapshot
import com.rfdotech.core.domain.location.Location
import com.rfdotech.core.presentation.designsystem.ASPECT_RATIO_16_9
import com.rfdotech.core.presentation.designsystem.RunIcon
import com.rfdotech.core.presentation.designsystem.Space100
import com.rfdotech.core.presentation.designsystem.Space18
import com.rfdotech.core.presentation.designsystem.Space300
import com.rfdotech.core.presentation.designsystem.Space32
import com.rfdotech.run.domain.ListOfLocations
import com.rfdotech.run.presentation.R
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val DELAY_500_MILLIS = 500L
const val CAMERA_ZOOM = 17f

@Composable
fun TrackerMap(
    isRunFinished: Boolean,
    currentLocation: Location?,
    locations: ListOfLocations,
    onSnapshot: (Bitmap) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val mapStyle = remember {
        MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style)
    }
    val cameraPositionState = rememberCameraPositionState()
    val markerState = rememberMarkerState()

    val markerPositionLat by animateFloatAsState(
        targetValue = currentLocation?.latitude?.toFloat() ?: 0f,
        animationSpec = tween(durationMillis = DELAY_500_MILLIS.toInt()),
        label = ""
    )
    val markerPositionLong by animateFloatAsState(
        targetValue = currentLocation?.longitude?.toFloat() ?: 0f,
        animationSpec = tween(durationMillis = DELAY_500_MILLIS.toInt()),
        label = ""
    )
    val markerPosition = remember(markerPositionLat, markerPositionLong) {
        LatLng(markerPositionLat.toDouble(), markerPositionLong.toDouble())
    }

    LaunchedEffect(markerPosition, isRunFinished) {
        if (!isRunFinished) {
            markerState.position = markerPosition
        }
    }

    LaunchedEffect(currentLocation, isRunFinished) {
        if (currentLocation != null && !isRunFinished) {
            val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(latLng, CAMERA_ZOOM)
            )
        }
    }

    var triggerCapture by remember {
        mutableStateOf(false)
    }
    var createSnapshotJob: Job? = remember {
        null
    }

    GoogleMap(
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            mapStyleOptions = mapStyle
        ),
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false
        ),
        modifier = if (isRunFinished) {
            modifier
                .width(Space300)
                .aspectRatio(ASPECT_RATIO_16_9)
                .alpha(0f)
                .onSizeChanged {
                    if (it.width >= Space300.value.toInt()) {
                        triggerCapture = true
                    }
                }
        } else {
            modifier
        }
    ) {
        ActiveRunPolyLines(locations = locations)

        MapEffect(locations, isRunFinished, triggerCapture, createSnapshotJob) { map ->
            if (isRunFinished && triggerCapture && createSnapshotJob == null) {
                triggerCapture = false

                val boundsBuilder = LatLngBounds.builder()
                locations.flatten().forEach { locationTimestamp ->
                    with (locationTimestamp.location.location) {
                        boundsBuilder.include(LatLng(latitude, longitude))
                    }
                }
                map.moveCamera(
                    CameraUpdateFactory.newLatLngBounds(
                        boundsBuilder.build(),
                        Space100.value.toInt()
                    )
                )

                map.setOnCameraIdleListener {
                    createSnapshotJob?.cancel()
                    createSnapshotJob = GlobalScope.launch {
                        // Make sure that the map really stops moving, and the image is sharp and focused
                        // before taking a screenshot
                        delay(DELAY_500_MILLIS)
                        map.awaitSnapshot()?.let(onSnapshot)
                    }
                }
            }
        }

        if (!isRunFinished && currentLocation != null) {
            MarkerComposable(
                currentLocation,
                state = markerState
            ) {
                Box(
                    modifier = Modifier
                        .size(Space32)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = RunIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(Space18)
                    )
                }
            }
        }
    }
}