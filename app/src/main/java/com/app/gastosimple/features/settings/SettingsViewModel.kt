package com.app.gastosimple.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.gastosimple.core.data.prefs.AppLanguage
import com.app.gastosimple.core.data.prefs.AppPalette
import com.app.gastosimple.core.data.prefs.AppThemeMode
import com.app.gastosimple.core.data.prefs.UserPreferences
import com.app.gastosimple.core.data.prefs.UserPreferencesRepository
import com.app.gastosimple.core.ui.AppLanguageManager

import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel responsable de la gestión y sincronización de las preferencias del usuario (HU-11).
 *
 * @param repository Repositorio de preferencias [UserPreferencesRepository].
 */
class SettingsViewModel(
    private val repository: UserPreferencesRepository
) : ViewModel() {

    /**
     * Estado observable de las preferencias de usuario.
     * Mantiene un StateFlow activo mientras existan suscriptores en la UI.
     */
    val userPreferences: StateFlow<UserPreferences> = repository.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UserPreferences()
        )

    /**
     * Actualiza el modo del tema visual seleccionado por el usuario.
     *
     * @param themeMode Modo de tema elegido ([AppThemeMode]).
     */
    fun onThemeModeSelected(themeMode: AppThemeMode) {
        viewModelScope.launch {
            repository.setThemeMode(themeMode)
        }
    }

    /**
     * Actualiza la paleta cromática seleccionada por el usuario.
     *
     * @param palette Paleta elegida ([AppPalette]).
     */
    fun onPaletteSelected(palette: AppPalette) {
        viewModelScope.launch {
            repository.setPalette(palette)
        }
    }

    /**
     * Actualiza el idioma de la aplicación seleccionado por el usuario.
     *
     * @param language Idioma elegido ([AppLanguage]).
     */
    fun onLanguageSelected(language: AppLanguage) {
        AppLanguageManager.applyLanguage(language)
        viewModelScope.launch {
            repository.setLanguage(language)
        }
    }
}

