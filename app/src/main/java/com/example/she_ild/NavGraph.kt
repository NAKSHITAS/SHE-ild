package com.example.she_ild

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import com.example.she_ild.ui.theme.EmergencyContactScreen
import com.example.she_ild.ui.theme.EmergencyServiceScreen
import com.example.she_ild.ui.theme.PermissionScreen
import com.example.she_ild.ui.theme.PhoneNumberScreen
import com.example.she_ild.ui.theme.SplashScreen
import com.example.she_ild.ui.theme.SHEildViewModel
import com.example.she_ild.Screen

@Composable
fun SHEildNavGraph(
    navController: NavHostController,
    viewModel: SHEildViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(route = Screen.Splash.route) {
            SplashScreen {
                navController.navigate(Screen.Register.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            }
        }
        composable(route = Screen.Register.route) {
            PhoneNumberScreen(
                viewModel = viewModel,
                onContinueClicked = {
                    navController.navigate(Screen.EmergencySetup.route)
                }
            )
        }
        composable(route = Screen.EmergencySetup.route) {
            EmergencyContactScreen(
                viewModel = viewModel,
                onContinueClicked = {
                    navController.navigate(Screen.Permissions.route)
                }
            )
        }
        composable(route = Screen.Permissions.route) {
            PermissionScreen(
                onPermissionsGranted = {
                    navController.navigate(Screen.Home.route)
                }
            )
        }

        composable(route = Screen.Home.route) {
            EmergencyServiceScreen(viewModel = viewModel)
        }
    }
}
