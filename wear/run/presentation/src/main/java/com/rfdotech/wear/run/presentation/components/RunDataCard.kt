package com.rfdotech.wear.run.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.rfdotech.core.presentation.designsystem.FontSize10
import com.rfdotech.core.presentation.designsystem.FontSize12
import com.rfdotech.core.presentation.designsystem.Space16
import com.rfdotech.core.presentation.designsystem.Space8

@Composable
fun RunDataCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    valueTextColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(Space16))
            .background(MaterialTheme.colorScheme.surface)
            .padding(Space8)
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = FontSize10
        )
        Text(
            text = value,
            fontSize = FontSize12,
            color = valueTextColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}