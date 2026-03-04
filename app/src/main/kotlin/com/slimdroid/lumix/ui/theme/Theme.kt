package com.slimdroid.lumix.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowInsetsControllerCompat

private val LightColorScheme = lightColorScheme(
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

private val DarkColorScheme = darkColorScheme(
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

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(2.dp),
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

private val AppTypography = Typography(
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    )
)

internal val LocalThemeIsDark = compositionLocalOf { mutableStateOf(true) }

@Composable
internal fun AppTheme(
    content: @Composable () -> Unit
) {
    val systemIsDark = isSystemInDarkTheme()
    val isDarkState = remember { mutableStateOf(systemIsDark) }
    CompositionLocalProvider(
        LocalThemeIsDark provides isDarkState
    ) {
        val isDark by isDarkState
        val view = LocalView.current
        LaunchedEffect(isDark) {
            val window = (view.context as Activity).window
            WindowInsetsControllerCompat(window, window.decorView).apply {
                isAppearanceLightStatusBars = isDark
                isAppearanceLightNavigationBars = isDark
            }
        }
        MaterialTheme(
            colorScheme = if (isDark) DarkColorScheme else LightColorScheme,
            typography = AppTypography,
            shapes = AppShapes,
            content = { Surface(content = content) }
        )
    }
}

