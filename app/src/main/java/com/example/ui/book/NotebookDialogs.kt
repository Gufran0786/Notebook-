package com.example.ui.book

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FormatColorText
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.model.NotebookPage
import com.example.data.model.NotebookSettings
import com.example.util.ColorUtils
import com.example.util.ImageStorage
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CoverCustomizerDialog(
    settings: NotebookSettings,
    onDismiss: () -> Unit,
    onUploadCoverImage: () -> Unit,
    onResetToDefault: () -> Unit,
    onSelectCoverStyle: (String) -> Unit = {},
    onSaveDetails: (author: String, title: String, subtitle: String, volume: String) -> Unit
) {
    var author by remember(settings.authorName) { mutableStateOf(settings.authorName) }
    var title by remember(settings.bookTitle) { mutableStateOf(settings.bookTitle) }
    var subtitle by remember(settings.bookSubtitle) { mutableStateOf(settings.bookSubtitle) }
    var volume by remember(settings.bookVolume) { mutableStateOf(settings.bookVolume) }
    var selectedStyle by remember(settings.coverStyle) { mutableStateOf(settings.coverStyle) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Tune, contentDescription = null, tint = Color(0xFFC79824))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Customize Notebook Cover", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                Text(
                    text = "Choose your notebook cover style or upload custom art.",
                    fontSize = 13.sp,
                    color = Color(0xFF666666)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Cover Theme:", fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp, color = Color(0xFF333333))
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Rainbow Mixup Style Card
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (selectedStyle == "RAINBOW_MIXUP") Color(0xFFEDE7F6) else Color(0xFFF5F5F5),
                        border = if (selectedStyle == "RAINBOW_MIXUP") ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.Brush.horizontalGradient(listOf(Color(0xFFFF007F), Color(0xFF00E5FF)))
                        ) else null,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                selectedStyle = "RAINBOW_MIXUP"
                                onSelectCoverStyle("RAINBOW_MIXUP")
                            }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp)
                        ) {
                            Text("🌈", fontSize = 20.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Rainbow Mix", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Gold Ornate Style Card
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (selectedStyle == "GOLD_ORNATE") Color(0xFFFFF8E1) else Color(0xFFF5F5F5),
                        border = if (selectedStyle == "GOLD_ORNATE") ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.Brush.horizontalGradient(listOf(Color(0xFFFFD54F), Color(0xFFC79824)))
                        ) else null,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                selectedStyle = "GOLD_ORNATE"
                                onSelectCoverStyle("GOLD_ORNATE")
                            }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp)
                        ) {
                            Text("👑", fontSize = 20.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Classic Gold", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Cover Photo Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onUploadCoverImage,
                        modifier = Modifier.weight(1f).testTag("upload_cover_dialog_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C1E05), contentColor = Color(0xFFFFDF73)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Upload Photo", fontSize = 12.sp)
                    }

                    if (settings.customCoverUri != null) {
                        OutlinedButton(
                            onClick = onResetToDefault,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reset Cover", fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text("Author / Signature Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Embossed Book Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = subtitle,
                    onValueChange = { subtitle = it },
                    label = { Text("Cover Subtitle") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = volume,
                    onValueChange = { volume = it },
                    label = { Text("Volume & Year Tag") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSaveDetails(author, title, subtitle, volume)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC79824), contentColor = Color.Black)
            ) {
                Text("Save Changes", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableOfContentsSheet(
    pages: List<NotebookPage>,
    currentPageIndex: Int,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSelectPage: (targetPagerIndex: Int) -> Unit,
    onAddNewPage: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedTab by remember { mutableIntStateOf(0) } // 0: All Pages, 1: Bookmarks

    val filteredPages = remember(pages, searchQuery, selectedTab) {
        pages.filter { page ->
            val matchesTab = if (selectedTab == 1) page.isBookmarked else true
            val matchesQuery = if (searchQuery.isBlank()) {
                true
            } else {
                page.title.contains(searchQuery, ignoreCase = true) ||
                        page.content.contains(searchQuery, ignoreCase = true)
            }
            matchesTab && matchesQuery
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFFFAF9F6)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Notebook Contents",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        color = Color(0xFF1E1E1E)
                    )
                    Text(
                        text = "${pages.size} Pure White Pages • Unlimited",
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = {
                            onAddNewPage()
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1E88E5)
                        ),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("New Page", fontSize = 12.5.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Search your notes...", fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tabs: All vs Bookmarked
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                divider = {}
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("All Pages (${pages.size})", fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Bookmark, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Bookmarks (${pages.count { it.isBookmarked }})", fontWeight = FontWeight.SemiBold)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Jump to Cover Item
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onSelectPage(0)
                        onDismiss()
                    },
                colors = CardDefaults.cardColors(
                    containerColor = if (currentPageIndex == 0) Color(0xFFFFECB3) else Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = Color(0xFFB8860B),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Front Cover Page",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF2C1E05)
                        )
                        Text(
                            text = "Hardcover & Notebook Details",
                            fontSize = 12.sp,
                            color = Color(0xFF757575)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Pages List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(filteredPages) { index, page ->
                    val actualPageIndex = pages.indexOf(page) + 1
                    val isCurrent = currentPageIndex == actualPageIndex
                    val imgCount = ImageStorage.parseJsonArray(page.imageUrisJson).size

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelectPage(actualPageIndex)
                                onDismiss()
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCurrent) Color(0xFFE3F2FD) else Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                // Page badge
                                Surface(
                                    shape = CircleShape,
                                    color = if (isCurrent) Color(0xFF1976D2) else Color(0xFFEEEEEE),
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "$actualPageIndex",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isCurrent) Color.White else Color(0xFF616161)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = page.title.ifBlank { "Untitled Note" },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.5.sp,
                                        color = Color(0xFF212121),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    val snippet = page.content.ifBlank { "Empty page" }
                                    Text(
                                        text = snippet,
                                        fontSize = 12.sp,
                                        color = Color(0xFF757575),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            // Image badge and bookmark
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (imgCount > 0) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFE8F5E9),
                                        modifier = Modifier.padding(end = 6.dp)
                                    ) {
                                        Text(
                                            text = "📷 $imgCount",
                                            fontSize = 11.sp,
                                            color = Color(0xFF2E7D32),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                if (page.isBookmarked) {
                                    Icon(
                                        Icons.Default.Bookmark,
                                        contentDescription = "Bookmarked",
                                        tint = Color(0xFFD32F2F),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FullscreenImageViewerDialog(
    imagePath: String,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xF0000000))
        ) {
            AsyncImage(
                model = File(imagePath),
                contentDescription = "Fullscreen Attached Photo",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentScale = ContentScale.Fit
            )

            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(24.dp)
                    .size(44.dp)
                    .background(Color(0x88000000), CircleShape)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Close Photo",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InkColorPickerDialog(
    initialColorHex: String,
    onDismiss: () -> Unit,
    onApplyPageColor: (String) -> Unit,
    onApplyDefaultColor: (String) -> Unit
) {
    var selectedHex by remember(initialColorHex) { mutableStateOf(initialColorHex.ifBlank { "#1A1A1A" }) }
    var activeTab by remember { mutableIntStateOf(0) }

    // RGB Slider state
    val initialColor = remember(selectedHex) { ColorUtils.parseColor(selectedHex) }
    var redVal by remember(initialColor) { mutableFloatStateOf(((initialColor.toArgb() shr 16) and 0xFF).toFloat()) }
    var greenVal by remember(initialColor) { mutableFloatStateOf(((initialColor.toArgb() shr 8) and 0xFF).toFloat()) }
    var blueVal by remember(initialColor) { mutableFloatStateOf((initialColor.toArgb() and 0xFF).toFloat()) }
    var hexInputText by remember(selectedHex) { mutableStateOf(selectedHex.removePrefix("#")) }

    val currentColor = remember(selectedHex) { ColorUtils.parseColor(selectedHex) }
    val isLegible = remember(currentColor) { ColorUtils.isLegibleOnWhite(currentColor) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(0.95f),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .background(currentColor, CircleShape)
                            .border(2.dp, Color(0xFFE0E0E0), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Writing Ink Color",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Serif
                    )
                }

                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray, modifier = Modifier.size(20.dp))
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Live Writing Preview Card (Pure White Paper sample)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Live Ink Preview",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF888888)
                            )

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isLegible) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
                            ) {
                                Text(
                                    text = if (isLegible) "✓ High Contrast" else "⚠️ Very Pale",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isLegible) Color(0xFF2E7D32) else Color(0xFFE65100),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Title in selected color
                        Text(
                            text = "My Handwritten Notes",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            color = currentColor
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Body text in selected color
                        Text(
                            text = "Creativity is intelligence having fun. Every thought written here preserves your ideas in pure ink.",
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            fontFamily = FontFamily.SansSerif,
                            color = currentColor
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Active Hex: $selectedHex",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF9E9E9E)
                        )
                    }
                }

                // Tab Row for Curated vs Custom Mixer
                TabRow(
                    selectedTabIndex = activeTab,
                    containerColor = Color(0xFFF5F5F5),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .padding(bottom = 12.dp)
                ) {
                    Tab(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Curated Inks", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    )
                    Tab(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Custom Mixer", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    )
                }

                if (activeTab == 0) {
                    // Curated Classic & Artisan Inks
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val grouped = ColorUtils.curatedInks.groupBy { it.category }
                        grouped.forEach { (category, presets) ->
                            Text(
                                text = category,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF616161),
                                modifier = Modifier.padding(top = 4.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                presets.forEach { preset ->
                                    val isSelected = selectedHex.equals(preset.hex, ignoreCase = true)
                                    val chipColor = ColorUtils.parseColor(preset.hex)

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) Color(0xFFE3F2FD) else Color(0xFFFAFAFA))
                                            .border(
                                                width = if (isSelected) 2.dp else 1.dp,
                                                color = if (isSelected) Color(0xFF1976D2) else Color(0xFFE0E0E0),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .clickable {
                                                selectedHex = preset.hex
                                                hexInputText = preset.hex.removePrefix("#")
                                                val c = ColorUtils.parseColor(preset.hex)
                                                val argb = c.toArgb()
                                                redVal = ((argb shr 16) and 0xFF).toFloat()
                                                greenVal = ((argb shr 8) and 0xFF).toFloat()
                                                blueVal = (argb and 0xFF).toFloat()
                                            }
                                            .padding(vertical = 8.dp, horizontal = 4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .background(chipColor, CircleShape)
                                                .border(1.dp, Color(0x33000000), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isSelected) {
                                                Icon(
                                                    Icons.Default.Check,
                                                    contentDescription = "Selected",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = preset.name,
                                            fontSize = 9.5.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = Color(0xFF333333),
                                            textAlign = TextAlign.Center,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Custom RGB Mixer & Direct Hex Input
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Hex Input
                        OutlinedTextField(
                            value = hexInputText,
                            onValueChange = { input ->
                                val clean = input.filter { it.isLetterOrDigit() }.take(6)
                                hexInputText = clean
                                if (clean.length == 6) {
                                    val formatted = "#$clean"
                                    selectedHex = formatted
                                    val c = ColorUtils.parseColor(formatted)
                                    val argb = c.toArgb()
                                    redVal = ((argb shr 16) and 0xFF).toFloat()
                                    greenVal = ((argb shr 8) and 0xFF).toFloat()
                                    blueVal = (argb and 0xFF).toFloat()
                                }
                            },
                            label = { Text("Hex Color Code (#RRGGBB)") },
                            prefix = { Text("#", fontWeight = FontWeight.Bold) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Red Slider
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Red", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F), modifier = Modifier.width(50.dp))
                            Slider(
                                value = redVal,
                                onValueChange = {
                                    redVal = it
                                    val c = Color(redVal.toInt(), greenVal.toInt(), blueVal.toInt())
                                    selectedHex = ColorUtils.toHex(c)
                                    hexInputText = selectedHex.removePrefix("#")
                                },
                                valueRange = 0f..255f,
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFFD32F2F),
                                    activeTrackColor = Color(0xFFEF5350)
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            Text("${redVal.toInt()}", fontSize = 12.sp, modifier = Modifier.width(36.dp), textAlign = TextAlign.End)
                        }

                        // Green Slider
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Green", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF388E3C), modifier = Modifier.width(50.dp))
                            Slider(
                                value = greenVal,
                                onValueChange = {
                                    greenVal = it
                                    val c = Color(redVal.toInt(), greenVal.toInt(), blueVal.toInt())
                                    selectedHex = ColorUtils.toHex(c)
                                    hexInputText = selectedHex.removePrefix("#")
                                },
                                valueRange = 0f..255f,
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFF388E3C),
                                    activeTrackColor = Color(0xFF66BB6A)
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            Text("${greenVal.toInt()}", fontSize = 12.sp, modifier = Modifier.width(36.dp), textAlign = TextAlign.End)
                        }

                        // Blue Slider
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Blue", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2), modifier = Modifier.width(50.dp))
                            Slider(
                                value = blueVal,
                                onValueChange = {
                                    blueVal = it
                                    val c = Color(redVal.toInt(), greenVal.toInt(), blueVal.toInt())
                                    selectedHex = ColorUtils.toHex(c)
                                    hexInputText = selectedHex.removePrefix("#")
                                },
                                valueRange = 0f..255f,
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFF1976D2),
                                    activeTrackColor = Color(0xFF42A5F5)
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            Text("${blueVal.toInt()}", fontSize = 12.sp, modifier = Modifier.width(36.dp), textAlign = TextAlign.End)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = {
                        onApplyPageColor(selectedHex)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Apply to This Page", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                OutlinedButton(
                    onClick = {
                        onApplyDefaultColor(selectedHex)
                        onApplyPageColor(selectedHex)
                        onDismiss()
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Set as Default for All Pages", fontSize = 12.5.sp)
                }
            }
        },
        dismissButton = {}
    )
}

