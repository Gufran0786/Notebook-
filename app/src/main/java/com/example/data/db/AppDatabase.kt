package com.example.data.db

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import com.example.data.model.NotebookPage
import com.example.data.model.NotebookSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface NotebookDao {
    @Query("SELECT * FROM notebook_pages ORDER BY pageIndex ASC")
    fun getAllPages(): Flow<List<NotebookPage>>

    @Query("SELECT * FROM notebook_pages WHERE id = :id LIMIT 1")
    suspend fun getPageById(id: Long): NotebookPage?

    @Query("SELECT COUNT(*) FROM notebook_pages")
    suspend fun getPageCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPage(page: NotebookPage): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPages(pages: List<NotebookPage>)

    @Update
    suspend fun updatePage(page: NotebookPage)

    @Query("DELETE FROM notebook_pages WHERE id = :id")
    suspend fun deletePageById(id: Long)

    @Query("SELECT * FROM notebook_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<NotebookSettings?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: NotebookSettings)
}

@Database(entities = [NotebookPage::class, NotebookSettings::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun notebookDao(): NotebookDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "notebook_database.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
