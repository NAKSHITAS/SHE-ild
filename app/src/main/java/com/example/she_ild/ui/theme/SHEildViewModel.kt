package com.example.she_ild.ui.theme

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.telephony.SmsManager
import androidx.lifecycle.ViewModel
import com.example.she_ild.data.PermissionStatus
import com.example.she_ild.data.SHEildUiState
import com.example.she_ild.service.ShakeDetectionService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class SHEildViewModel : ViewModel() {

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
        val state = uiState.value

        if (!state.isServiceRunning) return

        val contactNumber = state.emergencyContactNumber
        val userName = state.userName.ifBlank { "Someone" }

        if (contactNumber.isNotBlank()) {
            sendSms(context, contactNumber, userName)
            makeEmergencyCall(context, contactNumber)
        }
    }

    private fun sendSms(context: Context, contactNumber: String, userName: String) {
        val message = "🚨 Emergency! $userName needs help. Please respond ASAP. " +
                "Location: https://www.google.com/maps/search/?api=1&query=23.1910103,79.9844711 " +
                "Please stay safe and alert authorities immediately."

        try {
            val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                context.getSystemService(SmsManager::class.java)
            } else {
                SmsManager.getDefault()
            }

            val parts = smsManager.divideMessage(message) // 👈 splits into safe chunks
            smsManager.sendMultipartTextMessage(contactNumber, null, parts, null, null)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun makeEmergencyCall(context: Context, contactNumber: String) {
        try {
            val callIntent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:$contactNumber")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(callIntent)
        } catch (e: SecurityException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
