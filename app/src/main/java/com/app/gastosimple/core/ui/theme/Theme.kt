package com.app.gastosimple.core.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

import com.app.gastosimple.core.data.prefs.AppThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = CoolBlue,
    onPrimary = OffWhite,
    primaryContainer = DeepCoolBlue,
    onPrimaryContainer = PureWhite,
    secondary = CyanBlue,
    onSecondary = MidnightBlue,
    background = MidnightBlue,
    onBackground = OffWhite,
    surface = MidnightBlue,
    onSurface = OffWhite,
    surfaceVariant = DarkGray,
    onSurfaceVariant = LightGray,
    error = ErrorRed
)

private val LightColorScheme = lightColorScheme(
    primary = CoolBlue,
    onPrimary = PureWhite,
    primaryContainer = LightCoolBlue,
    onPrimaryContainer = DeepCoolBlue,
    secondary = DeepCoolBlue,
    onSecondary = PureWhite,
    background = PureWhite,
    onBackground = DarkGray,
    surface = OffWhite,
    onSurface = DarkGray,
    surfaceVariant = LightCoolBlue,
    onSurfaceVariant = DeepCoolBlue,
    error = ErrorRed
)


/**
 * Resuelve si se debe usar el tema oscuro en función del modo seleccionado y el estado del sistema.
 */
fun resolveIsDarkTheme(
    themeMode: AppThemeMode,
    isSystemDark: Boolean
): Boolean {
    return when (themeMode) {
        AppThemeMode.SYSTEM -> isSystemDark
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }
}

@Composable
fun GastoSimpleTheme(
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    darkTheme: Boolean = resolveIsDarkTheme(themeMode, isSystemInDarkTheme()),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disabled for a more consistent brand identity
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val context = view.context
            if (context is Activity) {
                val window = context.window
                window.statusBarColor = colorScheme.primary.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
