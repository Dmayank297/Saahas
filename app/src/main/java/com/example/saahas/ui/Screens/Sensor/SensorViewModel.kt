package com.example.saahas.ui.Screens.Sensor

import android.app.Application
import android.content.Context
import android.hardware.*
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.saahas.Models.Room.ContactDao
import com.example.saahas.PermissionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sqrt

class SensorViewModel(
    application: Application,
    private val context: Context,
    private val permissionManager: PermissionManager,
    private val contactDao: ContactDao
) : AndroidViewModel(application), SensorEventListener {

    private val sensorManager: SensorManager = application.getSystemService(SensorManager::class.java)
    private var accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private var gyroscope: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    private var lastKnownLatLng: Pair<Double, Double>? = null

    // StateFlow for Shake Detection
    private val _shakeDetected = MutableStateFlow(false)
    val shakeDetected = _shakeDetected.asStateFlow()

    // StateFlow for Gyroscope Rotation Data
    private val _rotationData = MutableStateFlow(Triple(0f, 0f, 0f))
    val rotationData = _rotationData.asStateFlow()

    // Thresholds
    private val shakeThreshold = 80f  // Lower = more sensitive
    private val rotationThreshold = 2f  // Ignores tiny movements

    fun startSensors() {
        accelerometer?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
        gyroscope?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
        Log.d("SensorViewModel", "Sensors Started")
        Toast.makeText(context, "Shake Service Initiated", Toast.LENGTH_SHORT).show()
    }

    fun stopSensors() {
        sensorManager.unregisterListener(this)
        Log.d("SensorViewModel", "Sensors Stopped")
        Toast.makeText(context, "Shake Service Stopped", Toast.LENGTH_SHORT).show()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        viewModelScope.launch {
            when (event?.sensor?.type) {
                Sensor.TYPE_ACCELEROMETER -> handleShakeDetection(event.values)
                Sensor.TYPE_GYROSCOPE -> handleGyroscope(event.values)
            }
        }
    }

    private fun handleShakeDetection(values: FloatArray) {
        val (x, y, z) = values
        val acceleration = sqrt(x * x + y * y + z * z)

        if (acceleration > shakeThreshold) {
            _shakeDetected.value = true
            Log.d("SensorViewModel", "Shake Detected! Acceleration: $acceleration")
            Toast.makeText(context, "Shake Detected! Initiating SOS", Toast.LENGTH_SHORT).show()
            onSOSButtonClicked() // Automatically trigger SOS when shake is detected
        } else {
            _shakeDetected.value = false
        }
    }

    private fun handleGyroscope(values: FloatArray) {
        val (x, y, z) = values

        if (abs(x) > rotationThreshold || abs(y) > rotationThreshold || abs(z) > rotationThreshold) {
            _rotationData.value = Triple(x, y, z)
            Log.d("SensorViewModel", "Gyro Data -> X: $x, Y: $y, Z: $z")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    fun onSOSButtonClicked() {
        if (permissionManager.isPermissionGranted(android.Manifest.permission.SEND_SMS) &&
            permissionManager.isPermissionGranted(android.Manifest.permission.CALL_PHONE)) {
            viewModelScope.launch {
                val contacts = contactDao.getAllContacts()
                if (contacts.isNotEmpty()) {
                    val smsManager = SmsManager.getDefault()
                    val locationUrl = "https://www.google.com/maps?q=${getLatLng()}"
                    val message = "Level 3 Emergency! My location: $locationUrl"
                    contacts.forEach { contact ->
                        try {
                            smsManager.sendTextMessage(contact.phoneNumber, null, message, null, null)
                            Log.d("SaahasSOS", "SMS sent to ${contact.phoneNumber}")
                            Toast.makeText(context, "SOS Message Sent to ${contact.phoneNumber}", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Log.e("SaahasSOS", "Failed to send SMS to ${contact.phoneNumber}: ${e.message}")
                            Toast.makeText(context, "Failed to Send SOS to ${contact.phoneNumber}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Log.d("SaahasSOS", "No contacts available")
                    Toast.makeText(context, "No contacts available for SOS", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Log.d("SaahasSOS", "Permissions not granted")
            Toast.makeText(context, "Permissions not granted for SOS", Toast.LENGTH_SHORT).show()
            permissionManager.checkAndRequestPermission(
                android.Manifest.permission.SEND_SMS,
                android.Manifest.permission.CALL_PHONE
            )
        }
    }

    private fun getLatLng(): String {
        return lastKnownLatLng?.let { "${it.first},${it.second}" } ?: "unknown"
    }
}