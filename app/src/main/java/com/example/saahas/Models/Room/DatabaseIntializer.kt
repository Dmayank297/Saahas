package com.example.saahas.Models.Room

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DatabaseInitializer {
    fun initialize(context: Context) {
        val dao = VoiceRecordingDatabase.getDatabase(context).reportDao()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)

        val sampleReports = listOf(
            Report(
                userId = "user1",
                latitude = 12.9716,
                longitude = 77.5946,
                city = "Bengaluru",
                category = "Harassment",
                description = "Incident reported near MG Road.",
                mediaUrl = null,
                createdAt = dateFormat.format(Date(System.currentTimeMillis() - 86400000)) // 1 day ago
            ),
            Report(
                userId = "user2",
                latitude = 12.9352,
                longitude = 77.6245,
                city = "Bengaluru",
                category = "Domestic Violence",
                description = "Domestic issue reported in Koramangala.",
                mediaUrl = "file://sample_image.jpg",
                createdAt = dateFormat.format(Date(System.currentTimeMillis() - 172800000)) // 2 days ago
            ),
            Report(
                userId = "user3",
                latitude = 12.9537,
                longitude = 77.6309,
                city = "Bengaluru",
                category = "Other",
                description = "General concern about safety in Indiranagar.",
                mediaUrl = null,
                createdAt = dateFormat.format(Date(System.currentTimeMillis() - 259200000)) // 3 days ago
            )
        )

        CoroutineScope(Dispatchers.IO).launch {
            sampleReports.forEach { dao.insert(it) }
        }
    }
}