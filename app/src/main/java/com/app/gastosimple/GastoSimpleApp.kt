package com.app.gastosimple

import android.app.Application
import com.app.gastosimple.core.di.appModule
import com.app.gastosimple.core.di.databaseModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

import com.app.gastosimple.core.data.debug.MockDataSeeder
import com.app.gastosimple.core.data.local.GastoSimpleDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get

class GastoSimpleApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            // Desactivamos el logger de Android que puede causar crashes en el arranque
            // androidLogger(Level.ERROR) 
            androidContext(this@GastoSimpleApp)
            modules(appModule, databaseModule)
        }

        // Sembrado modular de datos mock en segundo plano (solo si la BD está vacía)
        CoroutineScope(Dispatchers.IO).launch {
            val dao: GastoSimpleDao = get()
            MockDataSeeder.seedDatabaseIfNeeded(dao)
        }
    }
}

