package com.app.gastosimple.core.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferencesRepository(private val context: Context) {

    companion object {
        val IS_SETUP_COMPLETE = booleanPreferencesKey("is_setup_complete")
        val PLANNED_BUDGET = stringPreferencesKey("planned_budget")
    }

    val isSetupComplete: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[IS_SETUP_COMPLETE] ?: false
        }

    val plannedBudget: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[PLANNED_BUDGET]
        }

    suspend fun setSetupComplete(complete: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_SETUP_COMPLETE] = complete
        }
    }

    suspend fun setPlannedBudget(amount: String?) {
        context.dataStore.edit { preferences ->
            if (amount == null) {
                preferences.remove(PLANNED_BUDGET)
            } else {
                preferences[PLANNED_BUDGET] = amount
            }
        }
    }
}
