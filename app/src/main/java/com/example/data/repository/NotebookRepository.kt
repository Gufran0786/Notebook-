package com.example.data.repository

import com.example.data.db.NotebookDao
import com.example.data.model.NotebookPage
import com.example.data.model.NotebookSettings
import kotlinx.coroutines.flow.Flow

class NotebookRepository(private val dao: NotebookDao) {
    val allPages: Flow<List<NotebookPage>> = dao.getAllPages()
    val settings: Flow<NotebookSettings?> = dao.getSettings()

    suspend fun getPageById(id: Long): NotebookPage? = dao.getPageById(id)

    suspend fun getPageCount(): Int = dao.getPageCount()

    suspend fun insertPage(page: NotebookPage): Long = dao.insertPage(page)

    suspend fun updatePage(page: NotebookPage) = dao.updatePage(page)

    suspend fun deletePage(id: Long) = dao.deletePageById(id)

    suspend fun saveSettings(settings: NotebookSettings) = dao.saveSettings(settings)

    suspend fun reorderPages(pages: List<NotebookPage>) {
        val updated = pages.mapIndexed { index, page ->
            page.copy(pageIndex = index)
        }
        dao.insertPages(updated)
    }
}
