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
    val remainingBudget: String = "0.0",
    val error: String? = null
)

class ExpenseViewModel(
    private val repository: ExpenseRepository,
    private val dao: com.app.gastosimple.core.data.local.GastoSimpleDao
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
                        val totalSpent = expenses.sumOf { BigDecimal(it.amount) }
                        val remaining = BigDecimal(period.totalBudget).subtract(totalSpent)
                        
                        ExpensesUiState(
                            activePeriod = period,
                            expenses = expenses,
                            users = users,
                            isLoading = false,
                            remainingBudget = remaining.toPlainString()
                        )
                    }.collect { _state.value = it }
                } else {
                    _state.value = _state.value.copy(isLoading = false)
                }
            }
        }
    }

    fun addExpense(amount: String, concept: String, category: String, userId: Long?, isShared: Boolean, recurrence: String) {
        val periodId = _state.value.activePeriod?.id ?: return
        val amountVal = amount.toBigDecimalOrNull() ?: BigDecimal.ZERO
        
        if (amountVal <= BigDecimal.ZERO) {
            _state.value = _state.value.copy(error = "El monto debe ser mayor a 0")
            return
        }
        
        val remaining = _state.value.remainingBudget.toBigDecimal()
        if (amountVal > remaining) {
            _state.value = _state.value.copy(error = "El monto supera el presupuesto restante ($${remaining.toPlainString()})")
            return
        }

        viewModelScope.launch {
            repository.addExpense(
                ExpenseEntity(
                    amount = amount,
                    concept = concept,
                    category = category,
                    userId = userId,
                    isShared = isShared,
                    date = Date().time,
                    recurrence = recurrence,
                    periodId = periodId
                )
            )
            _state.value = _state.value.copy(error = null)
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}
