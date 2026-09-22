package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val BasokaDarkColorScheme = darkColorScheme(
    primary = NeonElectricBlue,
    onPrimary = AlmostBlackBg,
    primaryContainer = SurfaceElevated,
    onPrimaryContainer = NeonElectricBlue,
    secondary = NeonYellow,
    onSecondary = AlmostBlackBg,
    secondaryContainer = DeepNavy,
    onSecondaryContainer = NeonYellow,
    tertiary = PurpleAccent,
    background = AlmostBlackBg,
    onBackground = TextPrimary,
    surface = SurfaceNavy,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle,
    error = ErrorRed,
    onError = TextPrimary
)

private val BasokaLightColorScheme = lightColorScheme(
    primary = Color(0xFF007A99),
    onPrimary = TextPrimary,
    primaryContainer = Color(0xFFD6F5FF),
    onPrimaryContainer = Color(0xFF004455),
    secondary = Color(0xFF8A7B00),
    onSecondary = TextPrimary,
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    outline = Color(0xFFCBD5E1)
)

@Composable
fun BasokaTheme(
    darkTheme: Boolean = true, // Default to Dark futuristic navy per requirements
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) BasokaDarkColorScheme else BasokaLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.let {
                it.statusBarColor = AlmostBlackBg.toArgb()
                it.navigationBarColor = AlmostBlackBg.toArgb()
                WindowCompat.getInsetsController(it, view).isAppearanceLightStatusBars = !darkTheme
                WindowCompat.getInsetsController(it, view).isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    BasokaTheme(darkTheme = darkTheme, content = content)
}
