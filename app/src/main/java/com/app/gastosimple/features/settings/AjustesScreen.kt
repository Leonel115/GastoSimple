package com.app.gastosimple.features.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.outlined.BrightnessAuto
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.gastosimple.R
import com.app.gastosimple.core.data.prefs.AppLanguage
import com.app.gastosimple.core.data.prefs.AppThemeMode
import com.app.gastosimple.core.data.prefs.UserPreferences
import com.app.gastosimple.core.ui.theme.GastoSimpleTheme
import org.koin.androidx.compose.koinViewModel

/**
 * Constantes dimensionales para la pantalla de Ajustes.
 */
private object SettingsDimens {
    val ScreenPadding = 16.dp
    val SectionSpacing = 20.dp
    val CardCornerRadius = 16.dp
    val RowVerticalPadding = 14.dp
    val RowHorizontalPadding = 16.dp
    val IconSize = 24.dp
    val SmallIconSize = 14.dp
    val SpaceBetweenIconAndText = 16.dp
    val DialogOptionPadding = 12.dp
    val Elevation = 2.dp
}

/**
 * Pantalla principal de Ajustes (HU-11).
 *
 * @param viewModel Instancia inyectada de [SettingsViewModel].
 */
@Composable
fun AjustesScreen(
    viewModel: SettingsViewModel = koinViewModel()
) {
    val preferences by viewModel.userPreferences.collectAsState()

    AjustesContent(
        preferences = preferences,
        onThemeModeSelected = viewModel::onThemeModeSelected,
        onLanguageSelected = viewModel::onLanguageSelected
    )
}

/**
 * Contenido sin estado (stateless) para la pantalla de Ajustes.
 *
 * @param preferences Estado actual de preferencias del usuario.
 * @param onThemeModeSelected Callback al seleccionar un modo de tema.
 * @param onLanguageSelected Callback al seleccionar un idioma.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AjustesContent(
    preferences: UserPreferences,
    onThemeModeSelected: (AppThemeMode) -> Unit,
    onLanguageSelected: (AppLanguage) -> Unit
) {
    var showThemeDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings_title),
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = SettingsDimens.ScreenPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(SettingsDimens.ScreenPadding))

            // SECCIÓN: Apariencia / Modo
            SettingsSectionTitle(title = stringResource(R.string.settings_section_appearance))
            Spacer(modifier = Modifier.height(SettingsDimens.DialogOptionPadding))

            Card(
                shape = RoundedCornerShape(SettingsDimens.CardCornerRadius),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = SettingsDimens.Elevation),
                modifier = Modifier.fillMaxWidth()
            ) {
                val themeModeLabel = getThemeModeLabel(preferences.themeMode)
                val themeIcon = getThemeModeIcon(preferences.themeMode)

                SettingsItemRow(
                    icon = themeIcon,
                    title = stringResource(R.string.settings_theme_mode),
                    currentValue = themeModeLabel,
                    onClick = { showThemeDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(SettingsDimens.SectionSpacing))

            // SECCIÓN: Idioma
            SettingsSectionTitle(title = stringResource(R.string.settings_section_language))
            Spacer(modifier = Modifier.height(SettingsDimens.DialogOptionPadding))

            Card(
                shape = RoundedCornerShape(SettingsDimens.CardCornerRadius),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = SettingsDimens.Elevation),
                modifier = Modifier.fillMaxWidth()
            ) {
                val languageLabel = getLanguageLabel(preferences.language)

                SettingsItemRow(
                    icon = Icons.Outlined.Language,
                    title = stringResource(R.string.settings_language_label),
                    currentValue = languageLabel,
                    onClick = { showLanguageDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(SettingsDimens.SectionSpacing))
        }
    }

    if (showThemeDialog) {
        ThemeModeSelectionDialog(
            currentThemeMode = preferences.themeMode,
            onThemeModeSelected = {
                onThemeModeSelected(it)
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false }
        )
    }

    if (showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguage = preferences.language,
            onLanguageSelected = {
                onLanguageSelected(it)
                showLanguageDialog = false
            },
            onDismiss = { showLanguageDialog = false }
        )
    }
}

/**
 * Encabezado modular de sección para agrupar configuraciones.
 */
