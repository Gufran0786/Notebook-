package com.example.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

data class InkColorPreset(
    val name: String,
    val hex: String,
    val category: String = "Classic"
)

object ColorUtils {

    val curatedInks = listOf(
        InkColorPreset("Onyx Black", "#1A1A1A", "Classic"),
        InkColorPreset("Royal Navy", "#1A365D", "Blue & Cyan"),
        InkColorPreset("Oxford Blue", "#0A2540", "Blue & Cyan"),
        InkColorPreset("Cobalt Sapphire", "#0D47A1", "Blue & Cyan"),
        InkColorPreset("Ocean Teal", "#005F73", "Blue & Cyan"),
        InkColorPreset("Emerald Forest", "#1B4D3E", "Green"),
        InkColorPreset("British Green", "#004225", "Green"),
        InkColorPreset("Forest Pine", "#2E7D32", "Green"),
        InkColorPreset("Deep Burgundy", "#58111A", "Red & Wine"),
        InkColorPreset("Bordeaux Red", "#800020", "Red & Wine"),
        InkColorPreset("Rich Crimson", "#C62828", "Red & Wine"),
        InkColorPreset("Imperial Purple", "#4A154B", "Purple & Plum"),
        InkColorPreset("Amethyst Violet", "#6A1B9A", "Purple & Plum"),
        InkColorPreset("Dark Espresso", "#3E2723", "Earth & Brown"),
        InkColorPreset("Vintage Sepia", "#704214", "Earth & Brown"),
        InkColorPreset("Burnt Terracotta", "#8B3A1C", "Earth & Brown"),
        InkColorPreset("Slate Graphite", "#37474F", "Monochrome"),
        InkColorPreset("Deep Charcoal", "#263238", "Monochrome"),
        InkColorPreset("Rose Mahogany", "#4E342E", "Earth & Brown"),
        InkColorPreset("Midnight Cyan", "#004D40", "Blue & Cyan")
    )

    val quickFavorites = listOf(
        "#1A1A1A", // Black
        "#1A365D", // Royal Navy
        "#1B4D3E", // Emerald
        "#58111A", // Burgundy
        "#4A154B", // Purple
        "#3E2723", // Espresso
        "#005F73"  // Ocean Teal
    )

    fun parseColor(hex: String?, fallback: Color = Color(0xFF1A1A1A)): Color {
        if (hex.isNullOrBlank()) return fallback
        return try {
            val cleanHex = hex.trim().removePrefix("#")
            when (cleanHex.length) {
                6 -> Color(android.graphics.Color.parseColor("#$cleanHex"))
                8 -> Color(android.graphics.Color.parseColor("#$cleanHex"))
                3 -> {
                    val r = cleanHex[0]
                    val g = cleanHex[1]
                    val b = cleanHex[2]
                    Color(android.graphics.Color.parseColor("#$r$r$g$g$b$b"))
                }
                else -> fallback
            }
        } catch (e: Exception) {
            fallback
        }
    }

    fun toHex(color: Color): String {
        val argb = color.toArgb()
        val r = (argb shr 16) and 0xFF
        val g = (argb shr 8) and 0xFF
        val b = argb and 0xFF
        return String.format("#%02X%02X%02X", r, g, b)
    }

    fun isLegibleOnWhite(color: Color): Boolean {
        val argb = color.toArgb()
        val r = ((argb shr 16) and 0xFF) / 255f
        val g = ((argb shr 8) and 0xFF) / 255f
        val b = (argb and 0xFF) / 255f
        // Standard relative luminance formula
        val luminance = 0.2126f * r + 0.7152f * g + 0.0722f * b
        return luminance < 0.75f // Must not be too close to pure white (1.0)
    }
}
