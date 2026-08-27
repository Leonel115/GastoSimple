package com.app.gastosimple.core.data.prefs

/**
 * Modos de tema visual soportados por la aplicación.
 */
enum class AppThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

/**
 * Paletas de color disponibles en la aplicación.
 * DEFAULT corresponde a la paleta actual de marca.
 * OCEAN y FOREST están reservadas para futuras personalizaciones temáticas.
 */
enum class AppPalette {
    DEFAULT,
    OCEAN,
    FOREST
}

/**
 * Idiomas soportados por la aplicación.
 */
enum class AppLanguage {
    SYSTEM,
    SPANISH,
    ENGLISH
}

/**
 * Representa el modelo inmutable de preferencias generales del usuario.
 *
 * @property themeMode Modo del tema visual (Sistema, Claro, Oscuro).
 * @property palette Paleta cromática seleccionada.
 * @property language Idioma de la interfaz de usuario.
 */
data class UserPreferences(
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val palette: AppPalette = AppPalette.DEFAULT,
    val language: AppLanguage = AppLanguage.SYSTEM
)
