@file:OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)

package com.rfdotech.run.presentation.run_overview.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.SubcomposeAsyncImage
import com.rfdotech.core.domain.Address
import com.rfdotech.core.presentation.designsystem.ASPECT_RATIO_16_9
import com.rfdotech.core.presentation.designsystem.CalendarIcon
import com.rfdotech.core.presentation.designsystem.FontSize12
import com.rfdotech.core.presentation.designsystem.RunItTheme
import com.rfdotech.core.presentation.designsystem.RunOutlinedIcon
import com.rfdotech.core.presentation.designsystem.Space1
import com.rfdotech.core.presentation.designsystem.Space12
import com.rfdotech.core.presentation.designsystem.Space16
import com.rfdotech.core.presentation.designsystem.Space18
import com.rfdotech.core.presentation.designsystem.Space2
import com.rfdotech.core.presentation.designsystem.Space4
import com.rfdotech.core.presentation.designsystem.Space40
import com.rfdotech.core.presentation.designsystem.colorOnSurfaceVariant
import com.rfdotech.core.presentation.designsystem.colorPrimary
import com.rfdotech.run.presentation.R
import com.rfdotech.run.presentation.run_overview.model.RunDataUi
import com.rfdotech.run.presentation.run_overview.model.RunUi
import kotlin.math.max

@Composable
fun RunListItem(
    run: RunUi,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDropDown by remember {
        mutableStateOf(false)
    }

    Box {
        Column(
            modifier = modifier
                .clip(RoundedCornerShape(Space16))
                .background(MaterialTheme.colorScheme.surface)
                .combinedClickable(
                    onClick = {},
                    onLongClick = {
                        showDropDown = true
                    }
                )
                .padding(Space16),
            verticalArrangement = Arrangement.spacedBy(Space16)
        ) {
            MapImage(imageUrl = run.mapPictureUrl)
            RunningTimeSection(
                duration = run.duration,
                address = run.address,
                modifier = Modifier.fillMaxWidth()
            )
            HorizontalDivider(
                color = colorOnSurfaceVariant.copy(alpha = 0.4f)
            )
            RunningDateSection(dateTime = run.dateTime)
            DataGrid(run = run)
        }
        DropdownMenu(
            expanded = showDropDown,
            onDismissRequest = { showDropDown = false }
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(id = R.string.delete)
                    )
                },
                onClick = {
                    showDropDown = false
                    onDeleteClick()
                }
            )
        }
    }
}

@Composable
private fun MapImage(
    imageUrl: String?,
    modifier: Modifier = Modifier
) {
    SubcomposeAsyncImage(
        model = imageUrl,
        contentDescription = stringResource(id = R.string.run_map),
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(ASPECT_RATIO_16_9)
            .clip(RoundedCornerShape(Space16)),
        loading = {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(Space18),
                    strokeWidth = Space2,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        error = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.errorContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.error_load_image),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    )
}

@Composable
private fun RunningTimeSection(
    duration: String,
    address: Address?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(Space40)
                .clip(RoundedCornerShape(Space12))
                .background(colorPrimary.copy(alpha = 0.1f))
                .border(
                    width = Space1,
                    color = colorPrimary,
                    shape = RoundedCornerShape(Space12)
                )
                .padding(Space4),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = RunOutlinedIcon,
                contentDescription = null,
                tint = colorPrimary
            )
        }
        Spacer(modifier = Modifier.width(Space16))
        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.total_running_time),
                color = colorOnSurfaceVariant
            )
            Text(
                text = duration,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (address != null) {
                Text(
                    text = address,
                    color = colorOnSurfaceVariant,
                    fontSize = FontSize12
                )
            }
        }
    }
}

@Composable
fun RunningDateSection(
    dateTime: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = CalendarIcon,
            contentDescription = null,
            tint = colorOnSurfaceVariant
        )
        Spacer(modifier = Modifier.width(Space16))
        Text(
            text = dateTime,
            color = colorOnSurfaceVariant
        )
    }
}

@Composable
private fun DataGrid(
    run: RunUi,
    modifier: Modifier = Modifier
) {
    val runDataUiList = listOf(
        RunDataUi(name = stringResource(id = R.string.distance), value = run.distance),
        RunDataUi(name = stringResource(id = R.string.pace), value = run.pace),
        RunDataUi(name = stringResource(id = R.string.avg_speed), value = run.avgSpeed),
        RunDataUi(name = stringResource(id = R.string.max_speed), value = run.maxSpeed),
        RunDataUi(name = stringResource(id = R.string.total_elevation), value = run.totalElevation)
    )

    var maxWidth by remember {
        mutableIntStateOf(0)
    }
    val maxWidthDp = with(LocalDensity.current) { maxWidth.toDp() }

    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Space16),
        verticalArrangement = Arrangement.spacedBy(Space16),
    ) {
        runDataUiList.forEach { runData ->
            DataGridCell(
                runData = runData,
                modifier = Modifier
                    .defaultMinSize(minWidth = maxWidthDp)
                    .onSizeChanged {
                        maxWidth = max(maxWidth, it.width)
                    }
            )
        }
    }
}

@Composable
private fun DataGridCell(
    runData: RunDataUi,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = runData.name,
            color = colorOnSurfaceVariant,
            fontSize = FontSize12
        )
        Spacer(modifier = Modifier.height(Space4))
        Text(
            text = runData.value,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview
@Composable
private fun RunListItemPreview() {
    RunItTheme {
        RunListItem(
            run = RunUi(
                id = "123",
                duration = "00:05:00",
                dateTime = "Apr. 14, 2024 - 02:22PM",
                distance = "0.8 km",
                avgSpeed = "12.5 km/h",
                maxSpeed = "16 km/h",
                pace = "17:38 / km",
                totalElevation = "1 m",
                mapPictureUrl = null,
                address = "Something"
            ),
            onDeleteClick = {

            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}