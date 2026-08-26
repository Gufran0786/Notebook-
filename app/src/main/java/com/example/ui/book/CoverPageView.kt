package com.example.ui.book

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
            } else {
                when (settings.coverStyle) {
                    "GOLD_ORNATE", "CLASSIC_GOLD" -> {
                        GoldenOrnateCoverContent(
                            settings = settings,
                            totalNotesCount = totalNotesCount,
                            onOpenBook = onOpenBook,
                            onChangeCoverClick = onChangeCoverClick,
                            onCustomizeDetailsClick = onCustomizeDetailsClick
                        )
                    }
                    "ROYAL_EMERALD" -> {
                        RoyalEmeraldCoverContent(
                            settings = settings,
                            totalNotesCount = totalNotesCount,
                            onOpenBook = onOpenBook,
                            onChangeCoverClick = onChangeCoverClick,
                            onCustomizeDetailsClick = onCustomizeDetailsClick
                        )
                    }
                    "MIDNIGHT_SAPPHIRE" -> {
                        MidnightSapphireCoverContent(
                            settings = settings,
                            totalNotesCount = totalNotesCount,
                            onOpenBook = onOpenBook,
                            onChangeCoverClick = onChangeCoverClick,
                            onCustomizeDetailsClick = onCustomizeDetailsClick
                        )
                    }
                    "CRIMSON_ROYALTY" -> {
                        CrimsonRoyaltyCoverContent(
                            settings = settings,
                            totalNotesCount = totalNotesCount,
                            onOpenBook = onOpenBook,
                            onChangeCoverClick = onChangeCoverClick,
                            onCustomizeDetailsClick = onCustomizeDetailsClick
                        )
                    }
                    "OBSIDIAN_PLATINUM" -> {
                        ObsidianPlatinumCoverContent(
                            settings = settings,
                            totalNotesCount = totalNotesCount,
                            onOpenBook = onOpenBook,
                            onChangeCoverClick = onChangeCoverClick,
                            onCustomizeDetailsClick = onCustomizeDetailsClick
                        )
                    }
                    "SUNSET_AURORA" -> {
                        SunsetAuroraCoverContent(
                            settings = settings,
                            totalNotesCount = totalNotesCount,
                            onOpenBook = onOpenBook,
                            onChangeCoverClick = onChangeCoverClick,
                            onCustomizeDetailsClick = onCustomizeDetailsClick
                        )
                    }
                    else -> {
                        // RAINBOW_MIXUP default
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
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
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

            val cornerSize = 24.dp.toPx()
            drawLine(strokeGold, Offset(borderPadding, borderPadding + cornerSize), Offset(borderPadding + cornerSize, borderPadding), strokeWidth = 2f)
            drawLine(strokeGold, Offset(w - borderPadding - cornerSize, borderPadding), Offset(w - borderPadding, borderPadding + cornerSize), strokeWidth = 2f)
            drawLine(strokeGold, Offset(borderPadding, h - borderPadding - cornerSize), Offset(borderPadding + cornerSize, h - borderPadding), strokeWidth = 2f)
            drawLine(strokeGold, Offset(w - borderPadding - cornerSize, h - borderPadding), Offset(w - borderPadding, h - borderPadding - cornerSize), strokeWidth = 2f)
        }

        CoverSpineOverlay()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 36.dp, end = 28.dp, top = 36.dp, bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
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

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
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

                        val goldCrestBrush = Brush.sweepGradient(
                            colors = listOf(
                                Color(0xFF5A3D06),
                                Color(0xFFB8860B),
                                Color(0xFFFFDF73),
                                Color(0xFFB8860B),
                                Color(0xFF5A3D06)
                            )
                        )

                        drawCircle(
                            brush = goldCrestBrush,
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
                            color = Color(0xFF332000),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

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
private fun RainbowMixupCoverContent(
    settings: NotebookSettings,
    totalNotesCount: Int,
    onOpenBook: () -> Unit,
    onChangeCoverClick: () -> Unit,
    onCustomizeDetailsClick: () -> Unit
) {
    val rainbowMeshBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF2E1065),
            Color(0xFF1E3A8A),
            Color(0xFF0284C7),
            Color(0xFF0D9488),
            Color(0xFF16A34A),
            Color(0xFFCA8A04),
            Color(0xFFEA580C),
            Color(0xFFE11D48),
            Color(0xFF9333EA)
        ),
        start = Offset(0f, 0f),
        end = Offset(1100f, 1900f)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(rainbowMeshBrush)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

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

            val cornerSize = 24.dp.toPx()
            drawLine(goldStroke, Offset(borderPadding, borderPadding + cornerSize), Offset(borderPadding + cornerSize, borderPadding), strokeWidth = 2f)
            drawLine(goldStroke, Offset(w - borderPadding - cornerSize, borderPadding), Offset(w - borderPadding, borderPadding + cornerSize), strokeWidth = 2f)
            drawLine(goldStroke, Offset(borderPadding, h - borderPadding - cornerSize), Offset(borderPadding + cornerSize, h - borderPadding), strokeWidth = 2f)
            drawLine(goldStroke, Offset(w - borderPadding - cornerSize, h - borderPadding), Offset(w - borderPadding, h - borderPadding - cornerSize), strokeWidth = 2f)
        }

        CoverSpineOverlay()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 36.dp, end = 28.dp, top = 36.dp, bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
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

                        drawCircle(
                            color = Color(0xFFFFD54F),
                            radius = radius,
                            center = Offset(cx, cy),
                            style = Stroke(width = 3.5.dp.toPx())
                        )

                        drawCircle(
                            brush = sweepRainbow,
                            radius = radius - 5.dp.toPx(),
                            center = Offset(cx, cy),
                            style = Stroke(width = 2.5.dp.toPx())
                        )

                        drawCircle(
                            color = Color(0x66000000),
                            radius = radius - 7.dp.toPx(),
                            center = Offset(cx, cy)
                        )
                    }

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

