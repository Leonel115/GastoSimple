package com.app.gastosimple.features.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.gastosimple.core.data.local.ExpenseEntity
import com.app.gastosimple.core.data.local.GastoSimpleDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

data class CalendarUiState(
    val recurringExpenses: List<ExpenseEntity> = emptyList(),
    val isLoading: Boolean = true
)

class CalendarViewModel(private val dao: GastoSimpleDao) : ViewModel() {
    private val _state = MutableStateFlow(CalendarUiState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            dao.getRecurringExpenses().collect {
                _state.value = CalendarUiState(recurringExpenses = it, isLoading = false)
            }
        }
    }
}
