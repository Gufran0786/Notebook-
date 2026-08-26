package com.example.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

enum class LineStyle(val displayName: String) {
    SOLID("Solid Line"),
    DASHED("Dashed Line"),
    ARROW("Single Arrow →"),
    DOUBLE_ARROW("Double Arrow ↔")
}

data class StraightLine(
    val startXRatio: Float, // 0f..1f normalized to canvas width
    val startYRatio: Float, // 0f..1f normalized to canvas height
    val endXRatio: Float,
    val endYRatio: Float,
    val colorHex: String = "#1A1A1A",
    val strokeWidthDp: Float = 2.5f,
    val style: LineStyle = LineStyle.SOLID
) {
    fun toJsonObject(): JSONObject {
        return JSONObject().apply {
            put("x1", startXRatio.toDouble())
            put("y1", startYRatio.toDouble())
            put("x2", endXRatio.toDouble())
            put("y2", endYRatio.toDouble())
            put("color", colorHex)
            put("width", strokeWidthDp.toDouble())
            put("style", style.name)
        }
    }

    companion object {
        fun fromJsonObject(json: JSONObject): StraightLine? {
            return try {
                StraightLine(
                    startXRatio = json.optDouble("x1", 0.0).toFloat(),
                    startYRatio = json.optDouble("y1", 0.0).toFloat(),
                    endXRatio = json.optDouble("x2", 0.0).toFloat(),
                    endYRatio = json.optDouble("y2", 0.0).toFloat(),
                    colorHex = json.optString("color", "#1A1A1A"),
                    strokeWidthDp = json.optDouble("width", 2.5).toFloat(),
                    style = try {
                        LineStyle.valueOf(json.optString("style", LineStyle.SOLID.name))
                    } catch (e: Exception) {
                        LineStyle.SOLID
                    }
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}

object LineDrawingUtils {

    fun parseLinesJson(json: String?): List<StraightLine> {
        if (json.isNullOrBlank() || json == "[]") return emptyList()
        val list = mutableListOf<StraightLine>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val line = StraightLine.fromJsonObject(obj)
                if (line != null) {
                    list.add(line)
                }
            }
        } catch (e: Exception) {
            // ignore corrupt json
        }
        return list
    }

    fun linesToJson(lines: List<StraightLine>): String {
        val array = JSONArray()
        for (line in lines) {
            array.put(line.toJsonObject())
        }
        return array.toString()
    }

    /**
     * Draws a straight line with arrows or dashed styling in any direction.
     */
    fun drawStraightLineOnCanvas(
        drawScope: DrawScope,
        line: StraightLine,
        canvasWidth: Float,
        canvasHeight: Float
    ) {
        val startX = line.startXRatio * canvasWidth
        val startY = line.startYRatio * canvasHeight
        val endX = line.endXRatio * canvasWidth
        val endY = line.endYRatio * canvasHeight

        val color = ColorUtils.parseColor(line.colorHex)
        val strokePx = with(drawScope) { line.strokeWidthDp.dp.toPx() }

        val pathEffect = if (line.style == LineStyle.DASHED) {
            PathEffect.dashPathEffect(floatArrayOf(strokePx * 3.5f, strokePx * 2.5f), 0f)
        } else null

        // Draw main straight line
        drawScope.drawLine(
            color = color,
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = strokePx,
            cap = StrokeCap.Round,
            pathEffect = pathEffect
        )

        // Draw arrowheads if applicable
        if (line.style == LineStyle.ARROW || line.style == LineStyle.DOUBLE_ARROW) {
            drawArrowHead(
                drawScope = drawScope,
                from = Offset(startX, startY),
                to = Offset(endX, endY),
                color = color,
                strokePx = strokePx
            )
        }

        if (line.style == LineStyle.DOUBLE_ARROW) {
            drawArrowHead(
                drawScope = drawScope,
                from = Offset(endX, endY),
                to = Offset(startX, startY),
                color = color,
                strokePx = strokePx
            )
        }
    }

    private fun drawArrowHead(
        drawScope: DrawScope,
        from: Offset,
        to: Offset,
        color: Color,
        strokePx: Float
    ) {
        val dx = to.x - from.x
        val dy = to.y - from.y
        val length = sqrt(dx * dx + dy * dy)
        if (length < 10f) return

        val angle = atan2(dy.toDouble(), dx.toDouble())
        val arrowLength = (strokePx * 4f + 14f).coerceAtMost(length * 0.4f)
        val arrowAngle = Math.PI / 6.0 // 30 degrees

        val x1 = to.x - (arrowLength * cos(angle - arrowAngle)).toFloat()
        val y1 = to.y - (arrowLength * sin(angle - arrowAngle)).toFloat()
        val x2 = to.x - (arrowLength * cos(angle + arrowAngle)).toFloat()
        val y2 = to.y - (arrowLength * sin(angle + arrowAngle)).toFloat()

        val path = Path().apply {
            moveTo(to.x, to.y)
            lineTo(x1, y1)
            lineTo(x2, y2)
            close()
        }

        drawScope.drawPath(path = path, color = color)
    }

    /**
     * Calculates angle in degrees (0 to 360) from start to end offset.
     */
    fun calculateAngleDegrees(start: Offset, end: Offset): Float {
        val dx = end.x - start.x
        val dy = end.y - start.y
        var degrees = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
        if (degrees < 0) degrees += 360f
        return degrees
    }
}
