package com.example.ui.book

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.NotebookSettings
import java.io.File

@Composable
fun CoverPageView(
    settings: NotebookSettings,
    totalNotesCount: Int,
    onOpenBook: () -> Unit,
    onChangeCoverClick: () -> Unit,
    onCustomizeDetailsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 20.dp)
            .shadow(16.dp, RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp, topStart = 4.dp, bottomStart = 4.dp)),
        shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp, topStart = 4.dp, bottomStart = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (settings.customCoverUri != null && File(settings.customCoverUri).exists()) {
                // User-uploaded custom cover image
                AsyncImage(
                    model = File(settings.customCoverUri),
                    contentDescription = "Custom Notebook Cover",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Realistic glossy hardcover overlay + spine bevel
                CoverSpineOverlay()

                // Bottom floating action buttons
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0xAA000000), Color(0xDD000000))
                            )
                        )
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = onOpenBook,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("open_notebook_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFDFB448),
                                contentColor = Color(0xFF1E1500)
                            ),
                            shape = RoundedCornerShape(26.dp)
                        ) {
                            Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Open Notebook ($totalNotesCount Pages)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            FilledTonalButton(
                                onClick = onChangeCoverClick,
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = Color(0x55FFFFFF),
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Change Cover", fontSize = 13.sp)
                            }

                            FilledTonalButton(
                                onClick = onCustomizeDetailsClick,
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = Color(0x55FFFFFF),
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Edit Title", fontSize = 13.sp)
                            }
                        }
                    }
                }
            } else if (settings.coverStyle == "GOLD_ORNATE") {
                // Classic Golden Ornate Hardcover
                GoldenOrnateCoverContent(
                    settings = settings,
                    totalNotesCount = totalNotesCount,
                    onOpenBook = onOpenBook,
                    onChangeCoverClick = onChangeCoverClick,
                    onCustomizeDetailsClick = onCustomizeDetailsClick
                )
            } else {
                // Blended Rainbow Mixed-up Cover (Vibrant holographic mixed gradient)
                RainbowMixupCoverContent(
                    settings = settings,
                    totalNotesCount = totalNotesCount,
                    onOpenBook = onOpenBook,
                    onChangeCoverClick = onChangeCoverClick,
                    onCustomizeDetailsClick = onCustomizeDetailsClick
                )
            }
        }
    }
}

