package com.app.gastosimple.core.data.prefs

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.mutablePreferencesOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class UserPreferencesRepositoryTest {

    private class FakeDataStore(
        initialPreferences: Preferences = emptyPreferences()
    ) : DataStore<Preferences> {
        private var currentPreferences: Preferences = initialPreferences
        private val _flow = kotlinx.coroutines.flow.MutableStateFlow(initialPreferences)
        override val data: Flow<Preferences> = _flow

        override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
            val updated = transform(currentPreferences)
            currentPreferences = updated
            _flow.value = updated
            return updated
        }
    }

    @Test
    fun `userPreferencesFlow returns default values when preferences are empty`() = runTest {
        val fakeDataStore = FakeDataStore(emptyPreferences())
        val repository = UserPreferencesRepository(fakeDataStore)

        val prefs = repository.userPreferencesFlow.first()

        assertEquals(AppThemeMode.SYSTEM, prefs.themeMode)
        assertEquals(AppPalette.DEFAULT, prefs.palette)
        assertEquals(AppLanguage.SYSTEM, prefs.language)
    }

    @Test
    fun `userPreferencesFlow parses stored preferences correctly`() = runTest {
        val initialPreferences = mutablePreferencesOf(
            UserPreferencesRepository.THEME_MODE to AppThemeMode.DARK.name,
            UserPreferencesRepository.APP_PALETTE to AppPalette.OCEAN.name,
            UserPreferencesRepository.APP_LANGUAGE to AppLanguage.SPANISH.name
        )
        val fakeDataStore = FakeDataStore(initialPreferences)
        val repository = UserPreferencesRepository(fakeDataStore)

        val prefs = repository.userPreferencesFlow.first()

        assertEquals(AppThemeMode.DARK, prefs.themeMode)
        assertEquals(AppPalette.OCEAN, prefs.palette)
        assertEquals(AppLanguage.SPANISH, prefs.language)
    }

    @Test
    fun `userPreferencesFlow handles invalid preference strings by falling back to defaults`() = runTest {
        val corruptedPreferences = mutablePreferencesOf(
            UserPreferencesRepository.THEME_MODE to "UNKNOWN_THEME_VALUE",
            UserPreferencesRepository.APP_PALETTE to "INVALID_PALETTE",
            UserPreferencesRepository.APP_LANGUAGE to "NON_EXISTING_LANGUAGE"
        )
        val fakeDataStore = FakeDataStore(corruptedPreferences)
        val repository = UserPreferencesRepository(fakeDataStore)

        val prefs = repository.userPreferencesFlow.first()

        assertEquals(AppThemeMode.SYSTEM, prefs.themeMode)
        assertEquals(AppPalette.DEFAULT, prefs.palette)
        assertEquals(AppLanguage.SYSTEM, prefs.language)
    }

    @Test
    fun `setThemeMode updates theme preference correctly`() = runTest {
        val fakeDataStore = FakeDataStore(emptyPreferences())
        val repository = UserPreferencesRepository(fakeDataStore)

        repository.setThemeMode(AppThemeMode.LIGHT)

        val prefs = repository.userPreferencesFlow.first()
        assertEquals(AppThemeMode.LIGHT, prefs.themeMode)
    }

    @Test
    fun `setPalette updates palette preference correctly`() = runTest {
        val fakeDataStore = FakeDataStore(emptyPreferences())
        val repository = UserPreferencesRepository(fakeDataStore)

        repository.setPalette(AppPalette.FOREST)

        val prefs = repository.userPreferencesFlow.first()
        assertEquals(AppPalette.FOREST, prefs.palette)
    }

    @Test
    fun `setLanguage updates language preference correctly`() = runTest {
        val fakeDataStore = FakeDataStore(emptyPreferences())
        val repository = UserPreferencesRepository(fakeDataStore)

        repository.setLanguage(AppLanguage.ENGLISH)

        val prefs = repository.userPreferencesFlow.first()
        assertEquals(AppLanguage.ENGLISH, prefs.language)
    }

    @Test
    fun `isSetupComplete and plannedBudget work correctly`() = runTest {
        val fakeDataStore = FakeDataStore(emptyPreferences())
        val repository = UserPreferencesRepository(fakeDataStore)

        assertFalse(repository.isSetupComplete.first())
        assertNull(repository.plannedBudget.first())

        repository.setSetupComplete(true)
        repository.setPlannedBudget("1500.00")

        assertTrue(repository.isSetupComplete.first())
        assertEquals("1500.00", repository.plannedBudget.first())

        repository.setPlannedBudget(null)
        assertNull(repository.plannedBudget.first())
    }

    @Test
    fun `userPreferencesFlow handles IOException gracefully and emits defaults`() = runTest {
        val failingDataStore = object : DataStore<Preferences> {
            override val data: Flow<Preferences> = flow {
                throw IOException("Disk read failure")
            }
            override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
                throw IOException("Disk write failure")
            }
        }
        val repository = UserPreferencesRepository(failingDataStore)

        val prefs = repository.userPreferencesFlow.first()
        assertEquals(AppThemeMode.SYSTEM, prefs.themeMode)
        assertEquals(AppPalette.DEFAULT, prefs.palette)
        assertEquals(AppLanguage.SYSTEM, prefs.language)
    }
}
