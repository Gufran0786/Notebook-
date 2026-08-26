package com.example.ui.book

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.util.ColorUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotebookScreen(
    viewModel: NotebookViewModel,
    modifier: Modifier = Modifier
) {
    val pages by viewModel.pages.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val currentPageIndex by viewModel.currentPageIndex.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedImagePreview by viewModel.selectedImagePreview.collectAsStateWithLifecycle()
    val showCoverCustomizer by viewModel.showCoverCustomizer.collectAsStateWithLifecycle()
    val showTableOfContents by viewModel.showTableOfContents.collectAsStateWithLifecycle()
    val saveFeedbackMessage by viewModel.saveFeedbackMessage.collectAsStateWithLifecycle()

    var pageToDeleteId by remember { mutableStateOf<Long?>(null) }
    var targetPageIdForImages by remember { mutableStateOf<Long?>(null) }
    var colorPickerTargetPageId by remember { mutableStateOf<Long?>(null) }
    var colorPickerInitialHex by remember { mutableStateOf("#1A1A1A") }

    // Multi-Image Picker for attaching photos to a note page
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty() && targetPageIdForImages != null) {
            viewModel.attachImages(targetPageIdForImages!!, uris)
            targetPageIdForImages = null
        }
    }

    // Cover Image Picker for uploading custom cover art
    val coverPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.setCustomCoverImage(uri)
        }
    }

    // Total book pages count in pager: 1 Cover + N Pages
    val totalPagerPages = 1 + pages.size

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
        containerColor = Color(0xFFEFECE6), // Elegant warm desk background
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (currentPageIndex == 0) settings.authorName else (pages.getOrNull(currentPageIndex - 1)?.title?.ifBlank { "Notebook Page $currentPageIndex" } ?: "Notebook"),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = Color(0xFF1F2937)
                        )
                        Text(
                            text = if (currentPageIndex == 0) "Hardcover Page • Flip to read" else "Pure White Page • $currentPageIndex of ${pages.size}",
                            fontSize = 11.5.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.setShowTableOfContents(true) },
                        modifier = Modifier.testTag("toc_button")
                    ) {
                        Icon(Icons.Default.List, contentDescription = "Table of Contents", tint = Color(0xFF1F2937))
                    }
                },
                actions = {
                    // Ink Color Quick Palette button when on a note page
                    if (currentPageIndex > 0) {
                        val currentNote = pages.getOrNull(currentPageIndex - 1)
                        val activeColor = ColorUtils.parseColor(currentNote?.inkColor?.ifBlank { settings.defaultInkColor } ?: settings.defaultInkColor)
                        IconButton(
                            onClick = {
                                if (currentNote != null) {
                                    colorPickerTargetPageId = currentNote.id
                                    colorPickerInitialHex = currentNote.inkColor.ifBlank { settings.defaultInkColor }
                                }
                            },
                            modifier = Modifier.testTag("ink_color_palette_top_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Palette,
                                    contentDescription = "Change Ink Color",
                                    tint = Color(0xFF1F2937)
                                )
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .size(8.dp)
                                        .background(activeColor, CircleShape)
                                        .border(1.dp, Color.White, CircleShape)
                                )
                            }
                        }
                    }

                    // Audio toggle for page turning rustle
                    IconButton(
                        onClick = { viewModel.toggleSoundEffects() },
                        modifier = Modifier.testTag("audio_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (settings.soundEffectsEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                            contentDescription = if (settings.soundEffectsEnabled) "Sound On" else "Sound Muted",
                            tint = if (settings.soundEffectsEnabled) Color(0xFF1F2937) else Color(0xFF9CA3AF)
                        )
                    }

                    // Cover Settings Action
                    IconButton(
                        onClick = { viewModel.setShowCoverCustomizer(true) },
                        modifier = Modifier.testTag("cover_settings_button")
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = "Cover Settings", tint = Color(0xFF1F2937))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFAF8F5)
                )
            )
        },
        bottomBar = {
            // Book Navigation Bar (Bottom Slider + Controls)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars),
                color = Color(0xFFFAF8F5),
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // Page Turner Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { viewModel.previousPage() },
                            enabled = currentPageIndex > 0,
                            modifier = Modifier.size(36.dp).testTag("prev_page_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Page", tint = if (currentPageIndex > 0) Color(0xFF1F2937) else Color(0xFFD1D5DB))
                        }

                        Slider(
                            value = currentPageIndex.toFloat(),
                            onValueChange = { viewModel.setCurrentPage(it.toInt()) },
                            valueRange = 0f..(totalPagerPages - 1).coerceAtLeast(1).toFloat(),
                            steps = (totalPagerPages - 2).coerceAtLeast(0),
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                                .testTag("page_slider"),
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFFC79824),
                                activeTrackColor = Color(0xFFC79824),
                                inactiveTrackColor = Color(0xFFE5E7EB)
                            )
                        )

                        IconButton(
                            onClick = { viewModel.nextPage() },
                            enabled = currentPageIndex < totalPagerPages - 1,
                            modifier = Modifier.size(36.dp).testTag("next_page_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Page", tint = if (currentPageIndex < totalPagerPages - 1) Color(0xFF1F2937) else Color(0xFFD1D5DB))
                        }
                    }

                    // Navigation Bottom Quick Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Current location chip
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFEAE8E3),
                            modifier = Modifier.clickable { viewModel.setShowTableOfContents(true) }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF4B5563))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (currentPageIndex == 0) "Cover Page" else "Page $currentPageIndex of ${pages.size}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF374151)
                                )
                            }
                        }

                        // Add new page action
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF1E88E5),
                            modifier = Modifier
                                .clickable {
                                    viewModel.addNewPage()
                                }
                                .testTag("add_page_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Add Blank Page",
                                    color = Color.White,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Google Play Books style realistic 3D page flip container
            GooglePlayBookPager(
                pageCount = totalPagerPages,
                currentPage = currentPageIndex,
                onPageChanged = { viewModel.setCurrentPage(it) },
                onRequestNewPage = { viewModel.addNewPage() },
                soundEffectsEnabled = settings.soundEffectsEnabled,
                modifier = Modifier.fillMaxSize()
            ) { pageIdx ->
                if (pageIdx == 0) {
                    // Page 0: Cover Page
                    CoverPageView(
                        settings = settings,
                        totalNotesCount = pages.size,
                        onOpenBook = { viewModel.setCurrentPage(1) },
                        onChangeCoverClick = {
                            coverPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        onCustomizeDetailsClick = { viewModel.setShowCoverCustomizer(true) },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Pages 1..N: Pure White Interior Note Pages
                    val noteIndex = pageIdx - 1
                    val notePage = pages.getOrNull(noteIndex)

                    if (notePage != null) {
                        PureWhitePageContent(
                            page = notePage,
                            pageNumber = pageIdx,
                            totalPages = pages.size,
                            defaultInkColor = settings.defaultInkColor,
                            saveFeedbackMessage = saveFeedbackMessage,
                            onTitleChange = { viewModel.updatePageTitle(notePage.id, it) },
                            onContentChange = { viewModel.updatePageContent(notePage.id, it) },
                            onToggleBookmark = { viewModel.toggleBookmark(notePage.id) },
                            onAddImageClick = {
                                targetPageIdForImages = notePage.id
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            onRemoveImage = { imageIndex ->
                                viewModel.removeImageFromPage(notePage.id, imageIndex)
                            },
                            onImageClick = { imagePath ->
                                viewModel.openImagePreview(imagePath)
                            },
                            onDeletePage = {
                                pageToDeleteId = notePage.id
                            },
                            onOpenColorPicker = {
                                colorPickerTargetPageId = notePage.id
                                colorPickerInitialHex = notePage.inkColor.ifBlank { settings.defaultInkColor }
                            },
                            onSelectQuickColor = { hex ->
                                viewModel.updatePageInkColor(notePage.id, hex)
                            },
                            onAddStraightLine = { line ->
                                viewModel.addStraightLineToPage(notePage.id, line)
                            },
                            onUndoLine = {
                                viewModel.undoLastLineFromPage(notePage.id)
                            },
                            onClearLines = {
                                viewModel.clearAllLinesFromPage(notePage.id)
                            },
                            onManualSave = {
                                viewModel.triggerSaveFeedback("Permanently saved ✓")
                            },
                            showLightLines = settings.showLightLines,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        // Empty fallback page
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Page not found", color = Color.Gray)
                        }
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (pageToDeleteId != null) {
        AlertDialog(
            onDismissRequest = { pageToDeleteId = null },
            title = { Text("Delete This Page?") },
            text = { Text("Are you sure you want to remove this notebook page and its attached photos? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        pageToDeleteId?.let { viewModel.deletePage(it) }
                        pageToDeleteId = null
                    }
                ) {
                    Text("Delete", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { pageToDeleteId = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Fullscreen Image Viewer
    if (selectedImagePreview != null) {
        FullscreenImageViewerDialog(
            imagePath = selectedImagePreview!!,
            onDismiss = { viewModel.openImagePreview(null) }
        )
    }

    // Cover Customizer Dialog
    if (showCoverCustomizer) {
        CoverCustomizerDialog(
            settings = settings,
            onDismiss = { viewModel.setShowCoverCustomizer(false) },
            onUploadCoverImage = {
                coverPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            onResetToDefault = { viewModel.resetToDefaultCover() },
            onSelectCoverStyle = { viewModel.setCoverStyle(it) },
            onSaveDetails = { author, title, subtitle, volume ->
                viewModel.updateCoverDetails(author, title, subtitle, volume)
            }
        )
    }

    // Table of Contents & Navigation Sheet
    if (showTableOfContents) {
        TableOfContentsSheet(
            pages = pages,
            currentPageIndex = currentPageIndex,
            searchQuery = searchQuery,
            onSearchQueryChange = { viewModel.setSearchQuery(it) },
            onSelectPage = { viewModel.setCurrentPage(it) },
            onAddNewPage = { viewModel.addNewPage() },
            onDismiss = { viewModel.setShowTableOfContents(false) }
        )
    }

    // Custom Writing Ink Color Picker Dialog
    if (colorPickerTargetPageId != null) {
        InkColorPickerDialog(
            initialColorHex = colorPickerInitialHex,
            onDismiss = { colorPickerTargetPageId = null },
            onApplyPageColor = { hex ->
                colorPickerTargetPageId?.let { pageId ->
                    viewModel.updatePageInkColor(pageId, hex)
                }
            },
            onApplyDefaultColor = { hex ->
                viewModel.setDefaultInkColor(hex)
            }
        )
    }
}
