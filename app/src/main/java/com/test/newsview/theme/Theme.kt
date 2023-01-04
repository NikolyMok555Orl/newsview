package com.test.newsview.theme

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle



@SuppressLint("ConflictingOnColor")
private val LightColorPalette = lightColors(
    primary = colorPrimary,
    primaryVariant = primaryVariant,
    secondary=secondary,
    secondaryVariant=secondaryVariant,
    background = background,
    onSecondary=onSecondary,
    onPrimary = onPrimary,
    onBackground = onBackground,
    )

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = LightColorPalette


    val appTypography=AppTypography()
    MaterialTheme(
        colors = colors,
        typography = appTypography.AppTypography,
        content = content
    )
}