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
import com.rfdotech.core.presentation.designsystem.FontSize12
import com.rfdotech.core.presentation.designsystem.FontSize16
import com.rfdotech.core.presentation.designsystem.Space16
import com.rfdotech.core.presentation.designsystem.colorOnSurfaceVariant

@Composable
fun AnalyticsCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(Space16))
            .background(MaterialTheme.colorScheme.surface)
            .padding(Space16),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            color = colorOnSurfaceVariant,
            fontSize = FontSize12
        )
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = FontSize16
        )
    }
}