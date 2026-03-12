package com.slimdroid.lumix.theme

import androidx.compose.ui.graphics.Color

internal object Colors {
    /**
     * Light theme colors
     */
    internal object Light {

        // Primary
        internal val primary = Color(0xFFF1BF0D)
        internal val onPrimary = Color(0xFF1A1A1A)
        internal val primaryContainer = Color(0xFFE7E6EB)
        internal val onPrimaryContainer = Color(0xFFEBBB11)
        internal val inversePrimary = Color(0xFFE9B019)

        // Secondary
        internal val secondary = Color(0xFF333333)
        internal val onSecondary = Color(0xFFFFFFFF)
        internal val secondaryContainer = Color(0xFFE1E1EA)
        internal val onSecondaryContainer = Color(0xFF191C20)

        // Tertiary
        internal val tertiary = Color(0xFF29638A)
        internal val onTertiary = Color(0xFFFFFFFF)
        internal val tertiaryContainer = Color(0xFFCBE6FF)
        internal val onTertiaryContainer = Color(0xFF001E30)

        // Surface
        internal val surface = Color(0xFFFDFDFF)
        internal val onSurface = Color(0xFF181819)
        internal val surfaceVariant = Color(0xFFF1F1F7)
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
        internal val outline = Color(0xFF8E8E97)
        internal val outlineVariant = Color(0xFFD1D2DD)

        // Scrim
        internal val scrim = Color(0xFF000000)

        // Surface Container
        internal val surfaceDim = Color(0xFFE6E6EC)
        internal val surfaceBright = Color(0xFFFDFDFF)
        internal val surfaceContainer = Color(0xFFF1F1F8)
        internal val surfaceContainerHigh = Color(0xFFECECF4)
        internal val surfaceContainerHighest = Color(0xFFE6E6F1)
        internal val surfaceContainerLow = Color(0xFFF7F7FD)
        internal val surfaceContainerLowest = Color(0xFFFFFFFF)
    }

    /**
     * Dark theme colors
     */
    internal object Dark {

        // Primary
        internal val primary = Color(0xFF765BF6)
        internal val onPrimary = Color(0xFFFFFFFF)
        internal val primaryContainer = Color(0xFF775AF6)
        internal val onPrimaryContainer = Color(0xFFFFFFFF)
        internal val inversePrimary = Color(0xFF171615)

        // Secondary
        internal val secondary = Color(0xFF605599)
        internal val onSecondary = Color(0xFFFFFFFF)
        internal val secondaryContainer = Color(0xFFC7BBFF)
        internal val onSecondaryContainer = Color(0xFF352A6B)

        // Tertiary
        internal val tertiary = Color(0xFF930E85)
        internal val onTertiary = Color(0xFFFFFFFF)
        internal val tertiaryContainer = Color(0xFFC040AD)
        internal val onTertiaryContainer = Color(0xFFFFFFFF)

        // Surface
        internal val surface = Color(0xFF111318)
        internal val onSurface = Color(0xFFEDEDF5)
        internal val surfaceVariant = Color(0xFF37393E)
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
        internal val outline = Color(0xFF91909A)
        internal val outlineVariant = Color(0xFF46464F)

        // Scrim
        internal val scrim = Color(0xFF000000)

        // Surface Container
        internal val surfaceDim = Color(0xFF111318)
        internal val surfaceBright = Color(0xFF37393E)
        internal val surfaceContainer = Color(0xFF1D2024)
        internal val surfaceContainerHigh = Color(0xFF282A2F)
        internal val surfaceContainerHighest = Color(0xFF33353A)
        internal val surfaceContainerLow = Color(0xFF191C20)
        internal val surfaceContainerLowest = Color(0xFF0C0E13)
    }
}