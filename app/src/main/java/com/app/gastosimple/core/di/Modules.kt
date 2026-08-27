package com.app.gastosimple.core.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import com.app.gastosimple.core.data.local.GastoSimpleDatabase
import com.app.gastosimple.core.data.prefs.UserPreferencesRepository
import com.app.gastosimple.core.data.prefs.dataStore
import com.app.gastosimple.features.expenses.ExpenseRepository
import com.app.gastosimple.features.setup.SetupViewModel
import com.app.gastosimple.features.expenses.ExpenseViewModel
import com.app.gastosimple.features.calendar.CalendarViewModel
import com.app.gastosimple.features.dashboard.DashboardViewModel
import com.app.gastosimple.features.dashboard.domain.GetBudgetProgressUseCase
import com.app.gastosimple.features.settings.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            GastoSimpleDatabase::class.java,
            "gastosimple_db"
        ).fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }
    single { get<GastoSimpleDatabase>().dao() }
}

val appModule = module {
    single<DataStore<Preferences>> { androidContext().dataStore }
    single { UserPreferencesRepository(get<DataStore<Preferences>>()) }
    single { ExpenseRepository(get()) }
    
    // Domain / UseCases
    factory { GetBudgetProgressUseCase(get()) }
    
    viewModel { SetupViewModel(get(), get()) }
    viewModel { ExpenseViewModel(get(), get(), get()) }
    viewModel { CalendarViewModel(get()) }
    viewModel { DashboardViewModel(get(), get()) }
    viewModel { SettingsViewModel(get()) }
}

