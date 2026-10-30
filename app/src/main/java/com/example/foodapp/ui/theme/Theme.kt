package com.example.foodapp.ui.Theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val FastBiteRed = Color(0xFFDA291C)
val FastBiteYellow = Color(0xFFFFCC00)
val FastBiteDark = Color(0xFF1A1A1A)
val FastBiteGray = Color(0xFFF5F5F5)
val FastBiteTextGray = Color(0xFF666666)

private val LightColorScheme = lightColorScheme(
    primary = FastBiteRed,
    secondary = FastBiteYellow,
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onBackground = FastBiteDark,
    onSurface = FastBiteDark
)

@Composable
fun FastBiteTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography(),
        content = content
    )
}