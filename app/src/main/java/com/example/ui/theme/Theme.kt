package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val SisyaLightColorScheme = lightColorScheme(
    primary = PrimaryDarkTeal,
    onPrimary = SurfaceWhite,
    primaryContainer = PrimarySoftTeal,
    onPrimaryContainer = PrimaryDarkTeal,
    secondary = AccentTeal,
    onSecondary = SurfaceWhite,
    secondaryContainer = SecondarySurface,
    onSecondaryContainer = PrimaryText,
    tertiary = InfoBlue,
    onTertiary = SurfaceWhite,
    background = MainBackground,
    onBackground = PrimaryText,
    surface = SurfaceWhite,
    onSurface = PrimaryText,
    surfaceVariant = SecondarySurface,
    onSurfaceVariant = SecondaryText,
    outline = BorderColor,
    outlineVariant = BorderLight,
    error = ErrorRed,
    onError = SurfaceWhite,
    errorContainer = ErrorSoft,
    onErrorContainer = ErrorRed
)

@Composable
fun SisyaAbhyasaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Sisya Abhyasa visual theme is intentionally bright, calm, light-gray & turquoise/teal surfaces
    val colorScheme = SisyaLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = true
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
