package com.example.ui.book

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.LinearScale
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.NotebookPage
import com.example.util.ColorUtils
import com.example.util.ImageStorage
import com.example.util.LineDrawingUtils
import com.example.util.LineStyle
import com.example.util.RichTextHelper
import com.example.util.StraightLine
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun PureWhitePageContent(
    page: NotebookPage,
    pageNumber: Int,
    totalPages: Int,
    defaultInkColor: String = "#1A1A1A",
    fontSizeSp: Float = 16f,
    saveFeedbackMessage: String? = null,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onUpdateFontSize: (Float) -> Unit = {},
    onToggleBookmark: () -> Unit,
    onAddImageClick: () -> Unit,
    onRemoveImage: (Int) -> Unit,
    onImageClick: (String) -> Unit,
    onDeletePage: () -> Unit,
    onOpenColorPicker: () -> Unit,
    onSelectQuickColor: (String) -> Unit,
    onAddStraightLine: (StraightLine) -> Unit = {},
    onUndoLine: () -> Unit = {},
    onClearLines: () -> Unit = {},
    onManualSave: () -> Unit = {},
    showLightLines: Boolean = false,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val density = LocalDensity.current

    // Immediate reactive local text state for responsive typing
    var localTitle by remember(page.id) { mutableStateOf(page.title) }
    var localContent by remember(page.id) { mutableStateOf(page.content) }

    LaunchedEffect(page.title) {
        if (localTitle != page.title) {
            localTitle = page.title
        }
    }
    LaunchedEffect(page.content) {
        if (localContent != page.content) {
            localContent = page.content
        }
    }

    val contentFocusRequester = remember { FocusRequester() }
    var showFontSizeDialog by remember { mutableStateOf(false) }
    var showLetterColorStudio by remember { mutableStateOf(false) }

    val imageList = remember(page.imageUrisJson) {
        ImageStorage.parseJsonArray(page.imageUrisJson)
    }

    val drawnLines = remember(page.inkDrawingJson) {
        LineDrawingUtils.parseLinesJson(page.inkDrawingJson)
    }

    val activeInkHex = remember(page.inkColor, defaultInkColor) {
        page.inkColor.ifBlank { defaultInkColor }
    }
    val activeInkColor = remember(activeInkHex) {
        ColorUtils.parseColor(activeInkHex)
    }

    val formattedDate = remember(page.createdAt) {
        SimpleDateFormat("EEE, dd MMM yyyy • hh:mm a", Locale.getDefault()).format(Date(page.createdAt))
    }

    // Straight Line Drawing Tool States
    var isStraightLineMode by remember { mutableStateOf(false) }
    var selectedLineStyle by remember { mutableStateOf(LineStyle.SOLID) }
    var selectedLineWidthDp by remember { mutableFloatStateOf(2.5f) }
    var snapToCardinalAngles by remember { mutableStateOf(false) }

    // Live Drag gesture tracking
    var liveDragStart by remember { mutableStateOf<Offset?>(null) }
    var liveDragCurrent by remember { mutableStateOf<Offset?>(null) }
    var canvasSize by remember { mutableStateOf(Size.Zero) }

    Card(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 18.dp)
            .shadow(12.dp, RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp, topStart = 2.dp, bottomStart = 2.dp)),
        shape = RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp, topStart = 2.dp, bottomStart = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)) // STRICT PURE WHITE PAGE
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFFFFF)) // PURE WHITE BACKGROUND
        ) {
            // Book Spine Gutter Shadow (Left Edge subtle bound gradient) & Ruled Lines
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Book Spine Binding Shadow
                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0x24000000),
                            Color(0x0E000000),
                            Color(0x00000000)
                        ),
                        startX = 0f,
                        endX = 22.dp.toPx()
                    ),
                    size = Size(22.dp.toPx(), size.height)
                )

                // Optional subtle paper ruling guidelines
                if (showLightLines) {
                    val lineSpacing = 32.dp.toPx()
                    val startY = 150.dp.toPx()
                    var currentY = startY
                    while (currentY < size.height - 40.dp.toPx()) {
                        drawLine(
                            color = Color(0x12000000),
                            start = Offset(30.dp.toPx(), currentY),
                            end = Offset(size.width - 24.dp.toPx(), currentY),
                            strokeWidth = 1f
                        )
                        currentY += lineSpacing
                    }
                }
            }

            // Permanent Straight Lines Canvas (Underlying or Overlay layer)
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                canvasSize = size
                // Render all saved straight lines permanently
                for (line in drawnLines) {
                    LineDrawingUtils.drawStraightLineOnCanvas(
                        drawScope = this,
                        line = line,
                        canvasWidth = size.width,
                        canvasHeight = size.height
                    )
                }

                // Render live line currently being dragged
                val start = liveDragStart
                val current = liveDragCurrent
                if (start != null && current != null) {
                    var endPoint = current
                    if (snapToCardinalAngles) {
                        val dx = current.x - start.x
                        val dy = current.y - start.y
                        val length = sqrt(dx * dx + dy * dy)
                        if (length > 10f) {
                            var angleDeg = LineDrawingUtils.calculateAngleDegrees(start, current)
                            // Snap to nearest 45 degrees
                            val snappedDeg = ((angleDeg / 45f).roundToInt() * 45).toFloat()
                            val snappedRad = Math.toRadians(snappedDeg.toDouble())
                            endPoint = Offset(
                                (start.x + length * cos(snappedRad)).toFloat(),
                                (start.y + length * sin(snappedRad)).toFloat()
                            )
                        }
                    }

                    // Draw live dragging line
                    val strokePx = with(density) { selectedLineWidthDp.dp.toPx() }
                    val pathEffect = if (selectedLineStyle == LineStyle.DASHED) {
                        PathEffect.dashPathEffect(floatArrayOf(strokePx * 3.5f, strokePx * 2.5f), 0f)
                    } else null

                    drawLine(
                        color = activeInkColor,
                        start = start,
                        end = endPoint,
                        strokeWidth = strokePx,
                        cap = StrokeCap.Round,
                        pathEffect = pathEffect
                    )

                    // Draw start and end pin points
                    drawCircle(color = activeInkColor, radius = strokePx * 1.6f + 3f, center = start)
                    drawCircle(color = activeInkColor, radius = strokePx * 1.6f + 3f, center = endPoint)
                }
            }

            // Main Content Area (Scrollable Text & Notes)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 28.dp, end = 20.dp, top = 20.dp, bottom = 16.dp)
                    .verticalScroll(scrollState)
            ) {
                // Top Page Header (Date, Page Counter, Save Button, Bookmark)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Date & Time Stamp
                    Text(
                        text = formattedDate,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF757575),
                        fontFamily = FontFamily.SansSerif
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Instant Save Button with Feedback
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (saveFeedbackMessage != null) Color(0xFFE8F5E9) else Color(0xFFF5F5F5),
                            modifier = Modifier
                                .clickable { onManualSave() }
                                .testTag("save_note_button_${page.id}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = if (saveFeedbackMessage != null) Icons.Default.Check else Icons.Default.Save,
                                    contentDescription = "Save Note",
                                    tint = if (saveFeedbackMessage != null) Color(0xFF2E7D32) else Color(0xFF616161),
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = saveFeedbackMessage ?: "Save",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (saveFeedbackMessage != null) Color(0xFF2E7D32) else Color(0xFF424242)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Bookmark Ribbon Indicator
                        IconButton(
                            onClick = onToggleBookmark,
                            modifier = Modifier.size(32.dp).testTag("bookmark_button_${page.id}")
                        ) {
                            Icon(
                                imageVector = if (page.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = if (page.isBookmarked) "Bookmarked" else "Bookmark Page",
                                tint = if (page.isBookmarked) Color(0xFFD32F2F) else Color(0xFF9E9E9E),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Page Number Pill
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF5F5F5),
                            modifier = Modifier.padding(start = 2.dp)
                        ) {
                            Text(
                                text = "Page $pageNumber of $totalPages",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF424242),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Title Input (Always editable with instant auto-save)
                BasicTextField(
                    value = localTitle,
                    onValueChange = { newTitle ->
                        localTitle = newTitle
                        onTitleChange(newTitle)
                    },
                    textStyle = TextStyle(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        color = activeInkColor
                    ),
                    cursorBrush = SolidColor(activeInkColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("page_title_input_${page.id}"),
                    decorationBox = { innerTextField ->
                        if (localTitle.isEmpty()) {
                            Text(
                                text = "Title...",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif,
                                color = Color(0xFFBDBDBD)
                            )
                        }
                        innerTextField()
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Subtle divider below title
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFFEEEEEE))
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Toolbar with Format, Straight Line Tool Button, Font Size Adjuster, Palette, Photo Picker
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (isStraightLineMode) Color(0xFFEDE7F6) else Color(0xFFFAFAFA),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Straight Line Tool Injector Toggle Button (Ruler / Straight Line Mode)
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isStraightLineMode) Color(0xFF673AB7) else Color(0xFFEEEEEE),
                            modifier = Modifier
                                .clickable { isStraightLineMode = !isStraightLineMode }
                                .testTag("straight_line_tool_toggle_${page.id}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    Icons.Default.Straighten,
                                    contentDescription = "Draw Straight Line in any direction",
                                    tint = if (isStraightLineMode) Color.White else Color(0xFF5E35B1),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isStraightLineMode) "Drawing Line" else "Draw Line",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isStraightLineMode) Color.White else Color(0xFF5E35B1)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Text Size Controls (A- / Size Badge / A+)
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFE8EAF6),
                            modifier = Modifier.padding(horizontal = 2.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                // Decrease font size (Chhota)
                                IconButton(
                                    onClick = {
                                        val newSize = (fontSizeSp - 2f).coerceAtLeast(11f)
                                        onUpdateFontSize(newSize)
                                    },
                                    modifier = Modifier
                                        .size(24.dp)
                                        .testTag("font_size_decrease_${page.id}")
                                ) {
                                    Text(
                                        text = "A-",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF283593)
                                    )
                                }

                                // Font size badge - clickable for popup presets
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.White,
                                    modifier = Modifier
                                        .clickable { showFontSizeDialog = true }
                                        .padding(horizontal = 2.dp)
                                        .testTag("font_size_badge_${page.id}")
                                ) {
                                    Text(
                                        text = "${fontSizeSp.roundToInt()}sp",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1A237E),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                // Increase font size (Bada)
                                IconButton(
                                    onClick = {
                                        val newSize = (fontSizeSp + 2f).coerceAtMost(38f)
                                        onUpdateFontSize(newSize)
                                    },
                                    modifier = Modifier
                                        .size(24.dp)
                                        .testTag("font_size_increase_${page.id}")
                                 ) {
                                    Text(
                                        text = "A+",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF283593)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Letters Color Studio Button (Individual letter color & rainbow)
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFFFF3E0),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = Brush.horizontalGradient(listOf(Color(0xFFFF9800), Color(0xFFE91E63))),
                                width = 1.dp
                            ),
                            modifier = Modifier
                                .clickable { showLetterColorStudio = true }
                                .padding(horizontal = 2.dp)
                                .testTag("letter_color_studio_button_${page.id}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                            ) {
                                Text("🔤", fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Letters Color",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE65100)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Insert Bullet
                        IconButton(
                            onClick = {
                                val current = localContent
                                val prefix = if (current.isEmpty() || current.endsWith("\n")) "• " else "\n• "
                                val updated = current + prefix
                                localContent = updated
                                onContentChange(updated)
                            },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(Icons.Default.FormatListBulleted, contentDescription = "Bullet List", tint = Color(0xFF555555), modifier = Modifier.size(16.dp))
                        }

                        // Insert Quote
                        IconButton(
                            onClick = {
                                val current = localContent
                                val prefix = if (current.isEmpty() || current.endsWith("\n")) "\" " else "\n\" "
                                val updated = current + prefix
                                localContent = updated
                                onContentChange(updated)
                            },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(Icons.Default.FormatQuote, contentDescription = "Quote", tint = Color(0xFF555555), modifier = Modifier.size(16.dp))
                        }

                        // Insert Current Time
                        IconButton(
                            onClick = {
                                val timeNow = SimpleDateFormat("[hh:mm a] ", Locale.getDefault()).format(Date())
                                val updated = localContent + (if (localContent.isNotEmpty() && !localContent.endsWith("\n")) "\n" else "") + timeNow
                                localContent = updated
                                onContentChange(updated)
                            },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(Icons.Default.Today, contentDescription = "Insert Timestamp", tint = Color(0xFF555555), modifier = Modifier.size(16.dp))
                        }

                        // Quick Favorite Inks Swatches
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .weight(1f)
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 4.dp)
                        ) {
                            ColorUtils.quickFavorites.forEach { hexColor ->
                                val swatchColor = ColorUtils.parseColor(hexColor)
                                val isSelected = activeInkHex.equals(hexColor, ignoreCase = true)

                                Box(
                                    modifier = Modifier
                                        .size(if (isSelected) 20.dp else 16.dp)
                                        .clip(CircleShape)
                                        .background(swatchColor)
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) Color(0xFF1976D2) else Color(0x33000000),
                                            shape = CircleShape
                                        )
                                        .clickable { onSelectQuickColor(hexColor) }
                                )
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Custom Ink Color Picker Trigger Button
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFFF0F0F0),
                            modifier = Modifier
                                .clickable { onOpenColorPicker() }
                                .testTag("custom_ink_color_button_${page.id}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .background(activeInkColor, CircleShape)
                                        .border(1.dp, Color(0x33000000), CircleShape)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    Icons.Default.Palette,
                                    contentDescription = "Custom Ink Color",
                                    tint = Color(0xFF424242),
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }

                        // Attach Photo Button
                        FilledTonalIconButton(
                            onClick = onAddImageClick,
                            modifier = Modifier.size(32.dp).testTag("attach_image_button_${page.id}"),
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = Color(0xFFEEEEEE),
                                contentColor = Color(0xFF1E88E5)
                            )
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Attach Photo", modifier = Modifier.size(17.dp))
                        }
                    }
                }

                // Dedicated Straight Line Studio Controls (When Straight Line Mode is Active)
                AnimatedVisibility(
                    visible = isStraightLineMode,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .background(Color(0xFFF3E5F5), RoundedCornerShape(10.dp))
                            .border(1.dp, Color(0xFFD1C4E9), RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "📏 Drag across page to draw straight line in ANY direction",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4A148C)
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (drawnLines.isNotEmpty()) {
                                    // Undo last line
                                    IconButton(
                                        onClick = { onUndoLine() },
                                        modifier = Modifier.size(28.dp).testTag("undo_line_button")
                                    ) {
                                        Icon(Icons.Default.Undo, contentDescription = "Undo Line", tint = Color(0xFF4A148C), modifier = Modifier.size(16.dp))
                                    }

                                    // Clear all lines
                                    IconButton(
                                        onClick = { onClearLines() },
                                        modifier = Modifier.size(28.dp).testTag("clear_lines_button")
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Clear All Lines", tint = Color(0xFFD32F2F), modifier = Modifier.size(16.dp))
                                    }
                                }

                                // Done with line drawing
                                IconButton(
                                    onClick = { isStraightLineMode = false },
                                    modifier = Modifier.size(28.dp).testTag("done_line_mode_button")
                                ) {
                                    Icon(Icons.Default.Done, contentDescription = "Done Line Drawing", tint = Color(0xFF2E7D32), modifier = Modifier.size(18.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Controls Row: Styles, Thickness, Snap Angle
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Line Styles
                            LineStyle.values().forEach { style ->
                                FilterChip(
                                    selected = selectedLineStyle == style,
                                    onClick = { selectedLineStyle = style },
                                    label = { Text(style.displayName, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF7B1FA2),
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }

                            // Snap to 45/90/180 degrees toggle
                            FilterChip(
                                selected = snapToCardinalAngles,
                                onClick = { snapToCardinalAngles = !snapToCardinalAngles },
                                label = { Text(if (snapToCardinalAngles) "Snap 45°/90° ON" else "Free 360°", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF00897B),
                                    selectedLabelColor = Color.White
                                )
                            )

                            // Thickness Chips
                            listOf(1.5f to "Thin", 2.5f to "Normal", 4.5f to "Thick", 7f to "Marker").forEach { (width, label) ->
                                FilterChip(
                                    selected = selectedLineWidthDp == width,
                                    onClick = { selectedLineWidthDp = width },
                                    label = { Text(label, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF1976D2),
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Attached Images Gallery Section
                if (imageList.isNotEmpty()) {
                    Text(
                        text = "Attached Photos (${imageList.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF616161),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        itemsIndexed(imageList) { index, imagePath ->
                            Box(
                                modifier = Modifier
                                    .size(width = 130.dp, height = 110.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                                    .clickable { onImageClick(imagePath) }
                            ) {
                                AsyncImage(
                                    model = File(imagePath),
                                    contentDescription = "Page Photo ${index + 1}",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )

                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(4.dp)
                                        .size(22.dp)
                                        .background(Color(0xCC000000), CircleShape)
                                        .clickable { onRemoveImage(index) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Remove Photo",
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Note Body Text Editor (Instant reactive typing & rich letter coloring)
                val hasLetterColorMarkup = remember(localContent) { localContent.contains("<c:") }

                if (hasLetterColorMarkup) {
                    // Rich Colored Letters Display with Click-to-Edit & Studio Button
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 380.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFFF8E1),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = Brush.horizontalGradient(listOf(Color(0xFFFFB300), Color(0xFFFF4081)))
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .clickable { showLetterColorStudio = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🌈", fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Custom Letter Colors Active",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFBF360C)
                                    )
                                }
                                Text(
                                    text = "Tap to Customize Letters ➔",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1976D2)
                                )
                            }
                        }

                        val annotated = remember(localContent, activeInkColor) {
                            RichTextHelper.parseToAnnotatedString(localContent, activeInkColor)
                        }

                        Text(
                            text = annotated,
                            fontSize = fontSizeSp.sp,
                            lineHeight = (fontSizeSp * 1.55f).sp,
                            fontFamily = FontFamily.Serif,
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 320.dp)
                                .clickable { showLetterColorStudio = true }
                                .testTag("page_rich_content_display_${page.id}")
                        )
                    }
                } else {
                    // Standard Direct Text Field
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 380.dp)
                            .clickable { contentFocusRequester.requestFocus() }
                    ) {
                        BasicTextField(
                            value = localContent,
                            onValueChange = { newContent ->
                                localContent = newContent
                                onContentChange(newContent)
                            },
                            textStyle = TextStyle(
                                fontSize = fontSizeSp.sp,
                                lineHeight = (fontSizeSp * 1.55f).sp,
                                fontFamily = FontFamily.SansSerif,
                                color = activeInkColor
                            ),
                            cursorBrush = SolidColor(activeInkColor),
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 380.dp)
                                .focusRequester(contentFocusRequester)
                                .testTag("page_content_input_${page.id}"),
                            decorationBox = { innerTextField ->
                                if (localContent.isEmpty()) {
                                    Text(
                                        text = "Start writing your thoughts, notes, and ideas on this pure white page...\n\n• Tap 'Letters Color' above to color individual letters or rainbow 🌈\n• Tap 'Draw Line' above to draw crisp straight lines in any direction\n• Tap 'A-' or 'A+' to make your writing smaller or larger\n• Tap the palette button to change your writing ink color\n• Tap the photo icon to attach pictures\n• Turn pages with realistic Google Play Books 3D curl\n• Everything is permanently saved and editable anytime",
                                        fontSize = fontSizeSp.sp,
                                        lineHeight = (fontSizeSp * 1.55f).sp,
                                        color = Color(0xFF9E9E9E),
                                        fontFamily = FontFamily.SansSerif
                                    )
                                }
                                innerTextField()
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Bottom Page Footer with Word Count and Delete Option
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val wordCount = remember(page.content) {
                        if (page.content.isBlank()) 0 else page.content.trim().split("\\s+".toRegex()).size
                    }

                    Text(
                        text = "$wordCount words • ${page.content.length} characters • ${drawnLines.size} lines",
                        fontSize = 11.sp,
                        color = Color(0xFF9E9E9E)
                    )

                    // Delete page option
                    if (totalPages > 1) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { onDeletePage() }
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete Page",
                                tint = Color(0xFFE57373),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Delete Page",
                                fontSize = 11.sp,
                                color = Color(0xFFE57373),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Interactive Straight Line Gesture Capture Overlay (Active only when Line Drawing Mode is ON)
            if (isStraightLineMode) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(selectedLineStyle, selectedLineWidthDp, activeInkHex, snapToCardinalAngles) {
                            detectDragGestures(
                                onDragStart = { startOffset ->
                                    liveDragStart = startOffset
                                    liveDragCurrent = startOffset
                                },
                                onDrag = { change, _ ->
                                    change.consume()
                                    liveDragCurrent = change.position
                                },
                                onDragEnd = {
                                    val start = liveDragStart
                                    val current = liveDragCurrent
                                    if (start != null && current != null) {
                                        var endPoint = current
                                        if (snapToCardinalAngles) {
                                            val dx = current.x - start.x
                                            val dy = current.y - start.y
                                            val length = sqrt(dx * dx + dy * dy)
                                            if (length > 10f) {
                                                val angleDeg = LineDrawingUtils.calculateAngleDegrees(start, current)
                                                val snappedDeg = ((angleDeg / 45f).roundToInt() * 45).toFloat()
                                                val snappedRad = Math.toRadians(snappedDeg.toDouble())
                                                endPoint = Offset(
                                                    (start.x + length * cos(snappedRad)).toFloat(),
                                                    (start.y + length * sin(snappedRad)).toFloat()
                                                )
                                            }
                                        }

                                        val w = if (canvasSize.width > 0) canvasSize.width else size.width.toFloat()
                                        val h = if (canvasSize.height > 0) canvasSize.height else size.height.toFloat()

                                        if (w > 0 && h > 0) {
                                            val straightLine = StraightLine(
                                                startXRatio = (start.x / w).coerceIn(0f, 1f),
                                                startYRatio = (start.y / h).coerceIn(0f, 1f),
                                                endXRatio = (endPoint.x / w).coerceIn(0f, 1f),
                                                endYRatio = (endPoint.y / h).coerceIn(0f, 1f),
                                                colorHex = activeInkHex,
                                                strokeWidthDp = selectedLineWidthDp,
                                                style = selectedLineStyle
                                            )
                                            onAddStraightLine(straightLine)
                                        }
                                    }
                                    liveDragStart = null
                                    liveDragCurrent = null
                                },
                                onDragCancel = {
                                    liveDragStart = null
                                    liveDragCurrent = null
                                }
                            )
                        }
                ) {
                    // Live angle/direction HUD Badge when user is actively dragging
                    val start = liveDragStart
                    val current = liveDragCurrent
                    if (start != null && current != null) {
                        val angle = LineDrawingUtils.calculateAngleDegrees(start, current).roundToInt()
                        val dx = current.x - start.x
                        val dy = current.y - start.y
                        val lengthPx = sqrt(dx * dx + dy * dy).roundToInt()

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xDD000000),
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 16.dp)
                        ) {
                            Text(
                                text = "Angle: $angle° • Length: ${lengthPx}px • Direction: ${getDirectionLabel(angle)}",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Font Size Selection Dialog
            if (showFontSizeDialog) {
                AlertDialog(
                    onDismissRequest = { showFontSizeDialog = false },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.FormatSize,
                                contentDescription = "Font Size",
                                tint = Color(0xFF3F51B5),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Text Font Size",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                    },
                    text = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "Likhne wale text ka size chhota ya bada karein:",
                                fontSize = 13.sp,
                                color = Color(0xFF616161)
                            )

                            // Quick preset sizes
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(
                                    12f to "12sp (Small)",
                                    14f to "14sp (Compact)",
                                    16f to "16sp (Default)",
                                    20f to "20sp (Medium)",
                                    24f to "24sp (Large)",
                                    30f to "30sp (Huge)"
                                ).forEach { (size, label) ->
                                    FilterChip(
                                        selected = fontSizeSp.roundToInt() == size.roundToInt(),
                                        onClick = {
                                            onUpdateFontSize(size)
                                        },
                                        label = { Text(label, fontSize = 12.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Color(0xFF3F51B5),
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                }
                            }

                            // Continuous Slider
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Current: ${fontSizeSp.roundToInt()} sp",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        color = Color(0xFF1A237E)
                                    )
                                    Text(
                                        text = if (fontSizeSp < 15f) "Chhota (Small)" else if (fontSizeSp > 22f) "Bada (Large)" else "Standard",
                                        fontSize = 12.sp,
                                        color = Color(0xFF757575)
                                    )
                                }

                                Slider(
                                    value = fontSizeSp,
                                    onValueChange = { onUpdateFontSize(it) },
                                    valueRange = 11f..38f,
                                    steps = 26,
                                    colors = SliderDefaults.colors(
                                        thumbColor = Color(0xFF3F51B5),
                                        activeTrackColor = Color(0xFF3F51B5)
                                    ),
                                    modifier = Modifier.testTag("font_size_dialog_slider")
                                )
                            }

                            // Live sample preview
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF5F5F5),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Preview: The quick brown fox jumps over the lazy dog.",
                                    fontSize = fontSizeSp.sp,
                                    lineHeight = (fontSizeSp * 1.55f).sp,
                                    color = activeInkColor,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = { showFontSizeDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5))
                        ) {
                            Text("Done / Theek Hai", color = Color.White)
                        }
                    }
                )
            }

            // Letter Color Studio Dialog
            if (showLetterColorStudio) {
                LetterColorStudioDialog(
                    initialText = localContent,
                    defaultInkHex = activeInkHex,
                    fontSizeSp = fontSizeSp,
                    onDismiss = { showLetterColorStudio = false },
                    onApplyFormattedText = { formatted ->
                        localContent = formatted
                        onContentChange(formatted)
                    }
                )
            }
        }
    }
}

private fun getDirectionLabel(angle: Int): String {
    return when (angle) {
        in 338..360, in 0..22 -> "East →"
        in 23..67 -> "South-East ↘"
        in 68..112 -> "South ↓"
        in 113..157 -> "South-West ↙"
        in 158..202 -> "West ←"
        in 203..247 -> "North-West ↖"
        in 248..292 -> "North ↑"
        in 293..337 -> "North-East ↗"
        else -> "Direction: $angle°"
    }
}

