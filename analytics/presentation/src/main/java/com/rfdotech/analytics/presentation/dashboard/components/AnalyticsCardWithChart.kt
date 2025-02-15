package com.rfdotech.analytics.presentation.dashboard.components

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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.rfdotech.analytics.presentation.DateHelper
import com.rfdotech.analytics.domain.DateParam
import com.rfdotech.analytics.domain.toZonedDateTime
import com.rfdotech.analytics.presentation.R
import com.rfdotech.analytics.presentation.dashboard.model.AnalyticType
import com.rfdotech.core.domain.TextWithContentDesc
import com.rfdotech.core.domain.location.Location
import com.rfdotech.core.domain.run.Run
import com.rfdotech.core.presentation.designsystem.ArrowRightIcon
import com.rfdotech.core.presentation.designsystem.FontSize16
import com.rfdotech.core.presentation.designsystem.RunItTheme
import com.rfdotech.core.presentation.designsystem.Space16
import com.rfdotech.core.presentation.designsystem.Space32
import com.rfdotech.core.presentation.designsystem.Space8
import com.rfdotech.core.presentation.designsystem.components.GradientBackground
import java.time.ZonedDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@Composable
fun AnalyticsCardWithChart(
    title: TextWithContentDesc,
    monthAndYear: String,
    analyticType: AnalyticType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDetailed: Boolean = false
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
                text = title.text,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = FontSize16,
                modifier = Modifier
                    .semantics {
                        this.contentDescription = title.contentDesc
                    }
            )
            if (!isDetailed) {
                IconButton(onClick = onClick) {
                    Icon(
                        imageVector = ArrowRightIcon,
                        contentDescription = stringResource(id = R.string.acc_go_to_detail_screen),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(Space8))

        // When we do this, we also recompose the modelProducer which will forcefully show the chart.
        // We currently have a bug using vico-compose 2.0.0-alpha.20
        if (analyticType.getData().isEmpty()) {
            Text(
                text = stringResource(id = R.string.data_not_available),
                color = MaterialTheme.colorScheme.error
            )
        } else {
            AnalyticChart(
                analyticType = analyticType,
                scrollEnabled = isDetailed,
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (!isDetailed) {
            Spacer(modifier = Modifier.height(Space32))
            Text(
                text = monthAndYear,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AnalyticsChartPreview() {
    RunItTheme {
        GradientBackground {
            Column {
                AnalyticsCardWithChart(
                    title = TextWithContentDesc("Avg. Something"),
                    monthAndYear = DateHelper.getMonthAndYearFormatted(),
                    analyticType = AnalyticType.Pace(getRunsWithPace()),
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Space16)
                )
            }
        }
    }
}

fun getRunsWithDistance(): List<Run> {
    return listOf(
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
            dateTimeUtc = getZonedDateTime(DateParam(2024, 1, 10))
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

fun getRunsWithPace(): List<Run> {
    return listOf(
        sampleRun(
            distanceMeters = 1000,
            dateTimeUtc = getZonedDateTime(DateParam(2024, 1, 1)),
            duration = 2.hours
        ),
//                sampleRun(
//                    distanceMeters = 2000,
//                    dateTimeUtc = getZonedDateTime(DateParam(2024, 1, 3))
//                ),
        sampleRun(
            distanceMeters = 1500,
            dateTimeUtc = getZonedDateTime(DateParam(2024, 1, 5)),
            duration = 180.minutes
        ),
//                sampleRun(
//                    distanceMeters = 1900,
//                    dateTimeUtc = getZonedDateTime(DateParam(2024, 1, 7))
//                ),
        sampleRun(
            distanceMeters = 3000,
            dateTimeUtc = getZonedDateTime(DateParam(2024, 1, 9)),
            duration = 185.minutes
        ),
        sampleRun(
            distanceMeters = 3200,
            dateTimeUtc = getZonedDateTime(DateParam(2024, 1, 11)),
            duration = 210.minutes
        ),
        sampleRun(
            distanceMeters = 3600,
            dateTimeUtc = getZonedDateTime(DateParam(2024, 1, 13)),
            duration = 236.minutes
        ),
        sampleRun(
            distanceMeters = 5000,
            dateTimeUtc = getZonedDateTime(DateParam(2024, 1, 15)),
            duration = 60.minutes
        ),
        sampleRun(
            distanceMeters = 4800,
            dateTimeUtc = getZonedDateTime(DateParam(2024, 1, 17)),
            duration = 70.minutes
        ),
        sampleRun(
            distanceMeters = 6700,
            dateTimeUtc = getZonedDateTime(DateParam(2024, 1, 22)),
            duration = 69.minutes
        ),
        sampleRun(
            distanceMeters = 6700,
            dateTimeUtc = getZonedDateTime(DateParam(2024, 2, 10)),
            duration = 69.minutes
        ),
        sampleRun(
            distanceMeters = 6700,
            dateTimeUtc = getZonedDateTime(DateParam(2024, 2, 11)),
            duration = 69.minutes
        ),
        sampleRun(
            distanceMeters = 6700,
            dateTimeUtc = getZonedDateTime(DateParam(2024, 2, 12)),
            duration = 69.minutes
        )
    )
}

private fun getZonedDateTime(param: DateParam): ZonedDateTime {
    return param.toZonedDateTime(isEnd = true)
}

private fun sampleRun(distanceMeters: Int, dateTimeUtc: ZonedDateTime, duration: Duration = 30.minutes): Run {
    return Run(
        id = null,
        duration = duration,
        dateTimeUtc = dateTimeUtc,
        distanceMeters = distanceMeters,
        location = Location(latitude = 1.0, longitude = 1.0),
        maxSpeedKmh = 15.0,
        totalElevationMeters = 10,
        numberOfSteps = 9500,
        avgHeartRate = 124,
        mapPictureUrl = null
    )
}