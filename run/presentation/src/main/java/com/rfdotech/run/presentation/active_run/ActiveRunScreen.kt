@file:OptIn(ExperimentalMaterial3Api::class)

package com.rfdotech.run.presentation.active_run

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rfdotech.core.presentation.designsystem.RunItTheme
import com.rfdotech.core.presentation.designsystem.Space16
import com.rfdotech.core.presentation.designsystem.Space18
import com.rfdotech.core.presentation.designsystem.StartIcon
import com.rfdotech.core.presentation.designsystem.StopIcon
import com.rfdotech.core.presentation.designsystem.components.MyDialog
import com.rfdotech.core.presentation.designsystem.components.MyFloatingActionButton
import com.rfdotech.core.presentation.designsystem.components.PrimaryButton
import com.rfdotech.core.presentation.designsystem.components.PrimaryScaffold
import com.rfdotech.core.presentation.designsystem.components.PrimaryToolbar
import com.rfdotech.core.presentation.designsystem.components.SecondaryButton
import com.rfdotech.run.domain.RunData
import com.rfdotech.run.presentation.R
import com.rfdotech.run.presentation.active_run.components.RunDataCard
import com.rfdotech.run.presentation.active_run.maps.TrackerMap
import com.rfdotech.run.presentation.active_run.service.ActiveRunService
import com.rfdotech.run.presentation.util.hasLocationPermission
import com.rfdotech.run.presentation.util.hasPostNotificationPermission
import com.rfdotech.run.presentation.util.shouldShowLocationPermissionRationale
import com.rfdotech.run.presentation.util.shouldShowPostNotificationPermissionRationale
import org.koin.androidx.compose.koinViewModel
import java.io.ByteArrayOutputStream
import kotlin.time.Duration.Companion.minutes

@Composable
fun ActiveRunScreenRoot(
    onServiceToggle: (isServiceRunning: Boolean) -> Unit,
    viewModel: ActiveRunViewModel = koinViewModel()
) {
    ActiveRunScreen(
        state = viewModel.state,
        onServiceToggle = onServiceToggle,
        onAction = viewModel::onAction
    )
}

