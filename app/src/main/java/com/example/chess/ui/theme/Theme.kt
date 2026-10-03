package com.example.chess.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = GoldAccent,
    background = AppBackground,
    surface = CardBackground,
    onPrimary = AppBackground,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun ChessTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
