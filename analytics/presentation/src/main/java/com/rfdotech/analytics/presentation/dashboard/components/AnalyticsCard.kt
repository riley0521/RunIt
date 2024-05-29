package com.rfdotech.analytics.presentation.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import com.rfdotech.analytics.presentation.dashboard.model.AnalyticsDataUi
import com.rfdotech.core.presentation.designsystem.FontSize12
import com.rfdotech.core.presentation.designsystem.FontSize16
import com.rfdotech.core.presentation.designsystem.Space16
import com.rfdotech.core.presentation.designsystem.colorOnSurfaceVariant

@Composable
fun AnalyticsCard(
    data: AnalyticsDataUi,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(Space16))
            .background(MaterialTheme.colorScheme.surface)
            .padding(Space16)
            .clearAndSetSemantics {
                // Should be title + value in an understandable format
                // E.g. 'Total time run . 1 day 2 hours 45 minutes'
               contentDescription = data.contentDesc
            },
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = data.title,
            color = colorOnSurfaceVariant,
            fontSize = FontSize12
        )
        Text(
            text = data.displayedValue,
            fontSize = FontSize16
        )
    }
}