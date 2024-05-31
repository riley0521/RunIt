package com.rfdotech.core.presentation.designsystem_wear

import androidx.compose.runtime.Composable
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Typography
import com.rfdotech.core.presentation.designsystem.DarkColorScheme
import com.rfdotech.core.presentation.designsystem.primaryFontFamily

private fun createColorScheme(): ColorScheme {
    val phoneTheme = DarkColorScheme

    return with(phoneTheme) {
        ColorScheme(
            primary = primary,
            onPrimary = onPrimary,
            primaryContainer = primaryContainer,
            onPrimaryContainer = onPrimaryContainer,
            secondary = secondary,
            onSecondary = onSecondary,
            secondaryContainer = secondaryContainer,
            onSecondaryContainer = onSecondaryContainer,
            tertiary = tertiary,
            onTertiary = onTertiary,
            tertiaryContainer = tertiaryContainer,
            onTertiaryContainer = onTertiaryContainer,
            surface = surface,
            onSurface = onSurface,
            onSurfaceVariant = onSurfaceVariant,
            surfaceDim = surfaceDim,
            background = background,
            onBackground = onBackground,
            error = error,
            onError = onError
        )
    }
}

private val Colors = createColorScheme()
private val Type = Typography(
    defaultFontFamily = primaryFontFamily
)

@Composable
fun RunItWearTheme(
    content: @Composable () -> Unit
) {

    MaterialTheme(
        colorScheme = Colors,
        typography = Type,
        content = content
    )
}