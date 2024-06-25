@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.rfdotech.run.presentation.run_overview

import android.Manifest
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.rfdotech.core.domain.Address
import com.rfdotech.core.domain.location.Location
import com.rfdotech.core.domain.run.WorkState
import com.rfdotech.core.presentation.designsystem.AnalyticsIcon
import com.rfdotech.core.presentation.designsystem.LogoIcon
import com.rfdotech.core.presentation.designsystem.RunIcon
import com.rfdotech.core.presentation.designsystem.RunItTheme
import com.rfdotech.core.presentation.designsystem.SignOutIcon
import com.rfdotech.core.presentation.designsystem.Space16
import com.rfdotech.core.presentation.designsystem.Space24
import com.rfdotech.core.presentation.designsystem.Space32
import com.rfdotech.core.presentation.designsystem.components.MyDialog
import com.rfdotech.core.presentation.designsystem.components.MyFloatingActionButton
import com.rfdotech.core.presentation.designsystem.components.PrimaryButton
import com.rfdotech.core.presentation.designsystem.components.PrimaryScaffold
import com.rfdotech.core.presentation.designsystem.components.PrimaryToolbar
import com.rfdotech.core.presentation.designsystem.components.SecondaryButton
import com.rfdotech.core.presentation.designsystem.components.util.DropDownItem
import com.rfdotech.core.presentation.ui.ObserveAsEvents
import com.rfdotech.core.presentation.ui.showToastRes
import com.rfdotech.run.presentation.R
import com.rfdotech.run.presentation.run_overview.components.RunListItem
import com.rfdotech.run.presentation.run_overview.mapper.toRunUi
import com.rfdotech.run.presentation.util.hasPostNotificationPermission
import com.rfdotech.run.presentation.util.shouldShowPostNotificationPermissionRationale
import org.koin.androidx.compose.koinViewModel

@Composable
fun RunOverviewScreenRoot(
    onAnalyticsClick: () -> Unit,
    onStartClick: () -> Unit,
    onSignOutClick: () -> Unit,
    viewModel: RunOverviewViewModel = koinViewModel()
) {

    val context = LocalContext.current
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            RunOverviewEvent.DeleteAccountSuccessful -> {
                context.showToastRes(R.string.account_deleted_successfully)
                onSignOutClick()
            }

            RunOverviewEvent.NoInternet -> {
                context.showToastRes(R.string.internet_not_available)
            }

            RunOverviewEvent.SignInAgain -> {
                context.showToastRes(R.string.please_sign_in_again_and_retry)
                onSignOutClick()
            }
        }
    }

    RunOverviewScreen(
        state = viewModel.state,
        onAction = { action ->
            when (action) {
                RunOverviewAction.OnAnalyticsClick -> onAnalyticsClick()
                RunOverviewAction.OnStartClick -> onStartClick()
                RunOverviewAction.OnSignOutClick -> {
                    viewModel.onAction(action)
                    onSignOutClick()
                }

                else -> viewModel.onAction(action)
            }
        },
        onGetAddressFromLocation = {
            viewModel.getAddressFromLocation(it)
        }
    )
}

@Composable
private fun RunOverviewScreen(
    state: RunOverviewState,
    onAction: (RunOverviewAction) -> Unit,
    onGetAddressFromLocation: (suspend (Location) -> Address?)? = null
) {
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = topAppBarState
    )
    val menuItems = listOf(
        DropDownItem(icon = AnalyticsIcon, title = stringResource(id = R.string.analytics)),
        DropDownItem(
            icon = Icons.Outlined.DeleteForever,
            title = stringResource(id = R.string.delete_account)
        ),
        DropDownItem(icon = SignOutIcon, title = stringResource(id = R.string.sign_out))
    )
    val context = LocalContext.current as ComponentActivity
    val runUiList = remember(state.runs) {
        state.runs.map { it.toRunUi(context) }
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            val shouldShowRationale = context.shouldShowPostNotificationPermissionRationale()
            onAction(RunOverviewAction.SubmitPostNotificationPermissionInfo(shouldShowRationale))
        }
    )

    LaunchedEffect(Unit) {
        val hasPermission = context.hasPostNotificationPermission()

        if (!hasPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    if (state.showRationale) {
        MyDialog(
            title = stringResource(id = R.string.permission_required),
            description = stringResource(id = R.string.notification_rationale),
            positiveButton = {
                SecondaryButton(
                    text = stringResource(id = R.string.okay),
                    isLoading = false,
                    onClick = {
                        onAction(RunOverviewAction.DismissRationaleDialog)
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                )
            },
            onDismiss = {} // no-op
        )
    }

    if (state.workInformation?.state == WorkState.RUNNING) {
        Dialog(onDismissRequest = {}) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }

    LaunchedEffect(state.workInformation) {
        if (state.workInformation?.state == WorkState.SUCCEEDED) {
            onAction(RunOverviewAction.ConfirmDeleteAccount)
        }
    }

    if (state.showDeleteAccountDialog) {
        MyDialog(
            title = stringResource(id = R.string.delete_account),
            description = stringResource(id = R.string.delete_account_description),
            positiveButton = {
                PrimaryButton(
                    text = stringResource(id = R.string.delete_account_confirmation),
                    isLoading = false,
                    onClick = {
                        onAction(RunOverviewAction.DeleteAccount(true))
                    },
                    modifier = Modifier.weight(1f)
                )
            },
            negativeButton = {
                SecondaryButton(
                    text = stringResource(id = R.string.delete_account_cancel),
                    isLoading = false,
                    onClick = {
                        onAction(RunOverviewAction.DeleteAccount(false))
                    },
                    modifier = Modifier.weight(1f)
                )
            },
            onDismiss = {
                onAction(RunOverviewAction.DeleteAccount(false))
            }
        )
    }

    PrimaryScaffold(
        topAppBar = {
            PrimaryToolbar(
                title = stringResource(id = R.string.app_name),
                menuItems = menuItems,
                onMenuItemClick = { index ->
                    when (index) {
                        0 -> onAction(RunOverviewAction.OnAnalyticsClick)
                        1 -> onAction(RunOverviewAction.OnDeleteAccountClick)
                        2 -> onAction(RunOverviewAction.OnSignOutClick)
                        else -> Unit
                    }
                },
                scrollBehavior = scrollBehavior,
                startContent = {
                    Icon(
                        imageVector = LogoIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(Space32)
                    )
                }
            )
        },
        floatingActionButton = {
            MyFloatingActionButton(
                icon = RunIcon,
                onClick = {
                    onAction(RunOverviewAction.OnStartClick)
                },
                contentDescription = stringResource(id = R.string.start_your_run),
                iconSize = Space24
            )
        }
    ) { padding ->
        if (state.isGettingRuns) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (runUiList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.click_run_icon_to_start_first_run),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Space16),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .padding(Space16)
                    .padding(bottom = Space16),
                contentPadding = padding,
                verticalArrangement = Arrangement.spacedBy(Space16)
            ) {
                items(items = runUiList, key = { it.id }) { run ->
                    RunListItem(
                        run = run,
                        onDeleteClick = {
                            onAction(RunOverviewAction.DeleteRunById(run.id))
                        },
                        modifier = Modifier.animateItemPlacement(),
                        onGetAddressFromLocation = onGetAddressFromLocation
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun RunOverviewScreenScreenPreview() {
    RunItTheme {
        RunOverviewScreen(
            state = RunOverviewState(),
            onAction = {}
        )
    }
}