@Composable
private fun RoyalEmeraldCoverContent(
    settings: NotebookSettings,
    totalNotesCount: Int,
    onOpenBook: () -> Unit,
    onChangeCoverClick: () -> Unit,
    onCustomizeDetailsClick: () -> Unit
) {
    val emeraldBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF032219),
            Color(0xFF0A4434),
            Color(0xFF0D5E48),
            Color(0xFF063325),
            Color(0xFF021B14)
        ),
        start = Offset(0f, 0f),
        end = Offset(1000f, 1800f)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(emeraldBrush)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val borderPadding = 18.dp.toPx()
            val innerPadding = 26.dp.toPx()
            val goldStroke = Color(0xFFDFB448)
            val softGold = Color(0x88E2C275)

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
                style = Stroke(width = 1f)
            )

            val cornerSize = 24.dp.toPx()
            drawLine(goldStroke, Offset(borderPadding, borderPadding + cornerSize), Offset(borderPadding + cornerSize, borderPadding), strokeWidth = 2f)
            drawLine(goldStroke, Offset(w - borderPadding - cornerSize, borderPadding), Offset(w - borderPadding, borderPadding + cornerSize), strokeWidth = 2f)
            drawLine(goldStroke, Offset(borderPadding, h - borderPadding - cornerSize), Offset(borderPadding + cornerSize, h - borderPadding), strokeWidth = 2f)
            drawLine(goldStroke, Offset(w - borderPadding - cornerSize, h - borderPadding), Offset(w - borderPadding, h - borderPadding - cornerSize), strokeWidth = 2f)
        }

        CoverSpineOverlay()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 36.dp, end = 28.dp, top = 36.dp, bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "ROYAL EMERALD EDITION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.5.sp,
                    color = Color(0xFFE2C275)
                )

                Spacer(modifier = Modifier.height(4.dp))
                Canvas(modifier = Modifier.size(width = 110.dp, height = 3.dp)) {
                    drawLine(
                        color = Color(0xFFDFB448),
                        start = Offset(0f, 1.5f),
                        end = Offset(size.width, 1.5f),
                        strokeWidth = 1.5f
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
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

                        drawCircle(
                            brush = Brush.sweepGradient(listOf(Color(0xFFDFB448), Color(0xFFFFF0B8), Color(0xFFBA8A24), Color(0xFFDFB448))),
                            radius = radius,
                            center = Offset(cx, cy),
                            style = Stroke(width = 3.dp.toPx())
                        )

                        drawCircle(
                            color = Color(0x44000000),
                            radius = radius - 6.dp.toPx(),
                            center = Offset(cx, cy)
                        )
                    }

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

                Text(
                    text = settings.bookTitle.ifBlank { settings.authorName.uppercase() },
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Serif,
                    letterSpacing = 1.5.sp,
                    color = Color(0xFFFFF8E1),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = settings.bookSubtitle,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.2.sp,
                    color = Color(0xFFE2C275),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = settings.bookVolume,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.8.sp,
                    color = Color(0xFFB0C4DE)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onOpenBook,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("open_notebook_emerald_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF032219),
                        contentColor = Color(0xFFDFB448)
                    ),
                    shape = RoundedCornerShape(26.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.horizontalGradient(listOf(Color(0xFFDFB448), Color(0xFF81C784)))
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
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.horizontalGradient(listOf(Color(0xFFDFB448), Color(0xFF81C784)))
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
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.horizontalGradient(listOf(Color(0xFFDFB448), Color(0xFF81C784)))
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
private fun MidnightSapphireCoverContent(
    settings: NotebookSettings,
    totalNotesCount: Int,
    onOpenBook: () -> Unit,
    onChangeCoverClick: () -> Unit,
    onCustomizeDetailsClick: () -> Unit
) {
    val sapphireBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF0A1128),
            Color(0xFF101F42),
            Color(0xFF1C3879),
            Color(0xFF0F2042),
            Color(0xFF050A18)
        ),
        start = Offset(0f, 0f),
        end = Offset(1000f, 1800f)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(sapphireBrush)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val borderPadding = 18.dp.toPx()
            val innerPadding = 26.dp.toPx()
            val silverGoldStroke = Color(0xFFE2E8F0)
            val glowCyan = Color(0x6638BDF8)

            drawRect(
                color = silverGoldStroke,
                topLeft = Offset(borderPadding, borderPadding),
                size = Size(w - borderPadding * 2, h - borderPadding * 2),
                style = Stroke(width = 2f)
            )

            drawRect(
                color = glowCyan,
                topLeft = Offset(innerPadding, innerPadding),
                size = Size(w - innerPadding * 2, h - innerPadding * 2),
                style = Stroke(width = 1f)
            )

            val cornerSize = 24.dp.toPx()
            drawLine(silverGoldStroke, Offset(borderPadding, borderPadding + cornerSize), Offset(borderPadding + cornerSize, borderPadding), strokeWidth = 2f)
            drawLine(silverGoldStroke, Offset(w - borderPadding - cornerSize, borderPadding), Offset(w - borderPadding, borderPadding + cornerSize), strokeWidth = 2f)
            drawLine(silverGoldStroke, Offset(borderPadding, h - borderPadding - cornerSize), Offset(borderPadding + cornerSize, h - borderPadding), strokeWidth = 2f)
            drawLine(silverGoldStroke, Offset(w - borderPadding - cornerSize, h - borderPadding), Offset(w - borderPadding, h - borderPadding - cornerSize), strokeWidth = 2f)
        }

        CoverSpineOverlay()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 36.dp, end = 28.dp, top = 36.dp, bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "MIDNIGHT SAPPHIRE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.5.sp,
                    color = Color(0xFF93C5FD)
                )

                Spacer(modifier = Modifier.height(4.dp))
                Canvas(modifier = Modifier.size(width = 110.dp, height = 3.dp)) {
                    drawLine(
                        color = Color(0xFF60A5FA),
                        start = Offset(0f, 1.5f),
                        end = Offset(size.width, 1.5f),
                        strokeWidth = 1.5f
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
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

                        drawCircle(
                            brush = Brush.sweepGradient(listOf(Color(0xFF60A5FA), Color(0xFFE2E8F0), Color(0xFF38BDF8), Color(0xFF60A5FA))),
                            radius = radius,
                            center = Offset(cx, cy),
                            style = Stroke(width = 3.dp.toPx())
                        )

                        drawCircle(
                            color = Color(0x55000000),
                            radius = radius - 6.dp.toPx(),
                            center = Offset(cx, cy)
                        )
                    }

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
                            color = Color(0xFFF8FAFC),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = settings.bookTitle.ifBlank { settings.authorName.uppercase() },
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Serif,
                    letterSpacing = 1.5.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = settings.bookSubtitle,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.2.sp,
                    color = Color(0xFF93C5FD),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = settings.bookVolume,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.8.sp,
                    color = Color(0xFFCBD5E1)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onOpenBook,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("open_notebook_sapphire_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0A1128),
                        contentColor = Color(0xFF93C5FD)
                    ),
                    shape = RoundedCornerShape(26.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.horizontalGradient(listOf(Color(0xFF60A5FA), Color(0xFF38BDF8)))
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
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.horizontalGradient(listOf(Color(0x88FFFFFF), Color(0xFF60A5FA)))
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
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.horizontalGradient(listOf(Color(0x88FFFFFF), Color(0xFF60A5FA)))
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
private fun CrimsonRoyaltyCoverContent(
    settings: NotebookSettings,
    totalNotesCount: Int,
    onOpenBook: () -> Unit,
    onChangeCoverClick: () -> Unit,
    onCustomizeDetailsClick: () -> Unit
) {
    val crimsonBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF2E040B),
            Color(0xFF4A0815),
            Color(0xFF700D22),
            Color(0xFF3B0610),
            Color(0xFF1F0207)
        ),
        start = Offset(0f, 0f),
        end = Offset(1000f, 1800f)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(crimsonBrush)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val borderPadding = 18.dp.toPx()
            val innerPadding = 26.dp.toPx()
            val goldStroke = Color(0xFFFFD54F)
            val warmGold = Color(0x99FFE082)

            drawRect(
                color = goldStroke,
                topLeft = Offset(borderPadding, borderPadding),
                size = Size(w - borderPadding * 2, h - borderPadding * 2),
                style = Stroke(width = 2.5f)
            )

            drawRect(
                color = warmGold,
                topLeft = Offset(innerPadding, innerPadding),
                size = Size(w - innerPadding * 2, h - innerPadding * 2),
                style = Stroke(width = 1.2f)
            )

            val cornerSize = 24.dp.toPx()
            drawLine(goldStroke, Offset(borderPadding, borderPadding + cornerSize), Offset(borderPadding + cornerSize, borderPadding), strokeWidth = 2f)
            drawLine(goldStroke, Offset(w - borderPadding - cornerSize, borderPadding), Offset(w - borderPadding, borderPadding + cornerSize), strokeWidth = 2f)
            drawLine(goldStroke, Offset(borderPadding, h - borderPadding - cornerSize), Offset(borderPadding + cornerSize, h - borderPadding), strokeWidth = 2f)
            drawLine(goldStroke, Offset(w - borderPadding - cornerSize, h - borderPadding), Offset(w - borderPadding, h - borderPadding - cornerSize), strokeWidth = 2f)
        }

        CoverSpineOverlay()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 36.dp, end = 28.dp, top = 36.dp, bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "CRIMSON ROYALTY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.5.sp,
                    color = Color(0xFFFFE082)
                )

                Spacer(modifier = Modifier.height(4.dp))
                Canvas(modifier = Modifier.size(width = 110.dp, height = 3.dp)) {
                    drawLine(
                        color = Color(0xFFFFD54F),
                        start = Offset(0f, 1.5f),
                        end = Offset(size.width, 1.5f),
                        strokeWidth = 1.5f
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
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

                        drawCircle(
                            brush = Brush.sweepGradient(listOf(Color(0xFFFFD54F), Color(0xFFFF8A80), Color(0xFFFFD54F))),
                            radius = radius,
                            center = Offset(cx, cy),
                            style = Stroke(width = 3.dp.toPx())
                        )

                        drawCircle(
                            color = Color(0x44000000),
                            radius = radius - 6.dp.toPx(),
                            center = Offset(cx, cy)
                        )
                    }

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

                Text(
                    text = settings.bookTitle.ifBlank { settings.authorName.uppercase() },
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Serif,
                    letterSpacing = 1.5.sp,
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

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onOpenBook,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("open_notebook_crimson_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2E040B),
                        contentColor = Color(0xFFFFE082)
                    ),
                    shape = RoundedCornerShape(26.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.horizontalGradient(listOf(Color(0xFFFFD54F), Color(0xFFFF5252)))
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
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.horizontalGradient(listOf(Color(0xFFFFD54F), Color(0xFFFF8A80)))
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
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.horizontalGradient(listOf(Color(0xFFFFD54F), Color(0xFFFF8A80)))
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
private fun ObsidianPlatinumCoverContent(
    settings: NotebookSettings,
    totalNotesCount: Int,
    onOpenBook: () -> Unit,
    onChangeCoverClick: () -> Unit,
    onCustomizeDetailsClick: () -> Unit
) {
    val obsidianBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF121212),
            Color(0xFF1E1E1E),
            Color(0xFF282828),
            Color(0xFF181818),
            Color(0xFF0A0A0A)
        ),
        start = Offset(0f, 0f),
        end = Offset(1000f, 1800f)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(obsidianBrush)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val borderPadding = 18.dp.toPx()
            val innerPadding = 26.dp.toPx()
            val platinumStroke = Color(0xFFE0E0E0)
            val subtleSilver = Color(0x66BDBDBD)

            drawRect(
                color = platinumStroke,
                topLeft = Offset(borderPadding, borderPadding),
                size = Size(w - borderPadding * 2, h - borderPadding * 2),
                style = Stroke(width = 2.5f)
            )

            drawRect(
                color = subtleSilver,
                topLeft = Offset(innerPadding, innerPadding),
                size = Size(w - innerPadding * 2, h - innerPadding * 2),
                style = Stroke(width = 1f)
            )

            val cornerSize = 24.dp.toPx()
            drawLine(platinumStroke, Offset(borderPadding, borderPadding + cornerSize), Offset(borderPadding + cornerSize, borderPadding), strokeWidth = 2f)
            drawLine(platinumStroke, Offset(w - borderPadding - cornerSize, borderPadding), Offset(w - borderPadding, borderPadding + cornerSize), strokeWidth = 2f)
            drawLine(platinumStroke, Offset(borderPadding, h - borderPadding - cornerSize), Offset(borderPadding + cornerSize, h - borderPadding), strokeWidth = 2f)
            drawLine(platinumStroke, Offset(w - borderPadding - cornerSize, h - borderPadding), Offset(w - borderPadding, h - borderPadding - cornerSize), strokeWidth = 2f)
        }

        CoverSpineOverlay()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 36.dp, end = 28.dp, top = 36.dp, bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "OBSIDIAN PLATINUM",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.5.sp,
                    color = Color(0xFFE0E0E0)
                )

                Spacer(modifier = Modifier.height(4.dp))
                Canvas(modifier = Modifier.size(width = 110.dp, height = 3.dp)) {
                    drawLine(
                        color = Color(0xFFBDBDBD),
                        start = Offset(0f, 1.5f),
                        end = Offset(size.width, 1.5f),
                        strokeWidth = 1.5f
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
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

                        drawCircle(
                            brush = Brush.sweepGradient(listOf(Color(0xFFE0E0E0), Color(0xFF9E9E9E), Color(0xFFFFFFFF), Color(0xFFE0E0E0))),
                            radius = radius,
                            center = Offset(cx, cy),
                            style = Stroke(width = 3.dp.toPx())
                        )

                        drawCircle(
                            color = Color(0x66000000),
                            radius = radius - 6.dp.toPx(),
                            center = Offset(cx, cy)
                        )
                    }

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
                            color = Color(0xFFFFFFFF),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = settings.bookTitle.ifBlank { settings.authorName.uppercase() },
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Serif,
                    letterSpacing = 1.5.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = settings.bookSubtitle,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.2.sp,
                    color = Color(0xFFBDBDBD),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = settings.bookVolume,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.8.sp,
                    color = Color(0xFF9E9E9E)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onOpenBook,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("open_notebook_obsidian_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF212121),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(26.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.horizontalGradient(listOf(Color(0xFFE0E0E0), Color(0xFF9E9E9E)))
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
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.horizontalGradient(listOf(Color(0xFFE0E0E0), Color(0xFF9E9E9E)))
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
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.horizontalGradient(listOf(Color(0xFFE0E0E0), Color(0xFF9E9E9E)))
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
private fun SunsetAuroraCoverContent(
    settings: NotebookSettings,
    totalNotesCount: Int,
    onOpenBook: () -> Unit,
    onChangeCoverClick: () -> Unit,
    onCustomizeDetailsClick: () -> Unit
) {
    val sunsetBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF311B92),
            Color(0xFF880E4F),
            Color(0xFFAD1457),
            Color(0xFFD81B60),
            Color(0xFFE65100),
            Color(0xFFFF8F00)
        ),
        start = Offset(0f, 0f),
        end = Offset(1000f, 1800f)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(sunsetBrush)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val borderPadding = 18.dp.toPx()
            val innerPadding = 26.dp.toPx()
            val goldStroke = Color(0xFFFFD54F)
            val softGlow = Color(0x99FFE082)

            drawRect(
                color = goldStroke,
                topLeft = Offset(borderPadding, borderPadding),
                size = Size(w - borderPadding * 2, h - borderPadding * 2),
                style = Stroke(width = 2.5f)
            )

            drawRect(
                color = softGlow,
                topLeft = Offset(innerPadding, innerPadding),
                size = Size(w - innerPadding * 2, h - innerPadding * 2),
                style = Stroke(width = 1f)
            )

            val cornerSize = 24.dp.toPx()
            drawLine(goldStroke, Offset(borderPadding, borderPadding + cornerSize), Offset(borderPadding + cornerSize, borderPadding), strokeWidth = 2f)
            drawLine(goldStroke, Offset(w - borderPadding - cornerSize, borderPadding), Offset(w - borderPadding, borderPadding + cornerSize), strokeWidth = 2f)
            drawLine(goldStroke, Offset(borderPadding, h - borderPadding - cornerSize), Offset(borderPadding + cornerSize, h - borderPadding), strokeWidth = 2f)
            drawLine(goldStroke, Offset(w - borderPadding - cornerSize, h - borderPadding), Offset(w - borderPadding, h - borderPadding - cornerSize), strokeWidth = 2f)
        }

        CoverSpineOverlay()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 36.dp, end = 28.dp, top = 36.dp, bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "SUNSET AURORA",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.5.sp,
                    color = Color(0xFFFFE082)
                )

                Spacer(modifier = Modifier.height(4.dp))
                Canvas(modifier = Modifier.size(width = 110.dp, height = 3.dp)) {
                    drawLine(
                        color = Color(0xFFFFD54F),
                        start = Offset(0f, 1.5f),
                        end = Offset(size.width, 1.5f),
                        strokeWidth = 1.5f
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
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

                        drawCircle(
                            brush = Brush.sweepGradient(listOf(Color(0xFFFFD54F), Color(0xFFFF4081), Color(0xFFFFAB00), Color(0xFFFFD54F))),
                            radius = radius,
                            center = Offset(cx, cy),
                            style = Stroke(width = 3.dp.toPx())
                        )

                        drawCircle(
                            color = Color(0x44000000),
                            radius = radius - 6.dp.toPx(),
                            center = Offset(cx, cy)
                        )
                    }

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

                Text(
                    text = settings.bookTitle.ifBlank { settings.authorName.uppercase() },
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Serif,
                    letterSpacing = 1.5.sp,
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
                    color = Color(0xFFFFCC80)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onOpenBook,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("open_notebook_sunset_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF311B92),
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
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.horizontalGradient(listOf(Color(0xFFFFD54F), Color(0xFFFF4081)))
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
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.horizontalGradient(listOf(Color(0xFFFFD54F), Color(0xFFFF4081)))
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
