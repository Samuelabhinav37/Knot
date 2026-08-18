package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val PastelColorScheme = lightColorScheme(
    primary = MintGreenDark,
    onPrimary = CloudWhite,
    primaryContainer = MintGreenLight,
    onPrimaryContainer = SlateText,
    secondary = BlushPinkDark,
    onSecondary = CloudWhite,
    secondaryContainer = BlushPinkLight,
    onSecondaryContainer = SlateText,
    tertiary = SkyBlueDark,
    onTertiary = CloudWhite,
    tertiaryContainer = SkyBlueLight,
    onTertiaryContainer = SlateText,
    background = CreamBackground,
    onBackground = SlateText,
    surface = CloudWhite,
    onSurface = SlateText,
    surfaceVariant = LilacLight,
    onSurfaceVariant = SlateMuted
)

private val PastelNightColorScheme = darkColorScheme(
    primary = MintGreenPrimary,
    onPrimary = SlateText,
    primaryContainer = MintGreenDark,
    onPrimaryContainer = CloudWhite,
    secondary = BlushPink,
    onSecondary = SlateText,
    secondaryContainer = BlushPinkDark,
    onSecondaryContainer = CloudWhite,
    tertiary = SkyBlue,
    onTertiary = SlateText,
    tertiaryContainer = SkyBlueDark,
    onTertiaryContainer = CloudWhite,
    background = Color(0xFF1E2430),
    onBackground = CloudWhite,
    surface = Color(0xFF283040),
    onSurface = CloudWhite,
    surfaceVariant = Color(0xFF333E54),
    onSurfaceVariant = LilacLavender
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep bespoke pastel aesthetic consistent
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) PastelNightColorScheme else PastelColorScheme

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

