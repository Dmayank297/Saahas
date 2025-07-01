package com.example.saahas.ui.Screens.SpeechRecognation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class VoiceControlViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VoiceControlViewModel::class.java)) {
            return VoiceControlViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}