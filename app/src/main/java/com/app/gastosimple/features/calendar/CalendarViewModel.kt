package com.app.gastosimple.features.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.gastosimple.core.data.local.BudgetPeriodEntity
import com.app.gastosimple.core.data.local.ExpenseEntity
import com.app.gastosimple.core.data.local.GastoSimpleDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Calendar

data class CalendarUiState(
    val recurringExpenses: List<ExpenseEntity> = emptyList(),
    val activePeriod: BudgetPeriodEntity? = null,
    val displayedDate: Calendar = Calendar.getInstance(),
    val isLoading: Boolean = true
)

class CalendarViewModel(private val dao: GastoSimpleDao) : ViewModel() {
    private val _state = MutableStateFlow(CalendarUiState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                dao.getRecurringExpenses(),
                dao.getActivePeriod()
            ) { expenses, period ->
                _state.value.copy(
                    recurringExpenses = expenses,
                    activePeriod = period,
                    isLoading = false
                )
            }.collect {
                _state.value = it
            }
        }
    }

    fun nextMonth() {
        val newDate = (_state.value.displayedDate.clone() as Calendar).apply {
            add(Calendar.MONTH, 1)
        }
        _state.value = _state.value.copy(displayedDate = newDate)
    }

    fun previousMonth() {
        val newDate = (_state.value.displayedDate.clone() as Calendar).apply {
            add(Calendar.MONTH, -1)
        }
        _state.value = _state.value.copy(displayedDate = newDate)
    }
}
