package com.app.gastosimple.core.ui.theme

import com.app.gastosimple.core.data.prefs.AppThemeMode
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemeModeTest {

    @Test
    fun `resolveIsDarkTheme with SYSTEM delegates to isSystemDark`() {
        assertTrue(resolveIsDarkTheme(AppThemeMode.SYSTEM, isSystemDark = true))
        assertFalse(resolveIsDarkTheme(AppThemeMode.SYSTEM, isSystemDark = false))
    }

    @Test
    fun `resolveIsDarkTheme with LIGHT always returns false`() {
        assertFalse(resolveIsDarkTheme(AppThemeMode.LIGHT, isSystemDark = true))
        assertFalse(resolveIsDarkTheme(AppThemeMode.LIGHT, isSystemDark = false))
    }

    @Test
    fun `resolveIsDarkTheme with DARK always returns true`() {
        assertTrue(resolveIsDarkTheme(AppThemeMode.DARK, isSystemDark = true))
        assertTrue(resolveIsDarkTheme(AppThemeMode.DARK, isSystemDark = false))
    }
}
