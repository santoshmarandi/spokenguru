package com.spokenguru.app.ui.theme

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

enum class DarkModePreference(val label: String, val icon: String, val hindiLabel: String) {
    SYSTEM("Auto", "⚙️", "सिस्टम अनुसार"),
    LIGHT("Light", "☀️", "दिन मोड"),
    DARK("Dark", "🌙", "रात मोड")
}

enum class AppThemePreset(
    val title: String,
    val subtitle: String,
    val primaryColor: Color,
    val secondaryColor: Color
) {
    LAVENDER_M3(
        title = "Lavender M3",
        subtitle = "मॉडर्न लैवेंडर (Studio UI)",
        primaryColor = Color(0xFF6750A4),
        secondaryColor = Color(0xFF4C239A)
    ),
    ROYAL_PURPLE(
        title = "Royal Purple",
        subtitle = "शाही बैंगनी (Classic)",
        primaryColor = Color(0xFF4F46E5),
        secondaryColor = Color(0xFF2B228B)
    ),
    DUO_GREEN(
        title = "Duolingo Green",
        subtitle = "डुओलिंगो हरा (Vibrant)",
        primaryColor = Color(0xFF58CC02),
        secondaryColor = Color(0xFF2F7A00)
    ),
    OCEAN_BLUE(
        title = "Ocean Blue",
        subtitle = "समुद्री नीला (Modern)",
        primaryColor = Color(0xFF0284C7),
        secondaryColor = Color(0xFF075985)
    ),
    SUNSET_AMBER(
        title = "Sunset Coral",
        subtitle = "सूर्यास्त नारंगी (Warm)",
        primaryColor = Color(0xFFEA580C),
        secondaryColor = Color(0xFF9A3412)
    )
}

data class AppThemePalette(
    val primary: Color,
    val primaryDark: Color,
    val primaryDeep: Color,
    val primaryLight: Color,
    val primaryBorder: Color,
    val lightBg: Color,
    val lightSurfaceContainer: Color,
    val lightPillBg: Color,
    val lightProgressTrack: Color,
    val lightCardBorder: Color,
    val lightDashedBorder: Color,
    val darkBg: Color,
    val darkSurface: Color,
    val darkSurfaceContainer: Color,
    val darkPillBg: Color,
    val darkCardBorder: Color
)

