package com.app.gastosimple

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.app.gastosimple.core.data.prefs.UserPreferences
import com.app.gastosimple.core.data.prefs.UserPreferencesRepository
import com.app.gastosimple.core.ui.AppLanguageManager
import com.app.gastosimple.core.ui.theme.GastoSimpleTheme
import com.app.gastosimple.navigation.NavGraph
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val prefs: UserPreferencesRepository = koinInject()
            val userPreferences by prefs.userPreferencesFlow.collectAsState(initial = UserPreferences())

            val baseContext = LocalContext.current
            val localizedContext = remember(userPreferences.language, baseContext) {
                AppLanguageManager.getLocalizedContext(baseContext, userPreferences.language)
            }
            val localizedConfig = remember(userPreferences.language, baseContext) {
                AppLanguageManager.getLocalizedConfiguration(baseContext, userPreferences.language)
            }

            CompositionLocalProvider(
                LocalContext provides localizedContext,
                LocalConfiguration provides localizedConfig
            ) {
                GastoSimpleTheme(themeMode = userPreferences.themeMode) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        NavGraph()
                    }
                }
            }
        }
    }
}
