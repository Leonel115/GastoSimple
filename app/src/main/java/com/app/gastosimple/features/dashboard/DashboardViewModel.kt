package com.app.gastosimple.features.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.gastosimple.features.dashboard.domain.BudgetProgressUiState
import com.app.gastosimple.features.dashboard.domain.DashboardFilterMode
import com.app.gastosimple.features.dashboard.domain.DateRangeCalculator
import com.app.gastosimple.features.dashboard.domain.GetBudgetProgressUseCase
import com.app.gastosimple.features.expenses.ExpenseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

/**
 * ViewModel para el Dashboard (HU-06).
 * Gestiona de forma reactiva el estado de los filtros temporales (Mes, Anual, Total)
 * y el cálculo de métricas financieras del presupuesto.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModel(
    private val getBudgetProgressUseCase: GetBudgetProgressUseCase,
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private val currentDate = LocalDate.now()
    private val initialYear = currentDate.year
    private val initialMonth = currentDate.monthValue

    private val _selectedFilterMode = MutableStateFlow(DashboardFilterMode.MONTHLY)
    val selectedFilterMode: StateFlow<DashboardFilterMode> = _selectedFilterMode.asStateFlow()

    private val _selectedMonth = MutableStateFlow(initialMonth)
    val selectedMonth: StateFlow<Int> = _selectedMonth.asStateFlow()

    private val _selectedYear = MutableStateFlow(initialYear)
    val selectedYear: StateFlow<Int> = _selectedYear.asStateFlow()

    private val availableYearsFlow: Flow<List<Int>> = expenseRepository.getAllTransactionDates()
        .map { timestamps -> DateRangeCalculator.extractAvailableYears(timestamps) }

    val budgetProgress: StateFlow<BudgetProgressUiState> = combine(
        _selectedFilterMode,
        _selectedYear,
        _selectedMonth,
        availableYearsFlow
    ) { mode, year, month, years ->
        FilterState(mode, year, month, years)
    }.flatMapLatest { filter ->
        getBudgetProgressUseCase(filter.mode, filter.year, filter.month).map { state ->
            state.copy(availableYears = filter.years)
        }
    }.flowOn(Dispatchers.IO)
     .stateIn(
         scope = viewModelScope,
         started = SharingStarted.WhileSubscribed(5000),
         initialValue = BudgetProgressUiState(
             isEmpty = true,
             selectedFilterMode = DashboardFilterMode.MONTHLY,
             selectedMonth = initialMonth,
             selectedYear = initialYear,
             isPastPeriod = false
         )
     )

    /**
     * Actualiza el modo de filtrado temporal ([DashboardFilterMode.MONTHLY], [DashboardFilterMode.ANNUAL], [DashboardFilterMode.TOTAL]).
     */
    fun setFilterMode(mode: DashboardFilterMode) {
        _selectedFilterMode.value = mode
    }

    /**
     * Actualiza el mes seleccionado (1..12).
     */
    fun setMonth(month: Int) {
        require(month in 1..12) { "Mes inválido: $month (debe ser 1..12)" }
        _selectedMonth.value = month
    }

    /**
     * Actualiza el año seleccionado.
     */
    fun setYear(year: Int) {
        require(year in 2000..2100) { "Año inválido: $year" }
        _selectedYear.value = year
    }

    private data class FilterState(
        val mode: DashboardFilterMode,
        val year: Int,
        val month: Int,
        val years: List<Int>
    )
}

