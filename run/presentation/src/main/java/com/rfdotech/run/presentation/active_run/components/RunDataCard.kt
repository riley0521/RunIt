package com.rfdotech.run.presentation.active_run.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import com.rfdotech.core.presentation.designsystem.FontSize12
import com.rfdotech.core.presentation.designsystem.FontSize16
import com.rfdotech.core.presentation.designsystem.FontSize32
import com.rfdotech.core.presentation.designsystem.RunItTheme
import com.rfdotech.core.presentation.designsystem.Space16
import com.rfdotech.core.presentation.designsystem.Space24
import com.rfdotech.core.presentation.designsystem.Space70
import com.rfdotech.core.presentation.ui.formatted
import com.rfdotech.core.presentation.ui.toFormattedKm
import com.rfdotech.core.presentation.ui.toFormattedPace
import com.rfdotech.run.domain.RunData
import com.rfdotech.run.presentation.R
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Composable
fun RunDataCard(
    elapsedTime: Duration,
    runData: RunData,
    modifier: Modifier = Modifier
) {
    val distanceKm = (runData.distanceMeters / 1000.0)

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(Space16))
            .background(MaterialTheme.colorScheme.surface)
            .padding(Space16),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RunDataItem(
            title = stringResource(id = R.string.duration),
            value = elapsedTime.formatted(),
            valueFontSize = FontSize32
        )
        Spacer(modifier = Modifier.height(Space24))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RunDataItem(
                title = stringResource(id = R.string.distance),
                value = distanceKm.toFormattedKm(),
                modifier = Modifier.defaultMinSize(minWidth = Space70)
            )
            RunDataItem(
                title = stringResource(id = R.string.pace),
                value = elapsedTime.toFormattedPace(distanceKm),
                modifier = Modifier.defaultMinSize(minWidth = Space70)
            )
        }
    }
}

@Composable
private fun RunDataItem(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    valueFontSize: TextUnit = FontSize16
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = FontSize12
        )
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = valueFontSize
        )
    }
}

@Preview
@Composable
private fun RunDataCardPreview() {
    RunItTheme {
        RunDataCard(
            elapsedTime = 10.minutes,
            runData = RunData(
                distanceMeters = 800,
                paceInSeconds = 180.seconds
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}