@Composable
private fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.SemiBold
    )
}

/**
 * Fila interactiva de ajuste con icono, título, valor actual y flecha de navegación.
 */
@Composable
private fun SettingsItemRow(
    icon: ImageVector,
    title: String,
    currentValue: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(
                horizontal = SettingsDimens.RowHorizontalPadding,
                vertical = SettingsDimens.RowVerticalPadding
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(SettingsDimens.IconSize)
            )
            Spacer(modifier = Modifier.width(SettingsDimens.SpaceBetweenIconAndText))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = currentValue,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.size(SettingsDimens.SmallIconSize)
        )
    }
}

/**
 * Diálogo de selección para el modo de tema visual.
 */
@Composable
private fun ThemeModeSelectionDialog(
    currentThemeMode: AppThemeMode,
    onThemeModeSelected: (AppThemeMode) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.settings_theme_mode),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                AppThemeMode.entries.forEach { mode ->
                    val isSelected = mode == currentThemeMode
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = isSelected,
                                role = Role.RadioButton,
                                onClick = { onThemeModeSelected(mode) }
                            )
                            .padding(vertical = SettingsDimens.DialogOptionPadding),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = null
                        )
                        Spacer(modifier = Modifier.width(SettingsDimens.SpaceBetweenIconAndText))
                        Text(
                            text = getThemeModeLabel(mode),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

/**
 * Diálogo de selección para el idioma de la aplicación.
 */
@Composable
private fun LanguageSelectionDialog(
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.settings_language_label),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                AppLanguage.entries.forEach { lang ->
                    val isSelected = lang == currentLanguage
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = isSelected,
                                role = Role.RadioButton,
                                onClick = { onLanguageSelected(lang) }
                            )
                            .padding(vertical = SettingsDimens.DialogOptionPadding),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = null
                        )
                        Spacer(modifier = Modifier.width(SettingsDimens.SpaceBetweenIconAndText))
                        Text(
                            text = getLanguageLabel(lang),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

/**
 * Retorna la etiqueta localizada correspondiente al modo de tema.
 */
@Composable
private fun getThemeModeLabel(mode: AppThemeMode): String {
    return when (mode) {
        AppThemeMode.SYSTEM -> stringResource(R.string.settings_theme_mode_system)
        AppThemeMode.LIGHT -> stringResource(R.string.settings_theme_mode_light)
        AppThemeMode.DARK -> stringResource(R.string.settings_theme_mode_dark)
    }
}

/**
 * Retorna el icono apropiado según el modo de tema configurado.
 */
private fun getThemeModeIcon(mode: AppThemeMode): ImageVector {
    return when (mode) {
        AppThemeMode.SYSTEM -> Icons.Outlined.BrightnessAuto
        AppThemeMode.LIGHT -> Icons.Outlined.LightMode
        AppThemeMode.DARK -> Icons.Outlined.DarkMode
    }
}

/**
 * Retorna la etiqueta localizada correspondiente al idioma configurado.
 */
@Composable
private fun getLanguageLabel(language: AppLanguage): String {
    return when (language) {
        AppLanguage.SYSTEM -> stringResource(R.string.settings_language_system)
        AppLanguage.SPANISH -> stringResource(R.string.settings_language_spanish)
        AppLanguage.ENGLISH -> stringResource(R.string.settings_language_english)
    }
}

@Preview(name = "Ajustes - Modo Claro", showBackground = true)
@Composable
private fun AjustesContentLightPreview() {
    GastoSimpleTheme(darkTheme = false) {
        AjustesContent(
            preferences = UserPreferences(
                themeMode = AppThemeMode.SYSTEM,
                language = AppLanguage.SPANISH
            ),
            onThemeModeSelected = {},
            onLanguageSelected = {}
        )
    }
}

@Preview(name = "Ajustes - Modo Oscuro", showBackground = true)
@Composable
private fun AjustesContentDarkPreview() {
    GastoSimpleTheme(darkTheme = true) {
        AjustesContent(
            preferences = UserPreferences(
                themeMode = AppThemeMode.DARK,
                language = AppLanguage.ENGLISH
            ),
            onThemeModeSelected = {},
            onLanguageSelected = {}
        )
    }
}
