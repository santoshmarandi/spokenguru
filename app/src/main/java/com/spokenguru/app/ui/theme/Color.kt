package com.spokenguru.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

// Dynamic Theme Palette getters (backed by LocalAppThemePalette)
val GuruPurple: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppThemePalette.current.primary

val GuruPurpleDark: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppThemePalette.current.primaryDark

val GuruPurpleDeep: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppThemePalette.current.primaryDeep

val GuruPurpleLight: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppThemePalette.current.primaryLight

val GuruPurpleBorder: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppThemePalette.current.primaryBorder

// Status & Accent Colors (Modern, Clean, Minimalist)
val GuruMint = Color(0xFF10B981)
val GuruMintLight = Color(0xFFECFDF5)
val GuruMintDark = Color(0xFF047857)

val GuruRed = Color(0xFFEF4444)
val GuruRedLight = Color(0xFFFEF2F2)
val GuruRedDark = Color(0xFFB91C1C)

val GuruAmber = Color(0xFFF59E0B)
val GuruAmberLight = Color(0xFFFFFBEB)

val GuruBlue = Color(0xFF3B82F6)
val GuruBlueLight = Color(0xFFEFF6FF)

val GuruBg: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppThemePalette.current.lightBg

val GuruSurfaceContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppThemePalette.current.lightSurfaceContainer

val GuruPillBg: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppThemePalette.current.lightPillBg

val GuruProgressTrack: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppThemePalette.current.lightProgressTrack

// Clean Minimalist Neutrals (Dynamic outline + M3 ink)
val GuruCardBorder: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppThemePalette.current.lightCardBorder

val GuruCardBorderDashed: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppThemePalette.current.lightDashedBorder

val GuruTextDark = Color(0xFF1D1B20)
val GuruTextMuted = Color(0xFF49454F)
val GuruTextSubtle = Color(0xFF79747E)

// Dark Theme Variants
val GuruDarkBg: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppThemePalette.current.darkBg

val GuruDarkSurface: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppThemePalette.current.darkSurface

val GuruDarkSurfaceContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppThemePalette.current.darkSurfaceContainer

val GuruDarkPillBg: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppThemePalette.current.darkPillBg

val GuruDarkCardBorder: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppThemePalette.current.darkCardBorder

val GuruDarkText = Color(0xFFF9FAFB)
val GuruDarkTextMuted = Color(0xFF9CA3AF)
