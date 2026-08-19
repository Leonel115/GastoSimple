package com.app.gastosimple.core.di

import androidx.room.Room
import com.app.gastosimple.core.data.local.GastoSimpleDatabase
import com.app.gastosimple.core.data.prefs.UserPreferencesRepository
import com.app.gastosimple.features.expenses.ExpenseRepository
import com.app.gastosimple.features.setup.SetupViewModel
import com.app.gastosimple.features.expenses.ExpenseViewModel
import com.app.gastosimple.features.calendar.CalendarViewModel
import com.app.gastosimple.features.installments.CalculateInstallmentQuotaUseCase
import com.app.gastosimple.features.installments.GetActiveBalancesUseCase
import com.app.gastosimple.features.installments.InstallmentViewModel
import com.app.gastosimple.features.installments.ProcessPaymentAndCloseUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            GastoSimpleDatabase::class.java,
            "gastosimple_db"
        ).addMigrations(GastoSimpleDatabase.MIGRATION_2_3)
            .build()
    }
    single { get<GastoSimpleDatabase>().dao() }
}

val appModule = module {
    single { UserPreferencesRepository(androidContext()) }
    single { ExpenseRepository(get()) }
    
    // Casos de Uso - Épica 5
    factory { CalculateInstallmentQuotaUseCase() }
    factory { GetActiveBalancesUseCase(get()) }
    factory { ProcessPaymentAndCloseUseCase(get()) }
    
    viewModel { SetupViewModel(get(), get()) }
    viewModel { ExpenseViewModel(get(), get(), get()) }
    viewModel { CalendarViewModel(get()) }
    viewModel { InstallmentViewModel(get(), get()) }
}
