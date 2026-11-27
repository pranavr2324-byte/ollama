package com.vibe.music.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val VibeBackground = Color(0xFF0A0A0E)
val VibeSurface = Color(0xFF14141B)
val VibeSurfaceElevated = Color(0xFF1E1E28)
val VibeSurfaceGlass = Color(0x3328283A)
val VibeSurfaceGlassBorder = Color(0x44FFFFFF)

val VibePrimary = Color(0xFF8B5CF6)       // Electric Violet
val VibePrimaryVariant = Color(0xFF7C3AED)
val VibeSecondary = Color(0xFF06B6D4)     // Neon Cyan
val VibeAccentPink = Color(0xFFEC4899)    // Hot Magenta
val VibeAccentGreen = Color(0xFF10B981)   // Acid Emerald
val VibeAccentOrange = Color(0xFFF97316)  // Sun Orange

val VibeTextPrimary = Color(0xFFF8FAFC)   // Crisp White
val VibeTextSecondary = Color(0xFF94A3B8) // Slate Gray
val VibeTextMuted = Color(0xFF64748B)     // Dark Slate Muted

val PrimaryGradient = Brush.linearGradient(
    colors = listOf(VibePrimary, VibeSecondary)
)

val MagentaVioletGradient = Brush.linearGradient(
    colors = listOf(VibeAccentPink, VibePrimary)
)

val DarkSurfaceGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF20202E), Color(0xFF101016))
)

val PlayerBackgroundGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF261947), Color(0xFF0E0E14), Color(0xFF0A0A0E))
)
