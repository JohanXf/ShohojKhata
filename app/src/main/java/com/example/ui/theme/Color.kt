package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// The 2 Exact Colors From 1000084919.png
val AppBeige = Color(0xFFFDF8F3)      // Soft, light cream beige background canvas (highly readable and warm)
val AppCaramel = Color(0xFF76421E)    // Rich dark caramel for all buttons, text, and primary actions

// Material 3 Baseline Colors - Cleaned up to use only the 2 colors with maximum contrast
val Purple80 = AppBeige
val PurpleGrey80 = AppCaramel
val Pink80 = AppBeige

val Purple40 = AppCaramel
val PurpleGrey40 = AppBeige
val Pink40 = AppCaramel

// Sohoj Khata Custom Bookkeeping Color Codes
val ForestGreen = AppCaramel          // Main brand color
val MintGreenLight = Color(0xFFFDF5ED) // Soft cream-caramel card/container background tint
val DeepGold = Color(0xFFFFA500)      // Accent/Highlights warm gold

val KhataGreen = Color(0xFF2E7D32)    // High contrast green for paid states
val KhataGreenBg = Color(0xFFE8F5E9)  // Rich light green background for high contrast readability

val KhataRed = Color(0xFFC62828)      // High contrast red for dues states
val KhataRedBg = Color(0xFFFFEBEE)    // Soft light red container background for readability

val WarmBg = AppBeige                 // Cozy wallpaper background canvas
val NavyDark = AppCaramel             // Primary text headers and body text

// Additional aliases for other names used in files to maintain 100% compatibility
val DarkCaramelBrand = ForestGreen
val WarmBeigeTint = MintGreenLight
val PaidCaramel = KhataGreen
val PaidCaramelBg = KhataGreenBg
val DuesRed = KhataRed
val DuesRedBg = KhataRedBg
val WarmCanvasBg = WarmBg
val DeepEspressoText = NavyDark
