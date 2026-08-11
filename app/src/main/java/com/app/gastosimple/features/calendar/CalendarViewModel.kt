package com.app.gastosimple.features.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.gastosimple.core.data.local.BudgetPeriodEntity
import com.app.gastosimple.core.data.local.ExpenseEntity
import com.app.gastosimple.core.data.local.GastoSimpleDao
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

data class CalendarUiState(
    val recurringExpenses: List<ExpenseEntity> = emptyList(),
    val activePeriod: BudgetPeriodEntity? = null,
    val displayedDate: Calendar = Calendar.getInstance(),
    val isLoading: Boolean = true
)

class CalendarViewModel(private val dao: GastoSimpleDao) : ViewModel() {
    
    private val _displayedDate = MutableStateFlow(Calendar.getInstance())
    
    val state: StateFlow<CalendarUiState> = combine(
        dao.getRecurringExpenses(),
        dao.getActivePeriod(),
        _displayedDate
    ) { expenses, period, displayedDate ->
        CalendarUiState(
            recurringExpenses = expenses,
            activePeriod = period,
            displayedDate = displayedDate,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CalendarUiState()
    )

    fun nextMonth() {
        val newDate = (state.value.displayedDate.clone() as Calendar).apply {
            add(Calendar.MONTH, 1)
        }
        _displayedDate.value = newDate
    }

    fun previousMonth() {
        val newDate = (state.value.displayedDate.clone() as Calendar).apply {
            add(Calendar.MONTH, -1)
        }
        _displayedDate.value = newDate
    }
}
