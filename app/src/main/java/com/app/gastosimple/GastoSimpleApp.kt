package com.app.gastosimple

import android.app.Application
import com.app.gastosimple.core.di.appModule
import com.app.gastosimple.core.di.databaseModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class GastoSimpleApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@GastoSimpleApp)
            modules(appModule, databaseModule)
        }
    }
}
