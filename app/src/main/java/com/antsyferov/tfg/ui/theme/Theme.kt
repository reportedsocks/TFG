package com.antsyferov.tfg.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = PurpleHeart,
    primaryVariant = DaisyBush,
    onPrimary = White,
    secondary = DodgerBlue,
    secondaryVariant = Prelude,
    onSecondary = MineShaft
)

private val LightColorPalette = lightColors(
    primary = Cerulean,
    primaryVariant = LochMara,
    onPrimary = White,
    secondary = PurpleHeart,
    onSecondary = White,
    background = Selago
)

@Composable
fun TFGTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}