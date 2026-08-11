package com.app.gastosimple

import android.app.Application
import com.app.gastosimple.core.di.appModule
import com.app.gastosimple.core.di.databaseModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

import org.koin.core.logger.Level

class GastoSimpleApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            // Desactivamos el logger de Android que puede causar crashes en el arranque
            // androidLogger(Level.ERROR) 
            androidContext(this@GastoSimpleApp)
            modules(appModule, databaseModule)
        }
    }
}
