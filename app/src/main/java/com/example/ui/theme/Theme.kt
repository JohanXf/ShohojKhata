package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext

private val LessBlack = Color(0xFF2C2C2C)
private val LittleGreyish = Color(0xFFCECECE)

private val LightColorScheme =
  lightColorScheme(
    primary = LessBlack,
    secondary = Color(0xFF666666),
    tertiary = LessBlack,
    background = LittleGreyish,
    surface = LessBlack,
    onBackground = LessBlack,
    onSurface = LittleGreyish,
    onPrimary = LittleGreyish
  )

@Composable
fun MyApplicationTheme(
  isBengali: Boolean = false,
  content: @Composable () -> Unit,
) {
  // Always use the customized rich caramel-earth LightColorScheme matching the global balance UI
  val colorScheme = LightColorScheme
  val typography = getAppTypography(isBengali)

  MaterialTheme(colorScheme = colorScheme, typography = typography, content = content)
}
