package com.rfdotech.analytics.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.rfdotech.analytics.domain.DateParam
import com.rfdotech.analytics.domain.toZonedDateTime
import com.rfdotech.analytics.presentation.R
import com.rfdotech.core.domain.location.Location
import com.rfdotech.core.domain.run.DistanceAndSpeedCalculator
import com.rfdotech.core.domain.run.Run
import com.rfdotech.core.presentation.designsystem.ArrowRightIcon
import com.rfdotech.core.presentation.designsystem.FontSize16
import com.rfdotech.core.presentation.designsystem.RunItTheme
import com.rfdotech.core.presentation.designsystem.Space16
import com.rfdotech.core.presentation.designsystem.Space32
import com.rfdotech.core.presentation.designsystem.Space8
import com.rfdotech.core.presentation.designsystem.components.GradientBackground
import java.time.ZonedDateTime
import java.util.UUID
import kotlin.time.Duration.Companion.minutes

@Composable
fun AnalyticsCardWithChart(
    monthAndYear: String,
    data: RunAndDistanceMap,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(Space16))
            .background(MaterialTheme.colorScheme.surface)
            .padding(Space16),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.avg_distance_overtime),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = FontSize16
            )
            IconButton(onClick = onClick) {
                Icon(
                    imageVector = ArrowRightIcon,
                    contentDescription = stringResource(id = R.string.go_to_analytics_detail),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        Spacer(modifier = Modifier.height(Space8))
        AnalyticChart(
            data = data,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(Space32))
        Text(
            text = monthAndYear,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AnalyticsChartPreview() {
    RunItTheme {
        GradientBackground {
            Column {
                AnalyticsCardWithChart(
                    monthAndYear = "May 2024",
                    data = getSampleMap(),
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Space16)
                )
            }
        }
    }
}

fun getSampleMap(): RunAndDistanceMap {
    return mapOf(
        sampleRun(
            distanceMeters = 1000,
            dateTimeUtc = getZonedDateTime(DateParam(2024, 1, 1))
        ),
//                sampleRun(
//                    distanceMeters = 2000,
//                    dateTimeUtc = getZonedDateTime(DateParam(2024, 1, 3))
//                ),
        sampleRun(
            distanceMeters = 1500,
            dateTimeUtc = getZonedDateTime(DateParam(2024, 1, 5))
        ),
//                sampleRun(
//                    distanceMeters = 1900,
//                    dateTimeUtc = getZonedDateTime(DateParam(2024, 1, 7))
//                ),
        sampleRun(
            distanceMeters = 3000,
            dateTimeUtc = getZonedDateTime(DateParam(2024, 1, 9))
        ),
        sampleRun(
            distanceMeters = 3200,
            dateTimeUtc = getZonedDateTime(DateParam(2024, 1, 11))
        ),
        sampleRun(
            distanceMeters = 3600,
            dateTimeUtc = getZonedDateTime(DateParam(2024, 1, 13))
        ),
        sampleRun(
            distanceMeters = 5000,
            dateTimeUtc = getZonedDateTime(DateParam(2024, 1, 15))
        ),
        sampleRun(
            distanceMeters = 4800,
            dateTimeUtc = getZonedDateTime(DateParam(2024, 1, 17))
        ),
        sampleRun(
            distanceMeters = 6700,
            dateTimeUtc = getZonedDateTime(DateParam(2024, 1, 22))
        )
    )
}

private fun getZonedDateTime(param: DateParam): ZonedDateTime {
    return param.toZonedDateTime(isEnd = true)
}

private fun sampleRun(distanceMeters: Int, dateTimeUtc: ZonedDateTime): Pair<Run, Float> {
    return Run(
        id = UUID.randomUUID().toString(),
        duration = 30.minutes,
        dateTimeUtc = dateTimeUtc,
        distanceMeters = distanceMeters,
        location = Location(latitude = 1.0, longitude = 1.0),
        maxSpeedKmh = 15.0,
        totalElevationMeters = 10,
        numberOfSteps = 9500,
        mapPictureUrl = null
    ) to DistanceAndSpeedCalculator.getKmFromMeters(distanceMeters).toFloat()
}