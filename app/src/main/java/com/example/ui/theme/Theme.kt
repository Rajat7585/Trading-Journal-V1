package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val ProfessionalDarkColorScheme = darkColorScheme(
    primary = AccentPrimary,
    onPrimary = TextOnAccent,
    primaryContainer = AccentContainer,
    onPrimaryContainer = TextOnAccent,
    secondary = AccentPrimary,
    onSecondary = DarkBackground,
    background = DarkBackground,
    onBackground = TextMain,
    surface = DarkSurface,
    onSurface = TextMain,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextMuted,
    outline = DarkBorder,
    outlineVariant = DarkSubtleBorder,
    error = LossRed,
    onError = DarkBackground,
    errorContainer = LossRedContainer,
    onErrorContainer = LossRed
)

@Composable
fun TradeArchiveTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = DarkBackground.toArgb()
                window.navigationBarColor = DarkBackground.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = ProfessionalDarkColorScheme,
        typography = Typography,
        content = content
    )
}
