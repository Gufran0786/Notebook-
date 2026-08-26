package com.example.ui.book

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.NotebookPage
import com.example.data.model.NotebookSettings
import com.example.data.repository.NotebookRepository
import com.example.util.ImageStorage
import com.example.util.LineDrawingUtils
import com.example.util.StraightLine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotebookViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NotebookRepository
    private var autoSaveJob: Job? = null

    val pages: StateFlow<List<NotebookPage>>
    val settings: StateFlow<NotebookSettings>

    private val _currentPageIndex = MutableStateFlow(0) // 0 = Cover Page, 1..N = Notebook Pages
    val currentPageIndex: StateFlow<Int> = _currentPageIndex.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedImagePreview = MutableStateFlow<String?>(null)
    val selectedImagePreview: StateFlow<String?> = _selectedImagePreview.asStateFlow()

    private val _showCoverCustomizer = MutableStateFlow(false)
    val showCoverCustomizer: StateFlow<Boolean> = _showCoverCustomizer.asStateFlow()

    private val _showTableOfContents = MutableStateFlow(false)
    val showTableOfContents: StateFlow<Boolean> = _showTableOfContents.asStateFlow()

    private val _saveFeedbackMessage = MutableStateFlow<String?>(null)
    val saveFeedbackMessage: StateFlow<String?> = _saveFeedbackMessage.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = NotebookRepository(db.notebookDao())

        pages = repository.allPages.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        settings = repository.settings
            .map { it ?: NotebookSettings() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = NotebookSettings()
            )

        // Seed initial welcoming page if fresh database
        viewModelScope.launch {
            if (repository.getPageCount() == 0) {
                val initialWelcomePage = NotebookPage(
                    pageIndex = 0,
                    title = "Welcome to your Notebook",
                    content = "This is your personal digital notebook with pure white pages and realistic Google Play Books style page flipping.\n\n✨ Highlights:\n• Unlimited pure white pages for infinite writing & thoughts\n• Authentic paper acoustics and 3D page curl\n• Attach photos directly onto any page using the top photo button\n• Customizable notebook cover\n• Drag, swipe, or tap page edges to turn pages smoothly\n• Automatic real-time saving",
                    imageUrisJson = "[]",
                    isBookmarked = true
                )
                repository.insertPage(initialWelcomePage)
            }
        }
    }

    fun setCurrentPage(index: Int) {
        _currentPageIndex.value = index
    }

    fun nextPage() {
        val total = 1 + pages.value.size // 1 for cover + pages
        if (_currentPageIndex.value < total - 1) {
            _currentPageIndex.value += 1
        } else {
            // If on the last page and user clicks next, seamlessly add a new page!
            addNewPage()
        }
    }

    fun previousPage() {
        if (_currentPageIndex.value > 0) {
            _currentPageIndex.value -= 1
        }
    }

    /**
     * Adds an unlimited blank page. If afterIndex is provided, inserts right after that page.
     */
    fun addNewPage(afterIndex: Int? = null): Int {
        val currentPages = pages.value
        val insertPosition = afterIndex ?: currentPages.size

        val newPage = NotebookPage(
            pageIndex = insertPosition,
            title = "",
            content = "",
            imageUrisJson = "[]"
        )
        viewModelScope.launch {
            repository.insertPage(newPage)
        }
        val targetIndex = 1 + insertPosition
        _currentPageIndex.value = targetIndex
        return targetIndex
    }

    /**
     * Quickly adds a batch of blank pages (e.g. 5 or 10 pages).
     */
    fun addBatchPages(count: Int) {
        viewModelScope.launch {
            val startIdx = pages.value.size
            for (i in 0 until count) {
                val newPage = NotebookPage(
                    pageIndex = startIdx + i,
                    title = "",
                    content = "",
                    imageUrisJson = "[]"
                )
                repository.insertPage(newPage)
            }
        }
    }

    fun updatePageTitle(pageId: Long, newTitle: String) {
        val currentList = pages.value
        val page = currentList.find { it.id == pageId } ?: return
        val updated = page.copy(title = newTitle, updatedAt = System.currentTimeMillis())

        autoSaveJob?.cancel()
        autoSaveJob = viewModelScope.launch {
            repository.updatePage(updated)
        }
    }

    fun updatePageContent(pageId: Long, newContent: String) {
        val currentList = pages.value
        val page = currentList.find { it.id == pageId } ?: return
        val updated = page.copy(content = newContent, updatedAt = System.currentTimeMillis())

        autoSaveJob?.cancel()
        autoSaveJob = viewModelScope.launch {
            delay(150) // Debounce rapid keystrokes
            repository.updatePage(updated)
        }
    }

    fun toggleBookmark(pageId: Long) {
        val currentList = pages.value
        val page = currentList.find { it.id == pageId } ?: return
        val updated = page.copy(isBookmarked = !page.isBookmarked)
        viewModelScope.launch {
            repository.updatePage(updated)
        }
    }

    fun attachImages(pageId: Long, uris: List<Uri>) {
        viewModelScope.launch {
            val context = getApplication<Application>()
            val currentList = pages.value
            val page = currentList.find { it.id == pageId } ?: return@launch
            val existingImages = ImageStorage.parseJsonArray(page.imageUrisJson).toMutableList()

            for (uri in uris) {
                val savedPath = ImageStorage.saveImageToInternalStorage(context, uri, "page_photo")
                if (savedPath != null) {
                    existingImages.add(savedPath)
                }
            }

            val updated = page.copy(
                imageUrisJson = ImageStorage.toJsonArray(existingImages),
                updatedAt = System.currentTimeMillis()
            )
            repository.updatePage(updated)
        }
    }

    fun removeImageFromPage(pageId: Long, imageIndex: Int) {
        viewModelScope.launch {
            val currentList = pages.value
            val page = currentList.find { it.id == pageId } ?: return@launch
            val existingImages = ImageStorage.parseJsonArray(page.imageUrisJson).toMutableList()

            if (imageIndex in existingImages.indices) {
                existingImages.removeAt(imageIndex)
                val updated = page.copy(
                    imageUrisJson = ImageStorage.toJsonArray(existingImages),
                    updatedAt = System.currentTimeMillis()
                )
                repository.updatePage(updated)
            }
        }
    }

    fun deletePage(pageId: Long) {
        viewModelScope.launch {
            repository.deletePage(pageId)
            // Adjust current page index if needed
            val remainingPagesCount = pages.value.size - 1
            if (_currentPageIndex.value > remainingPagesCount) {
                _currentPageIndex.value = remainingPagesCount.coerceAtLeast(0)
            }
        }
    }

    fun setCustomCoverImage(uri: Uri) {
        viewModelScope.launch {
            val context = getApplication<Application>()
            val savedPath = ImageStorage.saveImageToInternalStorage(context, uri, "cover_art")
            if (savedPath != null) {
                val current = settings.value
                val updated = current.copy(customCoverUri = savedPath)
                repository.saveSettings(updated)
            }
        }
    }

    fun resetToDefaultCover() {
        viewModelScope.launch {
            val current = settings.value
            val updated = current.copy(customCoverUri = null)
            repository.saveSettings(updated)
        }
    }

    fun updateCoverDetails(
        authorName: String,
        bookTitle: String,
        bookSubtitle: String,
        bookVolume: String
    ) {
        viewModelScope.launch {
            val current = settings.value
            val updated = current.copy(
                authorName = authorName,
                bookTitle = bookTitle,
                bookSubtitle = bookSubtitle,
                bookVolume = bookVolume
            )
            repository.saveSettings(updated)
        }
    }

    fun toggleSoundEffects() {
        viewModelScope.launch {
            val current = settings.value
            repository.saveSettings(current.copy(soundEffectsEnabled = !current.soundEffectsEnabled))
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun openImagePreview(path: String?) {
        _selectedImagePreview.value = path
    }

    fun setShowCoverCustomizer(show: Boolean) {
        _showCoverCustomizer.value = show
    }

    fun updatePageInkColor(pageId: Long, colorHex: String) {
        val currentList = pages.value
        val page = currentList.find { it.id == pageId } ?: return
        val updated = page.copy(inkColor = colorHex, updatedAt = System.currentTimeMillis())
        viewModelScope.launch {
            repository.updatePage(updated)
        }
    }

    fun setDefaultInkColor(colorHex: String) {
        viewModelScope.launch {
            val current = settings.value
            repository.saveSettings(current.copy(defaultInkColor = colorHex))
        }
    }

    fun setShowTableOfContents(show: Boolean) {
        _showTableOfContents.value = show
    }

    /**
     * Straight Line Drawing Tool Methods (Permanently saved in Room DB)
     */
    fun addStraightLineToPage(pageId: Long, line: StraightLine) {
        viewModelScope.launch {
            val currentList = pages.value
            val page = currentList.find { it.id == pageId } ?: return@launch
            val currentLines = LineDrawingUtils.parseLinesJson(page.inkDrawingJson).toMutableList()
            currentLines.add(line)

            val updated = page.copy(
                inkDrawingJson = LineDrawingUtils.linesToJson(currentLines),
                updatedAt = System.currentTimeMillis()
            )
            repository.updatePage(updated)
            triggerSaveFeedback("Line added & saved ✓")
        }
    }

    fun undoLastLineFromPage(pageId: Long) {
        viewModelScope.launch {
            val currentList = pages.value
            val page = currentList.find { it.id == pageId } ?: return@launch
            val currentLines = LineDrawingUtils.parseLinesJson(page.inkDrawingJson).toMutableList()
            if (currentLines.isNotEmpty()) {
                currentLines.removeAt(currentLines.size - 1)
                val updated = page.copy(
                    inkDrawingJson = LineDrawingUtils.linesToJson(currentLines),
                    updatedAt = System.currentTimeMillis()
                )
                repository.updatePage(updated)
                triggerSaveFeedback("Undone ✓")
            }
        }
    }

    fun clearAllLinesFromPage(pageId: Long) {
        viewModelScope.launch {
            val currentList = pages.value
            val page = currentList.find { it.id == pageId } ?: return@launch
            val updated = page.copy(
                inkDrawingJson = "[]",
                updatedAt = System.currentTimeMillis()
            )
            repository.updatePage(updated)
            triggerSaveFeedback("Cleared lines ✓")
        }
    }

    fun setCoverStyle(style: String) {
        viewModelScope.launch {
            val current = settings.value
            repository.saveSettings(current.copy(coverStyle = style))
        }
    }

    fun triggerSaveFeedback(message: String = "Permanently saved ✓") {
        viewModelScope.launch {
            _saveFeedbackMessage.value = message
            delay(1800)
            if (_saveFeedbackMessage.value == message) {
                _saveFeedbackMessage.value = null
            }
        }
    }
}
