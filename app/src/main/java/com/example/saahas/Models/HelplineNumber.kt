package com.example.saahas.Models

import androidx.annotation.DrawableRes

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "helplines")
data class HelplineNumber(
    val name: String,
    val number: String,
    @DrawableRes val iconResId: Int,
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
)
