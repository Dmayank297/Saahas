package com.example.saahas.ui.Screens.reportcrime

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saahas.Models.Room.Report
import com.example.saahas.Models.Room.ReportRepository
import com.example.saahas.Models.Room.VoiceRecordingDatabase
import com.example.saahas.POST
import com.example.saahas.REPORT_CRIME
import com.example.saahas.VOLUNTEER
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CommunityReportViewModel(
    context: Context
) : ViewModel() {
    private val _reports = MutableStateFlow<List<Report>>(emptyList())
    val reports: StateFlow<List<Report>> = _reports

    private val repository = ReportRepository(VoiceRecordingDatabase.getDatabase(context).reportDao())

    init {
        fetchAllReports()
    }

    private fun fetchAllReports() {
        viewModelScope.launch {
            repository.getAllReports().collectLatest { reports ->
                _reports.value = reports
            }
        }
    }

    fun onFABClicked(openScreen: (String) -> Unit) {
        openScreen(REPORT_CRIME)
    }

    fun onVolunteerClick(openScreen: (String) -> Unit) {
        openScreen(VOLUNTEER)
    }

    fun onReadMoreClick(openScreen: (String) -> Unit) {
        openScreen(POST)
    }
}