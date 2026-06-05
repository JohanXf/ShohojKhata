package com.example.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme

// Dynamic background/text getters linked to the Black and White theme
val WarmBg: Color
    @Composable get() = MaterialTheme.colorScheme.background

val ForestGreen: Color
    @Composable get() = MaterialTheme.colorScheme.primary

val NavyDark: Color
    @Composable get() = MaterialTheme.colorScheme.onBackground

val MintGreenLight: Color
    @Composable get() = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)

val DeepGold = Color(0xFFFFA500)      // Accent/Highlights warm gold

// High contrast Black and White styled states or fallback to modern readable indicators
val KhataGreen = Color(0xFF2E7D32)    // High contrast green for paid states
val KhataGreenBg: Color
    @Composable get() = Color(0xFFE8F5E9)

val KhataRed = Color(0xFFC62828)      // High contrast red for dues states
val KhataRedBg: Color
    @Composable get() = Color(0xFFFFEBEE)

// Compatibility aliases - all fully dynamic mapping to material color scheme
val AppBeige = Color(0xFFFDF8F3)
val AppCaramel = Color(0xFF76421E)

val Purple80 = Color.White
val PurpleGrey80 = Color.LightGray
val Pink80 = Color.White

val Purple40 = Color.Black
val PurpleGrey40 = Color.DarkGray
val Pink40 = Color.Black

val DarkCaramelBrand: Color
    @Composable get() = ForestGreen

val WarmBeigeTint: Color
    @Composable get() = MintGreenLight

val PaidCaramel: Color
    @Composable get() = KhataGreen

val PaidCaramelBg: Color
    @Composable get() = KhataGreenBg

val DuesRed: Color
    @Composable get() = KhataRed

val DuesRedBg: Color
    @Composable get() = KhataRedBg

val WarmCanvasBg: Color
    @Composable get() = WarmBg

val DeepEspressoText: Color
    @Composable get() = NavyDark
