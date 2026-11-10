package com.spokenguru.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun SpokenGuruTheme(
    darkModePref: DarkModePreference = ThemeState.darkModePref,
    preset: AppThemePreset = ThemeState.currentPreset,
    content: @Composable () -> Unit
) {
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (darkModePref) {
        DarkModePreference.SYSTEM -> isSystemDark
        DarkModePreference.LIGHT -> false
        DarkModePreference.DARK -> true
    }
    val palette = getThemePalette(preset)

    val colorScheme = if (isDark) {
        darkColorScheme(
            primary = palette.primary,
            primaryContainer = palette.darkPillBg,
            onPrimaryContainer = Color.White,
            secondary = GuruBlue,
            tertiary = GuruAmber,
            background = palette.darkBg,
            surface = palette.darkSurface,
            surfaceVariant = palette.darkSurfaceContainer,
            outline = palette.darkCardBorder,
            onPrimary = Color.White,
            onSecondary = Color.White,
            onTertiary = Color.Black,
            onBackground = GuruDarkText,
            onSurface = GuruDarkText,
            error = GuruRed
        )
    } else {
        lightColorScheme(
            primary = palette.primary,
            primaryContainer = palette.primaryLight,
            onPrimaryContainer = palette.primaryDeep,
            secondary = GuruBlue,
            tertiary = GuruAmber,
            background = palette.lightBg,
            surface = Color.White,
            surfaceVariant = palette.lightSurfaceContainer,
            outline = palette.lightCardBorder,
            onPrimary = Color.White,
            onSecondary = Color.White,
            onTertiary = Color.Black,
            onBackground = GuruTextDark,
            onSurface = GuruTextDark,
            error = GuruRed
        )
    }

    CompositionLocalProvider(
        LocalAppThemePalette provides palette,
        LocalIsDarkTheme provides isDark
    ) {
        val view = LocalView.current
        if (!view.isInEditMode) {
            SideEffect {
                val window = (view.context as Activity).window
                window.statusBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
            }
        }

        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
