package com.spokenenglishguru.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = IndigoLight,
    onPrimary = DarkSurface,
    secondary = AmberAccent,
    onSecondary = DarkBackground,
    tertiary = TealAccent,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary,
    onSurfaceVariant = DarkTextSecondary
)

private val LightColorScheme = lightColorScheme(
    primary = IndigoPrimary,
    onPrimary = LightSurface,
    secondary = AmberAccent,
    onSecondary = LightSurface,
    tertiary = TealAccent,
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = LightSurfaceVariant,
    onBackground = LightTextPrimary,
    onSurface = LightTextPrimary,
    onSurfaceVariant = LightTextSecondary
)

@Composable
fun SpokenEnglishTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            try {
                val window = (view.context as? Activity)?.window
                if (window != null) {
                    window.statusBarColor = if (darkTheme) DarkBackground.toArgb() else IndigoDark.toArgb()
                    WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
                }
            } catch (e: Exception) {
                // Ignore status bar adjustments on OEM devices that restrict it
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
