package com.example.she_ild.model

data class SHEildUserData(
    val name: String,
    val phoneNumber: String,
    val emergencyContact: EmergencyContact
)

data class EmergencyContact(
    val contactName: String,
    val contactNumber: String
)



