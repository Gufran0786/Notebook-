package com.example.util

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

object ImageStorage {
    suspend fun saveImageToInternalStorage(context: Context, uri: Uri, prefix: String = "img"): String? {
        return withContext(Dispatchers.IO) {
            try {
                val directory = File(context.filesDir, "notebook_media")
                if (!directory.exists()) {
                    directory.mkdirs()
                }

                val fileName = "${prefix}_${UUID.randomUUID()}.jpg"
                val destinationFile = File(directory, fileName)

                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val outputStream = FileOutputStream(destinationFile)

                inputStream?.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }

                destinationFile.absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    fun parseJsonArray(jsonString: String): List<String> {
        val list = mutableListOf<String>()
        try {
            if (jsonString.isNotBlank()) {
                val array = JSONArray(jsonString)
                for (i in 0 until array.length()) {
                    list.add(array.getString(i))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun toJsonArray(list: List<String>): String {
        val array = JSONArray()
        for (item in list) {
            array.put(item)
        }
        return array.toString()
    }
}
