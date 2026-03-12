package com.slimdroid.lumix.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun LumixTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    // Subtle background gradient to hint at light control
    val backgroundBrush = if (darkTheme) {
        Brush.radialGradient(
            colors = listOf(
                Color(0xFF20232A),
                Color(0xFF111318)
            )
        )
    } else {
        Brush.radialGradient(
            colors = listOf(
                Color(0xFFF1F1F8),
                Color(0xFFFDFDFF)
            )
        )
    }

    // Very subtle rainbow glow layer
    val rainbowGlow = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFF1744).copy(alpha = 0.05f),
            Color(0xFF00E5FF).copy(alpha = 0.05f),
            Color(0xFFD500F9).copy(alpha = 0.05f)
        )
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = if (darkTheme) Color(0xFF111318) else Color(0xFFFDFDFF)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(rainbowGlow)
            )
            BaseTheme(
                colorScheme = colorScheme,
                content = content
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BaseTheme(
    colorScheme: ColorScheme,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider {
        MaterialExpressiveTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

/**
 * Light theme color scheme
 */
val LightColorScheme = lightColorScheme(
    primary = Colors.Light.primary,
    onPrimary = Colors.Light.onPrimary,
    primaryContainer = Colors.Light.primaryContainer,
    onPrimaryContainer = Colors.Light.onPrimaryContainer,
    inversePrimary = Colors.Light.inversePrimary,
    secondary = Colors.Light.secondary,
    onSecondary = Colors.Light.onSecondary,
    secondaryContainer = Colors.Light.secondaryContainer,
    onSecondaryContainer = Colors.Light.onSecondaryContainer,
    tertiary = Colors.Light.tertiary,
    onTertiary = Colors.Light.onTertiary,
    tertiaryContainer = Colors.Light.tertiaryContainer,
    onTertiaryContainer = Colors.Light.onTertiaryContainer,
    background = Colors.Light.background,
    onBackground = Colors.Light.onBackground,
    surface = Colors.Light.surface,
    onSurface = Colors.Light.onSurface,
    surfaceVariant = Colors.Light.surfaceVariant,
    onSurfaceVariant = Colors.Light.onSurfaceVariant,
    inverseSurface = Colors.Light.inverseSurface,
    inverseOnSurface = Colors.Light.inverseOnSurface,
    error = Colors.Light.error,
    onError = Colors.Light.onError,
    errorContainer = Colors.Light.errorContainer,
    onErrorContainer = Colors.Light.onErrorContainer,
    outline = Colors.Light.outline,
    outlineVariant = Colors.Light.outlineVariant,
    scrim = Colors.Light.scrim,
    surfaceDim = Colors.Light.surfaceDim,
    surfaceBright = Colors.Light.surfaceBright,
    surfaceContainer = Colors.Light.surfaceContainer,
    surfaceContainerHigh = Colors.Light.surfaceContainerHigh,
    surfaceContainerHighest = Colors.Light.surfaceContainerHighest,
    surfaceContainerLow = Colors.Light.surfaceContainerLow,
    surfaceContainerLowest = Colors.Light.surfaceContainerLowest,
)

/**
 * Dark theme color scheme
 */
val DarkColorScheme = darkColorScheme(
    primary = Colors.Dark.primary,
    onPrimary = Colors.Dark.onPrimary,
    primaryContainer = Colors.Dark.primaryContainer,
    onPrimaryContainer = Colors.Dark.onPrimaryContainer,
    inversePrimary = Colors.Dark.inversePrimary,
    secondary = Colors.Dark.secondary,
    onSecondary = Colors.Dark.onSecondary,
    secondaryContainer = Colors.Dark.secondaryContainer,
    onSecondaryContainer = Colors.Dark.onSecondaryContainer,
    tertiary = Colors.Dark.tertiary,
    onTertiary = Colors.Dark.onTertiary,
    tertiaryContainer = Colors.Dark.tertiaryContainer,
    onTertiaryContainer = Colors.Dark.onTertiaryContainer,
    background = Colors.Dark.background,
    onBackground = Colors.Dark.onBackground,
    surface = Colors.Dark.surface,
    onSurface = Colors.Dark.onSurface,
    surfaceVariant = Colors.Dark.surfaceVariant,
    onSurfaceVariant = Colors.Dark.onSurfaceVariant,
    inverseSurface = Colors.Dark.inverseSurface,
    inverseOnSurface = Colors.Dark.inverseOnSurface,
    error = Colors.Dark.error,
    onError = Colors.Dark.onError,
    errorContainer = Colors.Dark.errorContainer,
    onErrorContainer = Colors.Dark.onErrorContainer,
    outline = Colors.Dark.outline,
    outlineVariant = Colors.Dark.outlineVariant,
    scrim = Colors.Dark.scrim,
    surfaceDim = Colors.Dark.surfaceDim,
    surfaceBright = Colors.Dark.surfaceBright,
    surfaceContainer = Colors.Dark.surfaceContainer,
    surfaceContainerHigh = Colors.Dark.surfaceContainerHigh,
    surfaceContainerHighest = Colors.Dark.surfaceContainerHighest,
    surfaceContainerLow = Colors.Dark.surfaceContainerLow,
    surfaceContainerLowest = Colors.Dark.surfaceContainerLowest,
)
