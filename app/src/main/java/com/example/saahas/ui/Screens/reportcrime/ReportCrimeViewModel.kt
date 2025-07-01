package com.example.saahas.ui.Screens.reportcrime

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saahas.Models.Room.Report
import com.example.saahas.Models.Room.ReportRepository
import com.example.saahas.Models.Room.VoiceRecordingDatabase
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportCrimeViewModel(
    private val context: Context
) : ViewModel() {
    val location = mutableStateOf<android.location.Location?>(null)
    val city = mutableStateOf<String?>(null)
    val category = mutableStateOf("")
    val description = mutableStateOf("")
    val mediaUri = mutableStateOf<Uri?>(null)
    val isSubmitting = mutableStateOf(false)
    val reportResult = mutableStateOf<String?>(null)

    private val repository = ReportRepository(VoiceRecordingDatabase.getDatabase(context).reportDao())

    fun fetchLocation() {
        val dummyLocation = android.location.Location("").apply {
            latitude = 12.9716
            longitude = 77.5946
        }
        location.value = dummyLocation
        city.value = "Bengaluru"
    }

    fun adjustLocation(latitude: Double, longitude: Double) {
        val newLocation = android.location.Location("").apply {
            this.latitude = latitude
            this.longitude = longitude
        }
        location.value = newLocation
        city.value = "Adjusted Location"
    }

    fun setMediaUri(uri: Uri?) {
        mediaUri.value = uri
    }

    fun submitReport() {
        if (isSubmitting.value || location.value == null || category.value.isEmpty()) {
            reportResult.value = "error: Please select a location and category"
            return
        }

        isSubmitting.value = true
        viewModelScope.launch {
            try {
                val report = Report(
                    userId = "local_user", // Mock user ID
                    latitude = location.value?.latitude ?: 0.0,
                    longitude = location.value?.longitude ?: 0.0,
                    city = city.value,
                    category = category.value,
                    description = description.value.takeIf { it.isNotEmpty() },
                    mediaUrl = mediaUri.value?.toString(),
                    createdAt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(Date())
                )

                repository.submitReport(report)

                // Mock Twilio SMS
                println("Mock SMS: A new report regarding ${category.value} has been added.")

                reportResult.value = "success: Report submitted successfully"
            } catch (e: Exception) {
                reportResult.value = "error: ${e.message}"
            } finally {
                isSubmitting.value = false
            }
        }
    }
}