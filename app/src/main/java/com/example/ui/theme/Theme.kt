package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ElectricVioletLight,
    onPrimary = Color(0xFF1E1035),
    primaryContainer = Color(0xFF3B1E6D),
    onPrimaryContainer = Color(0xFFE9D8FD),
    secondary = AcousticCyanLight,
    onSecondary = Color(0xFF00363D),
    secondaryContainer = Color(0xFF004F58),
    onSecondaryContainer = Color(0xFFA5F3FC),
    tertiary = StudioAmber,
    onTertiary = Color(0xFF3E2800),
    tertiaryContainer = Color(0xFF5D3F00),
    onTertiaryContainer = Color(0xFFFED7AA),
    background = StudioBackground,
    onBackground = TextPrimary,
    surface = StudioSurface,
    onSurface = TextPrimary,
    surfaceVariant = StudioSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = StudioBorder,
    error = StudioCoral,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
