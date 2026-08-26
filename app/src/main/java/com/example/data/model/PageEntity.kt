package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notebook_pages")
data class NotebookPage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val pageIndex: Int = 0,
    val title: String = "",
    val content: String = "",
    val imageUrisJson: String = "[]",
    val inkDrawingJson: String = "",
    val isBookmarked: Boolean = false,
    val inkColor: String = "#1A1A1A",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notebook_settings")
data class NotebookSettings(
    @PrimaryKey
    val id: Int = 1,
    val customCoverUri: String? = null,
    val authorName: String = "Gufran Khan",
    val bookTitle: String = "GUFRAN KHAN",
    val bookSubtitle: String = "A COLLECTION OF THOUGHTS, IDEAS & WRITINGS",
    val bookVolume: String = "VOLUME 1 | EST. 2024",
    val coverStyle: String = "GOLD_ORNATE",
    val fontSizeSp: Float = 16f,
    val soundEffectsEnabled: Boolean = true,
    val showLightLines: Boolean = false,
    val defaultInkColor: String = "#1A1A1A"
)
