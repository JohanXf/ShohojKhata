package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LessBlack = Color(0xFF222222)
private val LittleGreyish = Color(0xFFF3F4F6)

private val LightColorScheme =
  lightColorScheme(
    primary = LessBlack,
    secondary = Color(0xFF666666),
    tertiary = LessBlack,
    background = LittleGreyish,
    surface = Color.White,
    onBackground = LessBlack,
    onSurface = LessBlack,
    onPrimary = LittleGreyish
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = false,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  // Always use the customized LightColorScheme since dark mode is removed
  val colorScheme = LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
