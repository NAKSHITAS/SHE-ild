package com.example.she_ild.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(12.dp),       // Chips, buttons
    medium = RoundedCornerShape(16.dp),      // Cards, modals
    large = RoundedCornerShape(24.dp),       // Containers, images
    extraLarge = RoundedCornerShape(32.dp)   // Splash screens or full-width elements
)
