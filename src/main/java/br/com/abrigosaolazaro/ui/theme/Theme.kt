package br.com.abrigosaolazaro.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary            = OrangePrimary,
    onPrimary          = Color.White,
    primaryContainer   = Orange90,
    onPrimaryContainer = Orange10,
    secondary          = Brown40,
    onSecondary        = Color.White,
    secondaryContainer = Brown90,
    onSecondaryContainer = Brown10,
    background         = Orange99,
    onBackground       = Orange10,
    surface            = Color.White,
    onSurface          = Orange10,
    surfaceVariant     = Orange90,
    onSurfaceVariant   = Orange30,
    error              = ErrorRed,
    onError            = Color.White
)

private val DarkColors = darkColorScheme(
    primary            = Orange80,
    onPrimary          = Orange20,
    primaryContainer   = Orange30,
    onPrimaryContainer = Orange90,
    secondary          = Brown80,
    onSecondary        = Brown20,
    secondaryContainer = Brown40,
    onSecondaryContainer = Brown90,
    background         = Color(0xFF1A1209),
    onBackground       = Orange90,
    surface            = Color(0xFF221A0F),
    onSurface          = Orange90,
    surfaceVariant     = Color(0xFF3A2D1F),
    onSurfaceVariant   = Brown80
)

@Composable
fun AbrigoSaoLazaroTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography  = AbrigoTypography,
        content     = content
    )
}
