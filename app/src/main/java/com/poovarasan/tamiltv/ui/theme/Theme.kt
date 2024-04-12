package com.poovarasan.tamiltv.ui.theme


import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightThemeColors = lightColors(
    primary = Red700,
    primaryVariant = Red900,
    secondary = Red700,
    secondaryVariant = Red900,
    error = Red800,
)

private val DarkThemeColors = darkColors(
    primary = Red300,
    primaryVariant = Red700,
    secondary = Red300,
    error = Red200,
    onBackground = Color.Black,
    onSurface = Color.Black
)


val AppShape = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(4.dp),
    large = RoundedCornerShape(8.dp)
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (darkTheme) DarkThemeColors else LightThemeColors,
        typography = AppFont,
        shapes = AppShape,
        content = content
    )
}