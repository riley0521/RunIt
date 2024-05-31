package com.rfdotech.wear.app.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun RunItWearTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        content = content
    )
}