package com.rfdotech.core.presentation.designsystem

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.rfdotech.core.presentation.FontSize12
import com.rfdotech.core.presentation.FontSize14
import com.rfdotech.core.presentation.FontSize16
import com.rfdotech.core.presentation.FontSize18
import com.rfdotech.core.presentation.FontSize24

val primaryFontFamily = FontFamily(
    Font(resId = R.font.poppins_light, weight = FontWeight.Light),
    Font(resId = R.font.poppins_regular, weight = FontWeight.Normal),
    Font(resId = R.font.poppins_medium, weight = FontWeight.Medium),
    Font(resId = R.font.poppins_semibold, weight = FontWeight.SemiBold),
    Font(resId = R.font.poppins_bold, weight = FontWeight.Bold)
)

val typography = Typography(
    bodySmall = TextStyle(
        fontFamily = primaryFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = FontSize12,
        lineHeight = FontSize18
    ),
    bodyMedium = TextStyle(
        fontFamily = primaryFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = FontSize14,
        lineHeight = FontSize24
    ),
    bodyLarge = TextStyle(
        fontFamily = primaryFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = FontSize16,
        lineHeight = FontSize24,
        letterSpacing = 0.5.sp
    ),
    labelLarge = TextStyle(
        fontFamily = primaryFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = FontSize14,
        lineHeight = FontSize24
    ),
    headlineSmall = TextStyle(
        fontFamily = primaryFontFamily,
        fontSize = FontSize18
    ),
    headlineMedium = TextStyle(
        fontFamily = primaryFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = FontSize24
    )
)