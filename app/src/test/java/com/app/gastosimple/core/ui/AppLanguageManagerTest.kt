package com.app.gastosimple.core.ui

import com.app.gastosimple.core.data.prefs.AppLanguage
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

class AppLanguageManagerTest {

    @Test
    fun `resolveLocale with SPANISH returns es locale`() {
        val locale = AppLanguageManager.resolveLocale(AppLanguage.SPANISH)
        assertEquals("es", locale.language)
    }

    @Test
    fun `resolveLocale with ENGLISH returns en locale`() {
        val locale = AppLanguageManager.resolveLocale(AppLanguage.ENGLISH)
        assertEquals("en", locale.language)
    }

    @Test
    fun `resolveLocale with SYSTEM returns provided system locale`() {
        val customSystemLocale = Locale.FRANCE
        val locale = AppLanguageManager.resolveLocale(AppLanguage.SYSTEM, systemLocale = customSystemLocale)
        assertEquals(customSystemLocale, locale)
    }
}
