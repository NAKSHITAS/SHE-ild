package com.example.she_ild

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Register : Screen("register")
    object Permissions : Screen("permissions")
    object EmergencySetup : Screen("emergency_setup")
    object EmergencyService : Screen("emergency_service")
    object Home : Screen("home")

    // If you need to pass arguments later, this structure allows route templating
    // object Profile : Screen("profile/{userId}")
}
