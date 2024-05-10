package com.rfdotech.core.presentation.designsystem.components.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val colorOnBackGround: Color
    @Composable
    get() = MaterialTheme.colorScheme.onBackground

val colorOnSurfaceVariant: Color
    @Composable
    get() = MaterialTheme.colorScheme.onSurfaceVariant