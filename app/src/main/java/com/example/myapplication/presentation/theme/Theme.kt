package com.example.myapplication.presentation.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ================= LIGHT =================

private val LightColorScheme = lightColorScheme(

    primary = BrandPrimary,
    onPrimary = NeutralWhite,
    primaryContainer = Color(0xFFDDE9FF),
    onPrimaryContainer = Color(0xFF001C3A),

    secondary = BrandSecondary,
    onSecondary = NeutralBlack,
    secondaryContainer = Color(0xFFFFE0B2),
    onSecondaryContainer = Color(0xFF3E2723),

    tertiary = BrandTertiary,
    onTertiary = NeutralWhite,
    tertiaryContainer = Color(0xFFC8FACC),
    onTertiaryContainer = Color(0xFF003300),

    background = NeutralWhite,
    onBackground = NeutralBlack,

    surface = NeutralGray100,
    onSurface = NeutralBlack,
    surfaceVariant = NeutralGray200,
    onSurfaceVariant = NeutralGray800,

    surfaceTint = BrandPrimary,

    inverseSurface = NeutralGray800,
    inverseOnSurface = NeutralWhite,

    error = ErrorRed,
    onError = NeutralWhite,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    outline = NeutralGray400,
    outlineVariant = NeutralGray200,

    scrim = Color(0x99000000)
)

// ================= DARK =================

private val DarkColorScheme = darkColorScheme(

    primary = Color(0xFF4D9FFF),
    onPrimary = NeutralBlack,
    primaryContainer = Color(0xFF003366),
    onPrimaryContainer = Color(0xFFDDE9FF),

    secondary = Color(0xFFFFB74D),
    onSecondary = NeutralBlack,
    secondaryContainer = Color(0xFF5D4037),
    onSecondaryContainer = Color(0xFFFFE0B2),

    tertiary = Color(0xFF69F0AE),
    onTertiary = NeutralBlack,
    tertiaryContainer = Color(0xFF004D40),
    onTertiaryContainer = Color(0xFFC8FACC),

    background = Color(0xFF121212),
    onBackground = NeutralWhite,

    surface = Color(0xFF1E1E1E),
    onSurface = NeutralWhite,
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = NeutralGray400,

    surfaceTint = Color(0xFF4D9FFF),

    inverseSurface = NeutralWhite,
    inverseOnSurface = NeutralBlack,

    error = ErrorDark,
    onError = NeutralBlack,
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFFFDAD6),

    outline = NeutralGray600,
    outlineVariant = Color(0xFF3A3A3A),

    scrim = Color(0x99000000)
)

@Composable
fun MovieAppTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = AppTypography,
        content = content
    )
}