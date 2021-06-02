package com.hinnka.tsbrowser.ui.theme

import android.annotation.SuppressLint
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.hinnka.tsbrowser.App
import com.hinnka.tsbrowser.ext.logD

@SuppressLint("ConflictingOnColor")
private val SecretColorPalette = darkColors(
    primary = Teal200,
    primaryVariant = Color.Black,
    secondary = Teal200,
    onPrimary = Color.White,
    surface = Color.Black,
    onSurface = Color.White,
)

private val LightColorPalette = lightColors(
    primary = Teal200,
    primaryVariant = Teal700,
    secondary = Teal200,
    surface = Color.White,
)

val Colors.primaryLight: Color get() = if (isLight) PrimaryWhite else PrimaryDark


@Composable
fun TSBrowserTheme(content: @Composable() () -> Unit) {
    logD("TSBrowserTheme start")
    val colors = if (App.isSecretMode) {
        SecretColorPalette
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