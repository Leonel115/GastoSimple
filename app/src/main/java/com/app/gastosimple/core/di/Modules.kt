package com.app.gastosimple.core.di

import androidx.room.Room
import com.app.gastosimple.core.data.local.GastoSimpleDatabase
import com.app.gastosimple.core.data.prefs.UserPreferencesRepository
import com.app.gastosimple.features.expenses.ExpenseRepository
import com.app.gastosimple.features.setup.SetupViewModel
import com.app.gastosimple.features.expenses.ExpenseViewModel
import com.app.gastosimple.features.calendar.CalendarViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            GastoSimpleDatabase::class.java,
            "gastosimple_db"
        ).fallbackToDestructiveMigration()
            .build()
    }
    single { get<GastoSimpleDatabase>().dao() }
}

val appModule = module {
    single { UserPreferencesRepository(androidContext()) }
    single { ExpenseRepository(get()) }
    
    viewModel { SetupViewModel(get(), get()) }
    viewModel { ExpenseViewModel(get(), get()) }
    viewModel { CalendarViewModel(get()) }
}
