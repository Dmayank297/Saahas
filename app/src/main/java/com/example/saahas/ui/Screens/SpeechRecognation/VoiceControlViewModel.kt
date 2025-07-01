package com.example.saahas.ui.Screens.SpeechRecognation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.example.saahas.Voice.Service.BuzzerService
import com.example.saahas.Voice.Service.VoiceBackgroundService
import com.example.saahas.Voice.Service.VoiceCommandService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class VoiceControlViewModel(
    private val context: Context
) : ViewModel() {
    private val buzzerService = BuzzerService()
    private val voiceService = VoiceCommandService(context) { command ->
        when (command) {
            "start buzzer", "begin buzz" -> {
                buzzerService.startBuzzer()
                _commandStatus.value = "Buzzer started"
            }
            "stop buzzer", "end buzz" -> {
                buzzerService.stopBuzzer()
                _commandStatus.value = "Buzzer stopped"
            }
            else -> _commandStatus.value = "Unrecognized: $command"
        }
    }

    // StateFlow for command status
    private val _commandStatus = MutableStateFlow("Say 'start buzzer' or 'stop buzzer'")
    val commandStatus = _commandStatus.asStateFlow()

    // StateFlow for listening state
    private val _isListening = MutableStateFlow(false)
    val isListening = _isListening.asStateFlow()

    // StateFlow for background service state
    private val _isBackgroundServiceRunning = MutableStateFlow(false)
    val isBackgroundServiceRunning = _isBackgroundServiceRunning.asStateFlow()

    init {
        checkPermissions()
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            _commandStatus.value = "Microphone permission required"
        }
    }

    fun startVoiceService() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            _isListening.value = true
            voiceService.startListening()
            _commandStatus.value = "Listening..."

            // Start the background service as well
            val serviceIntent = Intent(context, VoiceBackgroundService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
            _isBackgroundServiceRunning.value = true
            _commandStatus.value = "Background service started - Say 'Porcupine' or 'Alexa'"
        } else {
            _commandStatus.value = "Please grant microphone permission first"
        }
    }

    fun stopVoiceService() {
        _isListening.value = false
        voiceService.stopListening()
        _commandStatus.value = "Stopped listening"

        // Stop the background service
        context.stopService(Intent(context, VoiceBackgroundService::class.java))
        _isBackgroundServiceRunning.value = false
        _commandStatus.value = "Background service stopped"

        // Stop the buzzer as well
        buzzerService.stopBuzzer()
    }

    fun stopBuzzerManually() {
        buzzerService.stopBuzzer()
        _commandStatus.value = "Buzzer stopped manually"
    }

    override fun onCleared() {
        super.onCleared()
        voiceService.destroy()
        buzzerService.stopBuzzer()
    }
}