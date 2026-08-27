package com.app.gastosimple.core.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

/**
 * Repositorio encargado de gestionar la persistencia y lectura de las preferencias de usuario
 * mediante Jetpack DataStore (Preferences DataStore).
 */
class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {

    /**
     * Constructor secundario que obtiene el DataStore a partir del contexto de Android.
     */
    constructor(context: Context) : this(context.dataStore)

    companion object {
        val IS_SETUP_COMPLETE = booleanPreferencesKey("is_setup_complete")
        val PLANNED_BUDGET = stringPreferencesKey("planned_budget")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val APP_PALETTE = stringPreferencesKey("app_palette")
        val APP_LANGUAGE = stringPreferencesKey("app_language")
    }

    /**
     * Flujo reactivo con las preferencias generales del usuario.
     * Implementa manejo defensivo ante errores de lectura de I/O emitiendo preferencias vacías por defecto.
     */
    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            mapPreferencesToUserPreferences(preferences)
        }

    /**
     * Flujo que indica si el proceso de onboarding inicial ha concluido.
     */
    val isSetupComplete: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[IS_SETUP_COMPLETE] ?: false
        }

    /**
     * Flujo del presupuesto planificado guardado.
     */
    val plannedBudget: Flow<String?> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PLANNED_BUDGET]
        }

    /**
     * Actualiza el modo del tema en las preferencias del usuario.
     *
     * @param themeMode Modo de tema seleccionado ([AppThemeMode]).
     */
    suspend fun setThemeMode(themeMode: AppThemeMode) {
        dataStore.edit { preferences ->
            preferences[THEME_MODE] = themeMode.name
        }
    }

    /**
     * Actualiza la paleta cromática en las preferencias del usuario.
     *
     * @param palette Paleta seleccionada ([AppPalette]).
     */
    suspend fun setPalette(palette: AppPalette) {
        dataStore.edit { preferences ->
            preferences[APP_PALETTE] = palette.name
        }
    }

    /**
     * Actualiza el idioma de la aplicación en las preferencias del usuario.
     *
     * @param language Idioma seleccionado ([AppLanguage]).
     */
    suspend fun setLanguage(language: AppLanguage) {
        dataStore.edit { preferences ->
            preferences[APP_LANGUAGE] = language.name
        }
    }

    /**
     * Marca el estado de finalización del setup inicial.
     *
     * @param complete true si el setup fue completado, false en caso contrario.
     */
    suspend fun setSetupComplete(complete: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_SETUP_COMPLETE] = complete
        }
    }

    /**
     * Establece o elimina el monto de presupuesto planificado.
     *
     * @param amount Monto a persistir, o null para eliminar la preferencia.
     */
    suspend fun setPlannedBudget(amount: String?) {
        dataStore.edit { preferences ->
            if (amount == null) {
                preferences.remove(PLANNED_BUDGET)
            } else {
                preferences[PLANNED_BUDGET] = amount
            }
        }
    }

    /**
     * Transforma el conjunto de claves en la entidad de dominio [UserPreferences].
     */
    private fun mapPreferencesToUserPreferences(preferences: Preferences): UserPreferences {
        val themeModeStr = preferences[THEME_MODE]
        val paletteStr = preferences[APP_PALETTE]
        val languageStr = preferences[APP_LANGUAGE]

        return UserPreferences(
            themeMode = parseThemeMode(themeModeStr),
            palette = parseAppPalette(paletteStr),
            language = parseAppLanguage(languageStr)
        )
    }

    /**
     * Parsea de manera segura el string del modo de tema a [AppThemeMode].
     */
    private fun parseThemeMode(value: String?): AppThemeMode {
        if (value == null) return AppThemeMode.SYSTEM
        return AppThemeMode.entries.find { it.name.equals(value, ignoreCase = true) } ?: AppThemeMode.SYSTEM
    }

    /**
     * Parsea de manera segura el string de la paleta a [AppPalette].
     */
    private fun parseAppPalette(value: String?): AppPalette {
        if (value == null) return AppPalette.DEFAULT
        return AppPalette.entries.find { it.name.equals(value, ignoreCase = true) } ?: AppPalette.DEFAULT
    }

    /**
     * Parsea de manera segura el string del idioma a [AppLanguage].
     */
    private fun parseAppLanguage(value: String?): AppLanguage {
        if (value == null) return AppLanguage.SYSTEM
        return AppLanguage.entries.find { it.name.equals(value, ignoreCase = true) } ?: AppLanguage.SYSTEM
    }
}
