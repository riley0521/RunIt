package com.rfdotech.core.presentation.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import com.rfdotech.core.presentation.designsystem.FontSize12
import com.rfdotech.core.presentation.designsystem.Space12
import com.rfdotech.core.presentation.designsystem.Space16

@Composable
fun MyDialog(
    title: String,
    description: String,
    positiveButton: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    negativeButton: @Composable RowScope.() -> Unit = {},
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = modifier
                .clip(RoundedCornerShape(Space16))
                .background(MaterialTheme.colorScheme.surface)
                .padding(Space16),
            verticalArrangement = Arrangement.spacedBy(Space12),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                fontSize = FontSize12,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Space16),
                verticalAlignment = Alignment.CenterVertically
            ) {
                negativeButton()
                positiveButton()
            }
        }
    }
}