@Composable
private fun GoldenOrnateCoverContent(
    settings: NotebookSettings,
    totalNotesCount: Int,
    onOpenBook: () -> Unit,
    onChangeCoverClick: () -> Unit,
    onCustomizeDetailsClick: () -> Unit
) {
    // Rich Golden gradient background
    val goldBgBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFFF0B8),
            Color(0xFFE5BE58),
            Color(0xFFBA8A24),
            Color(0xFF8F630E),
            Color(0xFFCBA037),
            Color(0xFFFFEEAA)
        ),
        start = Offset(0f, 0f),
        end = Offset(1000f, 1800f)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(goldBgBrush)
    ) {
        // Decorative Ornate Golden Borders & Crest Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Outer Filigree Double Border
            val borderPadding = 18.dp.toPx()
            val innerPadding = 26.dp.toPx()
            val strokeGold = Color(0x99523605)

            drawRect(
                color = strokeGold,
                topLeft = Offset(borderPadding, borderPadding),
                size = Size(w - borderPadding * 2, h - borderPadding * 2),
                style = Stroke(width = 2.5f)
            )

            drawRect(
                color = strokeGold.copy(alpha = 0.5f),
                topLeft = Offset(innerPadding, innerPadding),
                size = Size(w - innerPadding * 2, h - innerPadding * 2),
                style = Stroke(width = 1f)
            )

            // Corner ornate accents
            val cornerSize = 24.dp.toPx()
            // Top-left
            drawLine(
                strokeGold,
                Offset(borderPadding, borderPadding + cornerSize),
                Offset(borderPadding + cornerSize, borderPadding),
                strokeWidth = 2f
            )
            // Top-right
            drawLine(
                strokeGold,
                Offset(w - borderPadding - cornerSize, borderPadding),
                Offset(w - borderPadding, borderPadding + cornerSize),
                strokeWidth = 2f
            )
            // Bottom-left
            drawLine(
                strokeGold,
                Offset(borderPadding, h - borderPadding - cornerSize),
                Offset(borderPadding + cornerSize, h - borderPadding),
                strokeWidth = 2f
            )
            // Bottom-right
            drawLine(
                strokeGold,
                Offset(w - borderPadding - cornerSize, h - borderPadding),
                Offset(w - borderPadding, h - borderPadding - cornerSize),
                strokeWidth = 2f
            )
        }

        // Spine shading overlay
        CoverSpineOverlay()

        // Cover Typography and Crest
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 36.dp, end = 28.dp, top = 36.dp, bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header: "PROFESSIONAL NOTEBOOK"
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "PROFESSIONAL NOTEBOOK",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.5.sp,
                    color = Color(0xFF4A3408)
                )

                Spacer(modifier = Modifier.height(4.dp))
                Canvas(modifier = Modifier.size(width = 100.dp, height = 3.dp)) {
                    drawLine(
                        color = Color(0xFF6B4D0D),
                        start = Offset(0f, 1.5f),
                        end = Offset(size.width, 1.5f),
                        strokeWidth = 1.5f
                    )
                }
            }

            // Ornate Center Shield & Calligraphy
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                // Decorative Emblem Box
                Box(
                    modifier = Modifier
                        .size(170.dp)
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val cx = size.width / 2
                        val cy = size.height / 2
                        val radius = size.width / 2.2f

                        // Rainbow-tinted ornate shield stroke
                        val rainbowBrush = Brush.sweepGradient(
                            colors = listOf(
                                Color(0xFF00796B),
                                Color(0xFF388E3C),
                                Color(0xFFFBC02D),
                                Color(0xFFE64A19),
                                Color(0xFF7B1FA2),
                                Color(0xFF0288D1),
                                Color(0xFF00796B)
                            )
                        )

                        drawCircle(
                            brush = rainbowBrush,
                            radius = radius,
                            center = Offset(cx, cy),
                            style = Stroke(width = 3.dp.toPx())
                        )

                        drawCircle(
                            color = Color(0xFFFFFFFF).copy(alpha = 0.25f),
                            radius = radius - 6.dp.toPx(),
                            center = Offset(cx, cy)
                        )
                    }

                    // Elegant Calligraphy inside crest
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = settings.authorName,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic,
                            fontFamily = FontFamily.Serif,
                            color = Color(0xFF1E3A2B),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Title Block
                Text(
                    text = settings.bookTitle.ifBlank { settings.authorName.uppercase() },
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Serif,
                    letterSpacing = 1.5.sp,
                    color = Color(0xFF1F2937),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = settings.bookSubtitle,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.2.sp,
                    color = Color(0xFF4B380A),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = settings.bookVolume,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.8.sp,
                    color = Color(0xFF6B4F10)
                )
            }

            // Bottom Actions & Open Notebook Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onOpenBook,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("open_notebook_gold_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2C1E05),
                        contentColor = Color(0xFFFFE082)
                    ),
                    shape = RoundedCornerShape(26.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                ) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Open Notebook • ${if (totalNotesCount == 0) "Start Writing" else "$totalNotesCount Pages"}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = onChangeCoverClick,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF332000)
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.horizontalGradient(listOf(Color(0xFF8F630E), Color(0xFFBA8A24)))
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Upload Cover", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedButton(
                        onClick = onCustomizeDetailsClick,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF332000)
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.horizontalGradient(listOf(Color(0xFF8F630E), Color(0xFFBA8A24)))
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit Cover", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun CoverSpineOverlay() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        // Spine left bevel shadow
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color(0x77000000),
                    Color(0x33000000),
                    Color(0x00000000),
                    Color(0x22FFFFFF),
                    Color(0x00000000)
                ),
                startX = 0f,
                endX = 32.dp.toPx()
            ),
            size = Size(32.dp.toPx(), size.height)
        )

        // Spine ribs (embossed stitching)
        val ribYPositions = listOf(0.15f, 0.35f, 0.65f, 0.85f)
        for (relY in ribYPositions) {
            val y = size.height * relY
            drawLine(
                color = Color(0x55000000),
                start = Offset(0f, y),
                end = Offset(24.dp.toPx(), y),
                strokeWidth = 3f
            )
            drawLine(
                color = Color(0x44FFFFFF),
                start = Offset(0f, y + 2f),
                end = Offset(24.dp.toPx(), y + 2f),
                strokeWidth = 1.5f
            )
        }

        // Right edge book page thickness depth
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(Color(0x00000000), Color(0x33000000)),
                startX = size.width - 12.dp.toPx(),
                endX = size.width
            ),
            topLeft = Offset(size.width - 12.dp.toPx(), 0f),
            size = Size(12.dp.toPx(), size.height)
        )
    }
}

