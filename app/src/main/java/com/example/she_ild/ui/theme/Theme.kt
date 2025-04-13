package com.example.she_ild.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Light theme color scheme
private val LightColorScheme = lightColorScheme(
    primary = EmpoweredPink,
    onPrimary = Color.White,
    secondary = LavenderMist,
    onSecondary = Color.White,
    background = IvoryWhite,
    onBackground = NightBlack,
    surface = CalmLilac,
    onSurface = NightBlack,
    error = DangerRed,
    onError = Color.White
)

// Dark theme (optional — still warm, not harsh)
private val DarkColorScheme = darkColorScheme(
    primary = SoftRose,
    onPrimary = NightBlack,
    secondary = DeepPlum,
    onSecondary = Color.White,
    background = NightBlack,
    onBackground = IvoryWhite,
    surface = SoftGray,
    onSurface = IvoryWhite,
    error = AlertOrange,
    onError = NightBlack
)

@Composable
fun SHEildTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true, // Optional: enable for Android 12+
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // default unless you want a custom font set
        content = content
    )
}
