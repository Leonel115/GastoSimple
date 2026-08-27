package com.app.gastosimple.features.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.app.gastosimple.core.data.prefs.AppLanguage
import com.app.gastosimple.core.data.prefs.AppPalette
import com.app.gastosimple.core.data.prefs.AppThemeMode
import com.app.gastosimple.core.data.prefs.UserPreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private class FakeDataStore(
        initialPreferences: Preferences = emptyPreferences()
    ) : DataStore<Preferences> {
        private var currentPreferences: Preferences = initialPreferences
        private val _flow = MutableStateFlow(initialPreferences)
        override val data: Flow<Preferences> = _flow

        override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
            val updated = transform(currentPreferences)
            currentPreferences = updated
            _flow.value = updated
            return updated
        }
    }

    private lateinit var fakeDataStore: FakeDataStore
    private lateinit var repository: UserPreferencesRepository
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeDataStore = FakeDataStore()
        repository = UserPreferencesRepository(fakeDataStore)
        viewModel = SettingsViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial userPreferences state emits defaults`() = runTest(testDispatcher) {
        val state = viewModel.userPreferences.value
        assertEquals(AppThemeMode.SYSTEM, state.themeMode)
        assertEquals(AppPalette.DEFAULT, state.palette)
        assertEquals(AppLanguage.SYSTEM, state.language)
    }

    @Test
    fun `onThemeModeSelected updates theme in repository and state flow`() = runTest(testDispatcher) {
        viewModel.onThemeModeSelected(AppThemeMode.DARK)
        advanceUntilIdle()

        val updatedPrefs = repository.userPreferencesFlow.first()
        assertEquals(AppThemeMode.DARK, updatedPrefs.themeMode)
    }

    @Test
    fun `onPaletteSelected updates palette in repository and state flow`() = runTest(testDispatcher) {
        viewModel.onPaletteSelected(AppPalette.OCEAN)
        advanceUntilIdle()

        val updatedPrefs = repository.userPreferencesFlow.first()
        assertEquals(AppPalette.OCEAN, updatedPrefs.palette)
    }

    @Test
    fun `onLanguageSelected updates language in repository and state flow`() = runTest(testDispatcher) {
        viewModel.onLanguageSelected(AppLanguage.SPANISH)
        advanceUntilIdle()

        val updatedPrefs = repository.userPreferencesFlow.first()
        assertEquals(AppLanguage.SPANISH, updatedPrefs.language)
    }
}
