package com.rfdotech.analytics.presentation.dashboard.components

import android.text.Layout
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.patrykandpatrick.vico.compose.common.component.rememberLayeredComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.of
import com.patrykandpatrick.vico.compose.common.shape.markerCornered
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.LineCartesianLayerMarkerTarget
import com.patrykandpatrick.vico.core.common.Dimensions
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.shape.Corner
import com.patrykandpatrick.vico.core.common.shape.Shape
import com.rfdotech.core.presentation.designsystem.Space12
import com.rfdotech.core.presentation.designsystem.Space16
import com.rfdotech.core.presentation.designsystem.Space24
import com.rfdotech.core.presentation.designsystem.Space4
import com.rfdotech.core.presentation.designsystem.Space48
import com.rfdotech.core.presentation.designsystem.colorPrimary

@Composable
fun rememberDefaultLineChartMarker(
    getFormattedValue: (Float) -> String
): CartesianMarker {
    val labelBackgroundShape = Shape.markerCornered(Corner.FullyRounded)
    val labelBackground = rememberShapeComponent(labelBackgroundShape, MaterialTheme.colorScheme.onBackground)
    val label = rememberTextComponent(
        color = Color.Black,
        background = labelBackground,
        margins = Dimensions.of(bottom = Space16),
        textAlignment = Layout.Alignment.ALIGN_CENTER,
        minWidth = TextComponent.MinWidth.fixed(Space48.value)
    )

    val indicatorRearComponent = rememberShapeComponent(
        Shape.Pill,
        MaterialTheme.colorScheme.onBackground
    ).setShadow(Space12.value)
    val indicatorFrontComponent = rememberShapeComponent(Shape.Pill, colorPrimary)
    val indicator = rememberLayeredComponent(
        rear = indicatorRearComponent,
        front = indicatorFrontComponent,
        padding = Dimensions.of(Space4)
    )

    return remember(label, indicator) {
        object: DefaultCartesianMarker(
            label = label,
            valueFormatter = { _, targets ->
                (targets.last() as? LineCartesianLayerMarkerTarget)?.let { target ->
                    getFormattedValue(target.points.last().entry.y)
                }.orEmpty()
            },
            labelPosition = LabelPosition.AbovePoint, // We don't use persistent marker so, this is okay.
            indicator = indicator,
            indicatorSizeDp = Space24.value,
            guideline = null // I don't want any guidelines when the marker is shown.
        ) {}
    }
}