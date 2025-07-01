package com.example.saahas.Models.Room

import kotlinx.coroutines.flow.Flow

class ReportRepository(private val reportDao: ReportDao) {
    suspend fun submitReport(report: Report) {
        reportDao.insert(report)
    }

    fun getAllReports(): Flow<List<Report>> {
        return reportDao.getAllReports()
    }
}