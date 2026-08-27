package com.app.gastosimple.core.ui

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.app.gastosimple.core.data.prefs.AppLanguage
import java.util.Locale

/**
 * Gestor utilitario para la configuración y aplicación dinámica del Locale/Idioma en la aplicación.
 */
object AppLanguageManager {

    private const val LANGUAGE_TAG_SPANISH = "es"
    private const val LANGUAGE_TAG_ENGLISH = "en"

    /**
     * Resuelve el objeto [Locale] correspondiente a la preferencia de idioma seleccionada.
     *
     * @param language Idioma configurado ([AppLanguage]).
     * @param systemLocale Locale predeterminado del sistema operativo.
     * @return [Locale] resultante.
     */
    fun resolveLocale(language: AppLanguage, systemLocale: Locale = Locale.getDefault()): Locale {
        return when (language) {
            AppLanguage.SYSTEM -> systemLocale
            AppLanguage.SPANISH -> Locale(LANGUAGE_TAG_SPANISH)
            AppLanguage.ENGLISH -> Locale(LANGUAGE_TAG_ENGLISH)
        }
    }

    /**
     * Aplica el idioma seleccionado al contexto de la aplicación usando AppCompatDelegate de manera segura.
     *
     * @param language Idioma de destino ([AppLanguage]).
     */
    fun applyLanguage(language: AppLanguage) {
        runCatching {
            val localeList = when (language) {
                AppLanguage.SYSTEM -> LocaleListCompat.getEmptyLocaleList()
                AppLanguage.SPANISH -> LocaleListCompat.forLanguageTags(LANGUAGE_TAG_SPANISH)
                AppLanguage.ENGLISH -> LocaleListCompat.forLanguageTags(LANGUAGE_TAG_ENGLISH)
            }
            AppCompatDelegate.setApplicationLocales(localeList)
        }
    }

    /**
     * Crea una copia del Context con la configuración regional aplicada para Jetpack Compose.
     *
     * @param context Contexto base.
     * @param language Idioma a aplicar.
     * @return Contexto configurado con el nuevo Locale.
     */
    fun getLocalizedContext(context: Context, language: AppLanguage): Context {
        val targetLocale = resolveLocale(language)
        Locale.setDefault(targetLocale)
        val config = Configuration(context.resources.configuration).apply {
            setLocale(targetLocale)
            setLayoutDirection(targetLocale)
        }
        return context.createConfigurationContext(config)
    }

    /**
     * Crea un objeto Configuration con la configuración regional aplicada.
     *
     * @param context Contexto base.
     * @param language Idioma a aplicar.
     * @return Configuración regionalizada.
     */
    fun getLocalizedConfiguration(context: Context, language: AppLanguage): Configuration {
        val targetLocale = resolveLocale(language)
        return Configuration(context.resources.configuration).apply {
            setLocale(targetLocale)
            setLayoutDirection(targetLocale)
        }
    }
}
