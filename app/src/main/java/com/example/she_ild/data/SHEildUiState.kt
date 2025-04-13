package com.example.she_ild.data

data class SHEildUiState(
    val userName: String = "",
    val phoneNumber: String = "",
    val emergencyContactName: String = "",
    val emergencyContactNumber: String = "",
    val isServiceRunning: Boolean = false,
    val permissionStatus: PermissionStatus = PermissionStatus.NotGranted
)

enum class PermissionStatus {
    Granted,
    NotGranted,
    DeniedPermanently
}
