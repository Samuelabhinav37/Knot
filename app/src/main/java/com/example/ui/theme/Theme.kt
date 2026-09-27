package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SleekDarkColorScheme = darkColorScheme(
    primary = CorporatePrimary,
    onPrimary = Color.Black,
    primaryContainer = CorporatePrimaryLight,
    onPrimaryContainer = Color.White,
    secondary = CorporateAccentBlue,
    onSecondary = Color.Black,
    secondaryContainer = CorporateAccentBlueLight,
    onSecondaryContainer = CorporateAccentBlue,
    tertiary = CorporateAccentTeal,
    onTertiary = Color.Black,
    tertiaryContainer = CorporateAccentTealLight,
    onTertiaryContainer = CorporateAccentTeal,
    background = CorporateBg,
    onBackground = TextPrimary,
    surface = CorporateSurface,
    onSurface = TextPrimary,
    surfaceVariant = CorporateCardBorder,
    onSurfaceVariant = TextSecondary
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SleekDarkColorScheme,
        typography = Typography,
        content = content
    )
}
