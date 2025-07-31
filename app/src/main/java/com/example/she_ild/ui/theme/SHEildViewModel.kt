package com.example.she_ild.ui.theme

import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Looper
import android.telephony.SmsManager
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.*
import com.example.she_ild.data.PermissionStatus
import com.example.she_ild.data.SHEildUiState
import com.example.she_ild.service.ShakeDetectionService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SHEildViewModel : ViewModel() {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var currentLocation: Location? = null

    // ----------------- UI STATE -----------------
    private val _uiState = MutableStateFlow(SHEildUiState())
    val uiState: StateFlow<SHEildUiState> = _uiState

    private val _shakeThreshold = MutableStateFlow(2.7f) // Default shake sensitivity
    val shakeThreshold: StateFlow<Float> = _shakeThreshold

    // ----------------- STATE UPDATERS -----------------
    fun updateUserName(name: String) {
        _uiState.update { it.copy(userName = name.trim()) }
    }

    fun updatePhoneNumber(number: String) {
        _uiState.update { it.copy(phoneNumber = number.trim()) }
    }

    fun updateEmergencyContactName(name: String) {
        _uiState.update { it.copy(emergencyContactName = name.trim()) }
    }

    fun updateEmergencyContactNumber(number: String) {
        _uiState.update { it.copy(emergencyContactNumber = number.trim()) }
    }

    fun updatePermissionStatus(status: PermissionStatus) {
        _uiState.update { it.copy(permissionStatus = status) }
    }

    fun updateShakeThreshold(newThreshold: Float) {
        _shakeThreshold.value = newThreshold
    }

    // ----------------- SERVICE CONTROL -----------------
    fun setServiceRunning(context: Context, isRunning: Boolean) {
        _uiState.update { it.copy(isServiceRunning = isRunning) }

        val serviceIntent = Intent(context, ShakeDetectionService::class.java)
        if (isRunning) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        } else {
            context.stopService(serviceIntent)
        }
    }

    // ----------------- EMERGENCY ACTIONS -----------------
    fun onShakeDetected(context: Context) {
        Log.d("SHEildViewModel", "Shake detected!")
        val state = uiState.value

        if (!state.isServiceRunning) return

        val contactNumber = state.emergencyContactNumber
        val userName = state.userName.ifBlank { "Someone" }

        if (contactNumber.isNotBlank()) {
            getCurrentLocationAndSendSms(context, contactNumber, userName)
        }
    }

    private fun getCurrentLocationAndSendSms(context: Context, contactNumber: String, userName: String) {
        if (fusedLocationClient == null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        }

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(2000)
            .setMaxUpdateDelayMillis(10000)
            .build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    currentLocation = location
                    Log.d("SHEildViewModel", "Location obtained: ${location.latitude}, ${location.longitude}")
                    sendSmsWithLocation(context, contactNumber, userName, location)
                    makeEmergencyCall(context, contactNumber)
                    fusedLocationClient?.removeLocationUpdates(this)
                }
            }
        }

        try {
            // First try to get last known location
            fusedLocationClient?.lastLocation?.addOnSuccessListener { location ->
                if (location != null) {
                    currentLocation = location
                    Log.d("SHEildViewModel", "Using last known location: ${location.latitude}, ${location.longitude}")
                    sendSmsWithLocation(context, contactNumber, userName, location)
                    makeEmergencyCall(context, contactNumber)
                } else {
                    // Request fresh location
                    Log.d("SHEildViewModel", "Requesting fresh location...")
                    fusedLocationClient?.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.getMainLooper()
                    )
                }
            }?.addOnFailureListener { exception ->
                Log.e("SHEildViewModel", "Failed to get location", exception)
                // Send SMS without location as fallback
                sendSmsWithLocation(context, contactNumber, userName, null)
                makeEmergencyCall(context, contactNumber)
            }
        } catch (e: SecurityException) {
            Log.e("SHEildViewModel", "Location permission not granted", e)
            // Send SMS without location as fallback
            sendSmsWithLocation(context, contactNumber, userName, null)
            makeEmergencyCall(context, contactNumber)
        }
    }

    private fun sendSmsWithLocation(context: Context, contactNumber: String, userName: String, location: Location?) {
        val locationText = if (location != null) {
            "Location: https://www.google.com/maps/search/?api=1&query=${location.latitude},${location.longitude}"
        } else {
            "Location: Unable to determine current location"
        }
        
        val message = "🚨 EMERGENCY! $userName needs immediate help. Please respond ASAP.\n" +
                "$locationText\n" +
                "Time: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())}\n" +
                "Please contact authorities immediately if needed."

        try {
            Log.d("SHEildViewModel", "Sending SMS to $contactNumber")
            val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                context.getSystemService(SmsManager::class.java)
            } else {
                SmsManager.getDefault()
            }

            val parts = smsManager.divideMessage(message) // 👈 splits into safe chunks
            smsManager.sendMultipartTextMessage(contactNumber, null, parts, null, null)
            Log.d("SHEildViewModel", "SMS sent successfully")

        } catch (e: Exception) {
            Log.e("SHEildViewModel", "Failed to send SMS", e)
            e.printStackTrace()
        }
    }


    private fun makeEmergencyCall(context: Context, contactNumber: String) {
        try {
            Log.d("SHEildViewModel", "Making emergency call to $contactNumber")
            val callIntent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:$contactNumber")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(callIntent)
        } catch (e: SecurityException) {
            Log.e("SHEildViewModel", "Call permission not granted", e)
            e.printStackTrace()
        } catch (e: Exception) {
            Log.e("SHEildViewModel", "Failed to make call", e)
            e.printStackTrace()
        }
    }
}
