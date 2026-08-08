package com.app.gastosimple.features.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.gastosimple.core.data.local.BudgetPeriodEntity
import com.app.gastosimple.core.data.local.ExpenseEntity
import com.app.gastosimple.core.data.local.UserEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.Date

data class ExpensesUiState(
    val activePeriod: BudgetPeriodEntity? = null,
    val expenses: List<ExpenseEntity> = emptyList(),
    val users: List<UserEntity> = emptyList(),
    val isLoading: Boolean = true,
    val savingsMessage: String? = null
)

class ExpenseViewModel(
    private val repository: ExpenseRepository,
    private val dao: com.app.gastosimple.core.data.local.GastoSimpleDao // Temporary direct access for complex logic
) : ViewModel() {

    private val _state = MutableStateFlow(ExpensesUiState())
    val state = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            repository.getActivePeriod().collect { period ->
                if (period != null) {
                    combine(
                        repository.getExpenses(period.id),
                        repository.getUsers()
                    ) { expenses, users ->
                        ExpensesUiState(
                            activePeriod = period,
                            expenses = expenses,
                            users = users,
                            isLoading = false
                        )
                    }.collect { _state.value = it }
                } else {
                    _state.value = _state.value.copy(isLoading = false)
                }
            }
        }
    }

    fun addExpense(amount: String, concept: String, category: String, userId: Long, recurrence: String) {
        val periodId = _state.value.activePeriod?.id ?: return
        viewModelScope.launch {
            repository.addExpense(
                ExpenseEntity(
                    amount = amount,
                    concept = concept,
                    category = category,
                    userId = userId,
                    date = Date().time,
                    recurrence = recurrence,
                    periodId = periodId
                )
            )
        }
    }

    fun checkCycleEnd() {
        val period = _state.value.activePeriod ?: return
        // Simplification for MVP: Logic to detect if period should end
        // If ended:
        // 1. Calculate savings
        // 2. Close current period
        // 3. Create new period
    }
}