@Composable
private fun ActiveRunScreen(
    state: ActiveRunState,
    onServiceToggle: (isServiceRunning: Boolean) -> Unit,
    onAction: (ActiveRunAction) -> Unit
) {
    val context = LocalContext.current


    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        handlePermissionRequest(
            context = context,
            onActions = { actions ->
                actions.forEach(onAction)
            }
        )
    }

    LaunchedEffect(key1 = true) {
        handlePermissionRequest(
            context = context,
            onActions = { actions ->
                actions.forEach(onAction)
            },
            onPermissionLauncher = {
                permissionLauncher.requestAppPermissions(context)
            }
        )
    }

    LaunchedEffect(key1 = state.isRunFinished) {
        if (state.isRunFinished) {
            onServiceToggle(false)
        }
    }

    LaunchedEffect(key1 = state.shouldTrack) {
        if (context.hasLocationPermission() && state.shouldTrack && !ActiveRunService.isServiceActive) {
            onServiceToggle(true)
        }
    }

    PrimaryScaffold(
        withGradient = false,
        topAppBar = {
            PrimaryToolbar(
                showBackButton = true,
                onBackClick = {
                    onAction(ActiveRunAction.OnBackClick)
                },
                title = stringResource(id = R.string.active_run)
            )
        },
        floatingActionButton = {
            val fabIcon = if (state.shouldTrack) {
                StopIcon
            } else {
                StartIcon
            }

            val fabContentDesc = if (state.shouldTrack) {
                stringResource(id = R.string.pause_run)
            } else {
                stringResource(id = R.string.start_run)
            }

            MyFloatingActionButton(
                icon = fabIcon,
                onClick = {
                    onAction(ActiveRunAction.OnToggleRunClick)
                },
                iconSize = Space18,
                contentDescription = fabContentDesc
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            TrackerMap(
                isRunFinished = state.isRunFinished,
                currentLocation = state.currentLocation,
                locations = state.runData.locations,
                onSnapshot = { bmp ->
                    val stream = ByteArrayOutputStream()
                    stream.use {
                        bmp.compress(
                            Bitmap.CompressFormat.JPEG,
                            80,
                            it
                        )
                    }
                    onAction(ActiveRunAction.OnRunProcessed(stream.toByteArray()))
                },
                modifier = Modifier.fillMaxSize()
            )

            RunDataCard(
                elapsedTime = state.elapsedTime,
                runData = state.runData,
                modifier = Modifier
                    .padding(Space16)
                    .padding(padding)
                    .fillMaxWidth()
            )
        }
    }

    if (!state.shouldTrack && state.hasStartedRunning) {
        MyDialog(
            title = stringResource(id = R.string.running_is_paused),
            description = stringResource(id = R.string.resume_or_finish_the_run),
            positiveButton = {
                PrimaryButton(
                    text = stringResource(id = R.string.resume),
                    isLoading = false,
                    onClick = {
                        onAction(ActiveRunAction.OnResumeRunClick)
                    },
                    modifier = Modifier.weight(1f)
                )
            },
            negativeButton = {
                SecondaryButton(
                    text = stringResource(id = R.string.finish),
                    isLoading = state.isSavingRun,
                    onClick = {
                        onAction(ActiveRunAction.OnFinishRunClick)
                    },
                    modifier = Modifier.weight(1f)
                )
            },
            onDismiss = {
                onAction(ActiveRunAction.OnResumeRunClick)
            }
        )
    }

    if (state.showLocationRationale || state.showPostNotificationRationale) {
        MyDialog(
            title = stringResource(id = R.string.permission_required),
            description = when {
                state.showLocationRationale && state.showPostNotificationRationale -> stringResource(
                    id = R.string.location_notification_rationale
                )

                state.showLocationRationale -> stringResource(
                    id = R.string.location_rationale
                )

                else -> stringResource(
                    id = R.string.notification_rationale
                )
            },
            positiveButton = {
                SecondaryButton(
                    text = stringResource(id = R.string.okay),
                    isLoading = false,
                    onClick = {
                        onAction(ActiveRunAction.DismissRationaleDialog)
                        permissionLauncher.requestAppPermissions(context)
                    }
                )
            },
            onDismiss = {
                // We should only dismiss the dialog by pressing positive or negative button.
            }
        )
    }
}

private fun handlePermissionRequest(
    context: Context,
    onActions: (List<ActiveRunAction>) -> Unit,
    onPermissionLauncher: (() -> Unit)? = null
) {
    val activity = context as ComponentActivity
    val showLocationRationale = activity.shouldShowLocationPermissionRationale()
    val showPostNotificationRationale = activity.shouldShowPostNotificationPermissionRationale()

    onActions(
        listOf(
            ActiveRunAction.SubmitLocationPermissionInfo(
                acceptedPermission = activity.hasLocationPermission(),
                shouldShowRationale = showLocationRationale
            ),
            ActiveRunAction.SubmitPostNotificationPermissionInfo(
                acceptedPermission = activity.hasPostNotificationPermission(),
                shouldShowRationale = showPostNotificationRationale
            )
        )
    )

    if (!showLocationRationale && !showPostNotificationRationale) {
        onPermissionLauncher?.invoke()
    }
}

private fun ActivityResultLauncher<Array<String>>.requestAppPermissions(
    context: Context
) {
    val hasLocationPermission = context.hasLocationPermission()
    val hasPostNotificationPermission = context.hasPostNotificationPermission()

    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    val postNotificationPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.POST_NOTIFICATIONS)
    } else {
        arrayOf()
    }

    when {
        !hasLocationPermission && !hasPostNotificationPermission -> {
            launch(locationPermissions + postNotificationPermissions)
        }

        !hasLocationPermission -> {
            launch(locationPermissions)
        }

        !hasPostNotificationPermission -> {
            launch(postNotificationPermissions)
        }
    }
}

@Preview
@Composable
private fun ActiveRunScreenPreview() {
    RunItTheme {
        ActiveRunScreen(
            state = ActiveRunState(
                elapsedTime = 10.minutes,
                runData = RunData(
                    distanceMeters = 800,
                    paceInSeconds = 3.minutes
                )
            ),
            onServiceToggle = {},
            onAction = {}
        )
    }
}
