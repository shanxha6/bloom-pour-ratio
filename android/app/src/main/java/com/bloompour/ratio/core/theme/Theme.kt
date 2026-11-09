package com.bloompour.ratio.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkScheme = darkColorScheme(
    primary = Caramel,
    onPrimary = RoastDeep,
    primaryContainer = CeramicSurface,
    onPrimaryContainer = TextPrimary,
    secondary = Mint,
    onSecondary = RoastDeep,
    secondaryContainer = CeramicSurfaceAlt,
    onSecondaryContainer = Mint,
    tertiary = CaramelLight,
    onTertiary = RoastDeep,
    background = RoastBackground,
    onBackground = TextPrimary,
    surface = CeramicSurface,
    onSurface = TextPrimary,
    surfaceVariant = CeramicSurfaceAlt,
    onSurfaceVariant = TextSecondary,
    outline = TextMuted,
    error = EmberWarn,
    onError = RoastDeep
)

private val LightScheme = lightColorScheme(
    primary = Caramel,
    onPrimary = PaperBackground,
    primaryContainer = PaperSurface,
    onPrimaryContainer = PaperText,
    secondary = Mint,
    onSecondary = PaperText,
    secondaryContainer = PaperSurfaceAlt,
    onSecondaryContainer = PaperText,
    tertiary = CaramelLight,
    onTertiary = PaperText,
    background = PaperBackground,
    onBackground = PaperText,
    surface = PaperSurface,
    onSurface = PaperText,
    surfaceVariant = PaperSurfaceAlt,
    onSurfaceVariant = PaperTextMuted,
    outline = PaperTextMuted,
    error = EmberWarn,
    onError = PaperBackground
)

@Composable
fun AppTheme(
    useLightTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (useLightTheme) LightScheme else DarkScheme,
        typography = AppTypography,
        content = content
    )
}