fun getThemePalette(preset: AppThemePreset): AppThemePalette {
    return when (preset) {
        AppThemePreset.LAVENDER_M3 -> AppThemePalette(
            primary = Color(0xFF6750A4),
            primaryDark = Color(0xFF4F378B),
            primaryDeep = Color(0xFF4C239A),
            primaryLight = Color(0xFFE8DEF8),
            primaryBorder = Color(0xFFC4BAD7),
            lightBg = Color(0xFFFEF7FF),
            lightSurfaceContainer = Color(0xFFF7F2FA),
            lightPillBg = Color(0xFFF3EDF7),
            lightProgressTrack = Color(0xFFE6E0E9),
            lightCardBorder = Color(0xFFC8C1D1),
            lightDashedBorder = Color(0xFFC4BAD7),
            darkBg = Color(0xFF141218),
            darkSurface = Color(0xFF211F26),
            darkSurfaceContainer = Color(0xFF2B2930),
            darkPillBg = Color(0xFF2F2A3A),
            darkCardBorder = Color(0xFF443E54)
        )
        AppThemePreset.ROYAL_PURPLE -> AppThemePalette(
            primary = Color(0xFF4F46E5),
            primaryDark = Color(0xFF3730A3),
            primaryDeep = Color(0xFF2B228B),
            primaryLight = Color(0xFFEEECFD),
            primaryBorder = Color(0xFFC7D2FE),
            lightBg = Color(0xFFF9FAFB),
            lightSurfaceContainer = Color(0xFFF3F4FB),
            lightPillBg = Color(0xFFEEF0FA),
            lightProgressTrack = Color(0xFFE5E7EB),
            lightCardBorder = Color(0xFFD5D9E4),
            lightDashedBorder = Color(0xFFBCC3D7),
            darkBg = Color(0xFF0F172A),
            darkSurface = Color(0xFF1E293B),
            darkSurfaceContainer = Color(0xFF243147),
            darkPillBg = Color(0xFF27354D),
            darkCardBorder = Color(0xFF334155)
        )
        AppThemePreset.DUO_GREEN -> AppThemePalette(
            primary = Color(0xFF58CC02),
            primaryDark = Color(0xFF46A302),
            primaryDeep = Color(0xFF2F7A00),
            primaryLight = Color(0xFFE9FCDA),
            primaryBorder = Color(0xFFBBF7D0),
            lightBg = Color(0xFFF9FBF8),
            lightSurfaceContainer = Color(0xFFF2F9EE),
            lightPillBg = Color(0xFFECF7E6),
            lightProgressTrack = Color(0xFFE2ECE0),
            lightCardBorder = Color(0xFFD1DFCC),
            lightDashedBorder = Color(0xFFB7D4AF),
            darkBg = Color(0xFF0A140D),
            darkSurface = Color(0xFF132218),
            darkSurfaceContainer = Color(0xFF192D1F),
            darkPillBg = Color(0xFF1C3223),
            darkCardBorder = Color(0xFF1F3827)
        )
        AppThemePreset.OCEAN_BLUE -> AppThemePalette(
            primary = Color(0xFF0284C7),
            primaryDark = Color(0xFF0369A1),
            primaryDeep = Color(0xFF075985),
            primaryLight = Color(0xFFE0F2FE),
            primaryBorder = Color(0xFFBAE6FD),
            lightBg = Color(0xFFF8FAFC),
            lightSurfaceContainer = Color(0xFFF0F7FC),
            lightPillBg = Color(0xFFEAF4FB),
            lightProgressTrack = Color(0xFFDFECF5),
            lightCardBorder = Color(0xFFCBDCE8),
            lightDashedBorder = Color(0xFFAECBE0),
            darkBg = Color(0xFF0C141F),
            darkSurface = Color(0xFF152232),
            darkSurfaceContainer = Color(0xFF1A2A3D),
            darkPillBg = Color(0xFF1F3248),
            darkCardBorder = Color(0xFF22354D)
        )
        AppThemePreset.SUNSET_AMBER -> AppThemePalette(
            primary = Color(0xFFEA580C),
            primaryDark = Color(0xFFC2410C),
            primaryDeep = Color(0xFF9A3412),
            primaryLight = Color(0xFFFFEDE6),
            primaryBorder = Color(0xFFFED7AA),
            lightBg = Color(0xFFFFFBF8),
            lightSurfaceContainer = Color(0xFFFDF3EE),
            lightPillBg = Color(0xFFFAEEE8),
            lightProgressTrack = Color(0xFFF1E3DB),
            lightCardBorder = Color(0xFFE4D1C7),
            lightDashedBorder = Color(0xFFD8BCB0),
            darkBg = Color(0xFF18100C),
            darkSurface = Color(0xFF271B15),
            darkSurfaceContainer = Color(0xFF31221B),
            darkPillBg = Color(0xFF38271E),
            darkCardBorder = Color(0xFF3E2B21)
        )
    }
}

val LocalAppThemePalette = compositionLocalOf {
    getThemePalette(AppThemePreset.LAVENDER_M3)
}

val LocalIsDarkTheme = compositionLocalOf {
    false
}

@Composable
@ReadOnlyComposable
fun isAppDarkTheme(): Boolean = LocalIsDarkTheme.current

object ThemeState {
    var currentPreset by mutableStateOf(AppThemePreset.LAVENDER_M3)
    var darkModePref by mutableStateOf(DarkModePreference.SYSTEM)

    fun init(context: Context) {
        val prefs = context.getSharedPreferences("spokenguru_theme", Context.MODE_PRIVATE)
        val presetName = prefs.getString("theme_preset", AppThemePreset.LAVENDER_M3.name)
        val modeName = prefs.getString("dark_mode", DarkModePreference.SYSTEM.name)
        currentPreset = try {
            AppThemePreset.valueOf(presetName ?: AppThemePreset.LAVENDER_M3.name)
        } catch (e: Exception) {
            AppThemePreset.LAVENDER_M3
        }
        darkModePref = try {
            DarkModePreference.valueOf(modeName ?: DarkModePreference.SYSTEM.name)
        } catch (e: Exception) {
            DarkModePreference.SYSTEM
        }
    }

    fun updatePreset(context: Context, newPreset: AppThemePreset) {
        currentPreset = newPreset
        context.getSharedPreferences("spokenguru_theme", Context.MODE_PRIVATE)
            .edit()
            .putString("theme_preset", newPreset.name)
            .apply()
    }

    fun updateDarkMode(context: Context, newMode: DarkModePreference) {
        darkModePref = newMode
        context.getSharedPreferences("spokenguru_theme", Context.MODE_PRIVATE)
            .edit()
            .putString("dark_mode", newMode.name)
            .apply()
    }
}
