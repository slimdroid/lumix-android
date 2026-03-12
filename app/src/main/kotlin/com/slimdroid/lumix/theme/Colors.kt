package com.slimdroid.lumix.theme

import androidx.compose.ui.graphics.Color

internal object Colors {
    /**
     * Light theme colors
     */
    internal object Light {
        // Rainbow Palette
        private val violet = Color(0xFFD500F9)
        private val cyan = Color(0xFF00E5FF)
        private val lime = Color(0xFFAEEA00)
        private val orange = Color(0xFFFF9100)
        private val blue = Color(0xFF2979FF)

        // Primary
        internal val primary = violet
        internal val onPrimary = Color(0xFFFFFFFF)
        internal val primaryContainer = violet.copy(alpha = 0.2f)
        internal val onPrimaryContainer = violet
        internal val inversePrimary = Color(0xFFE9B019)

        // Secondary
        internal val secondary = cyan
        internal val onSecondary = Color(0xFF1A1A1A)
        internal val secondaryContainer = cyan.copy(alpha = 0.2f)
        internal val onSecondaryContainer = cyan

        // Tertiary
        internal val tertiary = orange
        internal val onTertiary = Color(0xFFFFFFFF)
        internal val tertiaryContainer = orange.copy(alpha = 0.2f)
        internal val onTertiaryContainer = orange

        // Surface (Semi-transparent)
        internal val surface = Color(0xCCFDFDFF)
        internal val onSurface = Color(0xFF181819)
        internal val surfaceVariant = Color(0x99F1F1F7)
        internal val onSurfaceVariant = Color(0xFF616168)
        internal val inverseSurface = Color(0xFF202122)
        internal val inverseOnSurface = Color(0xFFF0F0F7)

        // Background
        internal val background = surface
        internal val onBackground = onSurface

        // Error
        internal val error = Color(0xFFE41717)
        internal val onError = Color(0xFFFFFFFF)
        internal val errorContainer = Color(0xFFFFE8E8)
        internal val onErrorContainer = Color(0xFF980E0E)

        // Outline
        internal val outline = cyan
        internal val outlineVariant = violet

        // Scrim
        internal val scrim = Color(0x66000000)

        // Surface Container
        internal val surfaceDim = Color(0xCCE6E6EC)
        internal val surfaceBright = Color(0xCCFDFDFF)
        internal val surfaceContainer = Color(0x99F1F1F8)
        internal val surfaceContainerHigh = Color(0x99ECECF4)
        internal val surfaceContainerHighest = Color(0x99E6E6F1)
        internal val surfaceContainerLow = Color(0xAAF7F7FD)
        internal val surfaceContainerLowest = Color(0xCCFFFFFF)
    }

    /**
     * Dark theme colors
     */
    internal object Dark {
        // Rainbow Palette
        private val violet = Color(0xFFD500F9)
        private val cyan = Color(0xFF00E5FF)
        private val lime = Color(0xFFAEEA00)
        private val orange = Color(0xFFFF9100)
        private val blue = Color(0xFF2979FF)

        // Primary
        internal val primary = violet
        internal val onPrimary = Color(0xFFFFFFFF)
        internal val primaryContainer = violet.copy(alpha = 0.3f)
        internal val onPrimaryContainer = Color(0xFFFFFFFF)
        internal val inversePrimary = Color(0xFF171615)

        // Secondary
        internal val secondary = cyan
        internal val onSecondary = Color(0xFF111318)
        internal val secondaryContainer = cyan.copy(alpha = 0.3f)
        internal val onSecondaryContainer = Color(0xFFFFFFFF)

        // Tertiary
        internal val tertiary = lime
        internal val onTertiary = Color(0xFF111318)
        internal val tertiaryContainer = lime.copy(alpha = 0.3f)
        internal val onTertiaryContainer = Color(0xFFFFFFFF)

        // Surface (Semi-transparent)
        internal val surface = Color(0xCC111318)
        internal val onSurface = Color(0xFFEDEDF5)
        internal val surfaceVariant = Color(0x9937393E)
        internal val onSurfaceVariant = Color(0xFFC7C5D0)
        internal val inverseSurface = Color(0xFFE2E2E9)
        internal val inverseOnSurface = Color(0xFFC7C5D0)

        // Background
        internal val background = surface
        internal val onBackground = onSurface

        // Error
        internal val error = Color(0xFFF32525)
        internal val onError = Color(0xFFFFFFFF)
        internal val errorContainer = Color(0xFF73332D)
        internal val onErrorContainer = Color(0xFFFFDAD6)

        // Outline
        internal val outline = cyan
        internal val outlineVariant = violet

        // Scrim
        internal val scrim = Color(0x99000000)

        // Surface Container
        internal val surfaceDim = Color(0xCC111318)
        internal val surfaceBright = Color(0xCC37393E)
        internal val surfaceContainer = Color(0x991D2024)
        internal val surfaceContainerHigh = Color(0x99282A2F)
        internal val surfaceContainerHighest = Color(0x9933353A)
        internal val surfaceContainerLow = Color(0xAA191C20)
        internal val surfaceContainerLowest = Color(0xCC0C0E13)
    }
}