@Composable
private fun RainbowMixupCoverContent(
    settings: NotebookSettings,
    totalNotesCount: Int,
    onOpenBook: () -> Unit,
    onChangeCoverClick: () -> Unit,
    onCustomizeDetailsClick: () -> Unit
) {
    // Rich mixed-up organic rainbow gradient with vibrant flowing spectrum colors
    val rainbowMeshBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF2E1065), // Deep Cosmic Violet
            Color(0xFF1E3A8A), // Royal Indigo
            Color(0xFF0284C7), // Vivid Cyan Azure
            Color(0xFF0D9488), // Ocean Jade
            Color(0xFF16A34A), // Emerald Green
            Color(0xFFCA8A04), // Bright Amber Gold
            Color(0xFFEA580C), // Flame Sunset Orange
            Color(0xFFE11D48), // Rose Coral Crimson
            Color(0xFF9333EA)  // Electric Purple
        ),
        start = Offset(0f, 0f),
        end = Offset(1100f, 1900f)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(rainbowMeshBrush)
    ) {
        // Multi-layered organic color swirls for liquid rainbow mixup effect
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // 1. Iridescent radial swirls across the canvas
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x55FF007F), Color(0x2200E5FF), Color.Transparent),
                    center = Offset(w * 0.25f, h * 0.30f),
                    radius = w * 0.65f
                ),
                center = Offset(w * 0.25f, h * 0.30f),
                radius = w * 0.65f
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x66FFD700), Color(0x33FF6D00), Color.Transparent),
                    center = Offset(w * 0.80f, h * 0.70f),
                    radius = w * 0.70f
                ),
                center = Offset(w * 0.80f, h * 0.70f),
                radius = w * 0.70f
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x4400E5FF), Color(0x1118FFFF), Color.Transparent),
                    center = Offset(w * 0.70f, h * 0.20f),
                    radius = w * 0.55f
                ),
                center = Offset(w * 0.70f, h * 0.20f),
                radius = w * 0.55f
            )

            // 2. Luxury Ornate Golden Metallic Borders
            val borderPadding = 18.dp.toPx()
            val innerPadding = 26.dp.toPx()
            val goldStroke = Color(0xDDFFD700)
            val softGold = Color(0x88FFE082)

            drawRect(
                color = goldStroke,
                topLeft = Offset(borderPadding, borderPadding),
                size = Size(w - borderPadding * 2, h - borderPadding * 2),
                style = Stroke(width = 2.5f)
            )

            drawRect(
                color = softGold,
                topLeft = Offset(innerPadding, innerPadding),
                size = Size(w - innerPadding * 2, h - innerPadding * 2),
                style = Stroke(width = 1.2f)
            )

            // Ornate Corner Metal Brackets
            val cornerSize = 24.dp.toPx()
            // Top-left
            drawLine(goldStroke, Offset(borderPadding, borderPadding + cornerSize), Offset(borderPadding + cornerSize, borderPadding), strokeWidth = 2f)
            // Top-right
            drawLine(goldStroke, Offset(w - borderPadding - cornerSize, borderPadding), Offset(w - borderPadding, borderPadding + cornerSize), strokeWidth = 2f)
            // Bottom-left
            drawLine(goldStroke, Offset(borderPadding, h - borderPadding - cornerSize), Offset(borderPadding + cornerSize, h - borderPadding), strokeWidth = 2f)
            // Bottom-right
            drawLine(goldStroke, Offset(w - borderPadding - cornerSize, h - borderPadding), Offset(w - borderPadding, h - borderPadding - cornerSize), strokeWidth = 2f)
        }

        // Spine shading overlay
        CoverSpineOverlay()

        // Content layout
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 36.dp, end = 28.dp, top = 36.dp, bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header: "PROFESSIONAL NOTEBOOK"
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "PROFESSIONAL NOTEBOOK",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.5.sp,
                    color = Color(0xFFFFE082)
                )

                Spacer(modifier = Modifier.height(4.dp))
                Canvas(modifier = Modifier.size(width = 120.dp, height = 3.dp)) {
                    drawLine(
                        brush = Brush.horizontalGradient(
                            listOf(Color.Transparent, Color(0xFFFFD54F), Color.Transparent)
                        ),
                        start = Offset(0f, 1.5f),
                        end = Offset(size.width, 1.5f),
                        strokeWidth = 2f
                    )
                }
            }

            // Center Holographic Crest & Calligraphy
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(175.dp)
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val cx = size.width / 2
                        val cy = size.height / 2
                        val radius = size.width / 2.2f

                        // Mixed Rainbow halo sweep border
                        val sweepRainbow = Brush.sweepGradient(
                            colors = listOf(
                                Color(0xFFFF007F),
                                Color(0xFFFF9100),
                                Color(0xFFFFEA00),
                                Color(0xFF00E676),
                                Color(0xFF00E5FF),
                                Color(0xFF651FFF),
                                Color(0xFFFF007F)
                            )
                        )

                        // Golden Outer Ring
                        drawCircle(
                            color = Color(0xFFFFD54F),
                            radius = radius,
                            center = Offset(cx, cy),
                            style = Stroke(width = 3.5.dp.toPx())
                        )

                        // Rainbow Inner Glow
                        drawCircle(
                            brush = sweepRainbow,
                            radius = radius - 5.dp.toPx(),
                            center = Offset(cx, cy),
                            style = Stroke(width = 2.5.dp.toPx())
                        )

                        // Dark Frosted Glass center backing
                        drawCircle(
                            color = Color(0x66000000),
                            radius = radius - 7.dp.toPx(),
                            center = Offset(cx, cy)
                        )
                    }

                    // Author Name Calligraphy inside crest
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = settings.authorName,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic,
                            fontFamily = FontFamily.Serif,
                            color = Color(0xFFFFF8E1),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Title Block in Gold Foil against Rainbow Cover
                Text(
                    text = settings.bookTitle.ifBlank { settings.authorName.uppercase() },
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Serif,
                    letterSpacing = 1.6.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = settings.bookSubtitle,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.2.sp,
                    color = Color(0xFFFFE082),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = settings.bookVolume,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.8.sp,
                    color = Color(0xFFE0E0E0)
                )
            }

            // Bottom Actions & Open Notebook Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onOpenBook,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("open_notebook_rainbow_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1E1035),
                        contentColor = Color(0xFFFFE082)
                    ),
                    shape = RoundedCornerShape(26.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.horizontalGradient(listOf(Color(0xFFFFD54F), Color(0xFFFF4081)))
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                ) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Open Notebook • ${if (totalNotesCount == 0) "Start Writing" else "$totalNotesCount Pages"}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = onChangeCoverClick,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.horizontalGradient(listOf(Color(0x88FFFFFF), Color(0x88FFD54F)))
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Upload Cover", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedButton(
                        onClick = onCustomizeDetailsClick,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.horizontalGradient(listOf(Color(0x88FFFFFF), Color(0x88FFD54F)))
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit Cover", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
