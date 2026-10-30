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

// Clean Minimalist Neutrals
val GuruCardBorder = Color(0xFFE5E7EB)
val GuruCardBorderDashed = Color(0xFFD1D5DB)
val GuruTextDark = Color(0xFF111827)
val GuruTextMuted = Color(0xFF4B5563)
val GuruTextSubtle = Color(0xFF9CA3AF)

// Dark Theme Variants
val GuruDarkBg: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppThemePalette.current.darkBg

val GuruDarkSurface: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppThemePalette.current.darkSurface

val GuruDarkCardBorder: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppThemePalette.current.darkCardBorder

val GuruDarkText = Color(0xFFF9FAFB)
val GuruDarkTextMuted = Color(0xFF9CA3AF)
