package com.example.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

data class ColoredLetter(
    val char: Char,
    val hexColor: String? = null // null means default page ink
)

object RichTextHelper {

    // 16 vibrant curated letter colors covering the full color spectrum
    val letterColorPalette = listOf(
        InkColorPreset("Crimson Red", "#E53935", "Vibrant"),
        InkColorPreset("Coral Orange", "#FF5722", "Vibrant"),
        InkColorPreset("Sunset Amber", "#FB8C00", "Vibrant"),
        InkColorPreset("Golden Yellow", "#FBC02D", "Vibrant"),
        InkColorPreset("Lime Green", "#7CB342", "Vibrant"),
        InkColorPreset("Emerald Green", "#2E7D32", "Vibrant"),
        InkColorPreset("Ocean Teal", "#00897B", "Vibrant"),
        InkColorPreset("Vivid Cyan", "#00ACC1", "Vibrant"),
        InkColorPreset("Sky Azure", "#039BE5", "Vibrant"),
        InkColorPreset("Royal Blue", "#1E88E5", "Vibrant"),
        InkColorPreset("Deep Indigo", "#3949AB", "Vibrant"),
        InkColorPreset("Amethyst Violet", "#8E24AA", "Vibrant"),
        InkColorPreset("Electric Purple", "#AB47BC", "Vibrant"),
        InkColorPreset("Hot Pink / Magenta", "#D81B60", "Vibrant"),
        InkColorPreset("Rose Wine", "#880E4F", "Vibrant"),
        InkColorPreset("Espresso Brown", "#5D4037", "Classic"),
        InkColorPreset("Slate Charcoal", "#455A64", "Classic"),
        InkColorPreset("Pure Onyx", "#1A1A1A", "Classic")
    )

    val rainbowSpectrum = listOf(
        "#E53935", // Red
        "#FF5722", // Orange
        "#FB8C00", // Amber
        "#FDD835", // Yellow
        "#43A047", // Green
        "#00ACC1", // Cyan
        "#1E88E5", // Blue
        "#5E35B1", // Indigo
        "#8E24AA", // Purple
        "#D81B60"  // Pink
    )

    private val colorTagRegex = Regex("""<c:(#[0-9a-fA-F]{6}|#[0-9a-fA-F]{8})>(.*?)</c>""", RegexOption.DOT_MATCHES_ALL)

    /**
     * Parses markup like <c:#E53935>Hello</c> into an AnnotatedString with proper SpanStyle colors.
     */
    fun parseToAnnotatedString(rawText: String, defaultColor: Color): AnnotatedString {
        if (!rawText.contains("<c:")) {
            return AnnotatedString(rawText)
        }

        return buildAnnotatedString {
            var lastIndex = 0
            val matches = colorTagRegex.findAll(rawText)

            for (match in matches) {
                // Append text before the match
                if (match.range.first > lastIndex) {
                    append(rawText.substring(lastIndex, match.range.first))
                }

                val hex = match.groupValues[1]
                val content = match.groupValues[2]
                val color = ColorUtils.parseColor(hex, defaultColor)

                val start = length
                append(content)
                val end = length
                addStyle(SpanStyle(color = color), start, end)

                lastIndex = match.range.last + 1
            }

            // Append any remaining text
            if (lastIndex < rawText.length) {
                append(rawText.substring(lastIndex))
            }
        }
    }

    /**
     * Strips all <c:...>...</c> markup to get plain raw text
     */
    fun stripColorTags(rawText: String): String {
        return rawText.replace(colorTagRegex) { matchResult ->
            matchResult.groupValues[2]
        }
    }

    /**
     * Parses raw text with color tags into an array of individual letters with their respective colors.
     */
    fun decomposeIntoColoredLetters(rawText: String, defaultHex: String = "#1A1A1A"): List<ColoredLetter> {
        val result = mutableListOf<ColoredLetter>()
        var i = 0
        while (i < rawText.length) {
            if (rawText.startsWith("<c:", i)) {
                val tagEnd = rawText.indexOf(">", i)
                val closeTag = rawText.indexOf("</c>", tagEnd + 1)
                if (tagEnd != -1 && closeTag != -1) {
                    val hex = rawText.substring(i + 3, tagEnd)
                    val content = rawText.substring(tagEnd + 1, closeTag)
                    for (ch in content) {
                        result.add(ColoredLetter(ch, hex))
                    }
                    i = closeTag + 4
                    continue
                }
            }
            result.add(ColoredLetter(rawText[i], null))
            i++
        }
        return result
    }

    /**
     * Recomposes a list of ColoredLetters back into markup text.
     * Consolidates adjacent characters with the same color into a single <c:#HEX>...</c> tag.
     */
    fun composeFromColoredLetters(letters: List<ColoredLetter>): String {
        if (letters.isEmpty()) return ""

        val sb = StringBuilder()
        var currentHex: String? = null
        val currentRun = StringBuilder()

        fun flushRun() {
            if (currentRun.isNotEmpty()) {
                if (currentHex != null) {
                    sb.append("<c:").append(currentHex).append(">")
                    sb.append(currentRun.toString())
                    sb.append("</c>")
                } else {
                    sb.append(currentRun.toString())
                }
                currentRun.clear()
            }
        }

        for (item in letters) {
            if (item.hexColor != currentHex) {
                flushRun()
                currentHex = item.hexColor
            }
            currentRun.append(item.char)
        }
        flushRun()

        return sb.toString()
    }

    /**
     * Converts all characters of the given text into brilliant sequential rainbow letters.
     */
    fun makeRainbowLetters(rawText: String): String {
        val plain = stripColorTags(rawText)
        if (plain.isEmpty()) return ""

        val letters = mutableListOf<ColoredLetter>()
        var colorIdx = 0
        for (ch in plain) {
            if (ch.isWhitespace()) {
                letters.add(ColoredLetter(ch, null))
            } else {
                val colorHex = rainbowSpectrum[colorIdx % rainbowSpectrum.size]
                letters.add(ColoredLetter(ch, colorHex))
                colorIdx++
            }
        }
        return composeFromColoredLetters(letters)
    }

    /**
     * Applies a specific hex color to the entire given text or wraps it cleanly.
     */
    fun applyColorToAllLetters(rawText: String, hexColor: String): String {
        val plain = stripColorTags(rawText)
        if (plain.isEmpty()) return ""
        return "<c:$hexColor>$plain</c>"
    }

    /**
     * Alternates between two colors for every letter.
     */
    fun alternateColors(rawText: String, hexColor1: String, hexColor2: String): String {
        val plain = stripColorTags(rawText)
        if (plain.isEmpty()) return ""

        val letters = mutableListOf<ColoredLetter>()
        var toggle = true
        for (ch in plain) {
            if (ch.isWhitespace()) {
                letters.add(ColoredLetter(ch, null))
            } else {
                val chosenColor = if (toggle) hexColor1 else hexColor2
                letters.add(ColoredLetter(ch, chosenColor))
                toggle = !toggle
            }
        }
        return composeFromColoredLetters(letters)
    }
}
