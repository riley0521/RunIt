package com.rfdotech.analytics.presentation.dashboard.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.fullWidth
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineSpec
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.shader.color
import com.patrykandpatrick.vico.core.cartesian.HorizontalLayout
import com.patrykandpatrick.vico.core.cartesian.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shader.DynamicShader
import com.patrykandpatrick.vico.core.common.shader.TopBottomShader
import com.rfdotech.analytics.presentation.dashboard.model.AnalyticType
import com.rfdotech.core.domain.roundToDecimals
import com.rfdotech.core.presentation.designsystem.Space32
import com.rfdotech.core.presentation.designsystem.Space48
import com.rfdotech.core.presentation.designsystem.colorPrimary
import com.rfdotech.core.presentation.ui.formatted
import com.rfdotech.core.presentation.ui.getMonthDayPattern
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.time.Duration.Companion.seconds

@Composable
fun AnalyticChart(
    analyticType: AnalyticType,
    modifier: Modifier = Modifier,
    scrollEnabled: Boolean = false,
    markerTextWidth: Dp = Space48
) {
    val modelProducer = remember {
        CartesianChartModelProducer.build()
    }

    val data = remember(analyticType) {
        analyticType.getData()
    }

    val xToDates = remember(data) {
        data.keys.associate {
            val localDate = it.dateTimeUtc.toLocalDate()
            val dateFloat = localDate.toEpochDay().toFloat()

            dateFloat to localDate
        }
    }
    val xToDateMapKey = remember {
        ExtraStore.Key<Map<Float, LocalDate>>()
    }

    LaunchedEffect(analyticType) {
        if (data.isEmpty()) {
            return@LaunchedEffect
        }

        modelProducer.runTransaction {
            lineSeries { series(xToDates.keys, data.values) }
            updateExtras { it[xToDateMapKey] = xToDates }
        }.await()
    }

    val marker = rememberDefaultLineChartMarker(
        textWidth = markerTextWidth,
        getFormattedValue = { value ->
            return@rememberDefaultLineChartMarker when (analyticType) {
                is AnalyticType.Distance -> "${value.toDouble().roundToDecimals()} km"
                is AnalyticType.Pace -> {
                    val pace = value.toInt().seconds.formatted()
                    pace
                }
            }
        }
    )
    val horizontalSpace = markerTextWidth + Space32

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                lines = listOf(
                    rememberLineSpec(
                        shader = TopBottomShader(
                            DynamicShader.color(colorPrimary),
                            DynamicShader.color(colorPrimary.copy(alpha = 0.5f))
                        )
                    )
                ),
                spacing = if (scrollEnabled) horizontalSpace else Space32
            ),
            bottomAxis = rememberBottomAxis(
                label = rememberTextComponent(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                guideline = rememberAxisGuidelineComponent(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                axis = rememberLineComponent(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                valueFormatter = { x, chartValues, _ ->
                    val pattern = if (scrollEnabled) {
                        // TODO: We can further modify this if we want to change the language in the app only
                        // and not the system settings itself. But for simplicity, we use this.
                        Locale.getDefault().getMonthDayPattern()
                    } else {
                        "d"
                    }

                    val formatter = DateTimeFormatter.ofPattern(pattern)
                    (chartValues.model.extraStore[xToDateMapKey][x] ?: LocalDate.ofEpochDay(x.toLong())).format(formatter)
                },
                itemPlacer = remember {
                    AxisItemPlacer.Horizontal.default(addExtremeLabelPadding = true)
                }
            ),
            persistentMarkers = if (scrollEnabled) {
                xToDates.keys.associateWith {
                    marker
                }
            } else null
        ),
        modelProducer = modelProducer,
        modifier = modifier,
        marker = if (!scrollEnabled) marker else null,
        scrollState = rememberVicoScrollState(scrollEnabled = scrollEnabled),
        horizontalLayout = HorizontalLayout.fullWidth()
    )
}