package com.example.saahas.Models.Room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reports")
data class Report(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val latitude: Double,
    val longitude: Double,
    val city: String?,
    val category: String,
    val description: String?,
    val mediaUrl: String?,
    val createdAt: String
)