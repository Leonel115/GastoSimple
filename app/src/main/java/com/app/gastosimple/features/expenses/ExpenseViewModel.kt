package com.app.gastosimple.features.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.gastosimple.R
import com.app.gastosimple.core.data.local.BudgetPeriodEntity
import com.app.gastosimple.core.data.local.ExpenseEntity
import com.app.gastosimple.core.data.local.UserEntity
import com.app.gastosimple.core.data.prefs.UserPreferencesRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.Calendar
import java.util.TimeZone
import java.util.Date

data class ExpensesUiState(
    val activePeriod: BudgetPeriodEntity? = null,
    val expenses: List<ExpenseEntity> = emptyList(),
    val users: List<UserEntity> = emptyList(),
    val isLoading: Boolean = true,
    val isAddingExpense: Boolean = false,
    val remainingBudget: String = "0.0",
    val plannedBudget: String? = null,
    val errorResId: Int? = null,
    val errorParam: String? = null, // Para mensajes con parámetros como monto insuficiente
    val infoResId: Int? = null
)

class ExpenseViewModel(
    private val repository: ExpenseRepository,
    private val dao: com.app.gastosimple.core.data.local.GastoSimpleDao,
    private val prefs: UserPreferencesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ExpensesUiState())
    val state = _state.asStateFlow()

    private val _expenseAddedEvent = MutableSharedFlow<Unit>()
    val expenseAddedEvent = _expenseAddedEvent.asSharedFlow()

    init {
        loadData()
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    private fun loadData() {
        viewModelScope.launch {
            val todayMidnight = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            repository.getActivePeriod()
                .flatMapLatest { period ->
                    if (period != null) {
                        combine(
                            repository.getExpenses(period.id),
                            repository.getUsers(),
                            prefs.plannedBudget
                        ) { expenses, users, plannedBudget ->
                            // Filtrar eliminados
                            val activeExpenses = expenses.filter { !it.isDeleted }
                            
                            // Cálculo de saldo: Solo restar si fecha <= hoy
                            val totalSpent = activeExpenses
                                .filter { it.date <= todayMidnight }
                                .sumOf { BigDecimal(it.amount) }
                            
                            val remaining = BigDecimal(period.totalBudget).subtract(totalSpent)
                            
                            // Filtrar lista para la UI: Recurrentes + Únicos de HOY
                            val uiExpenses = activeExpenses.filter {
                                val expenseCal = Calendar.getInstance().apply { 
                                    timeInMillis = it.date
                                    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                                }
                                it.recurrence != "NONE" || expenseCal.timeInMillis == todayMidnight
                            }

                            ExpensesUiState(
                                activePeriod = period,
                                expenses = uiExpenses,
                                users = users,
                                isLoading = false,
                                remainingBudget = remaining.toPlainString(),
                                plannedBudget = plannedBudget
                            )
                        }
                    } else {
                        prefs.plannedBudget.map { planned ->
                            ExpensesUiState(isLoading = false, plannedBudget = planned)
                        }
                    }
                }
                .collect { newState ->
                    _state.value = newState
                }
        }
    }

    fun addExpense(
        amount: String,
        concept: String,
        category: String,
        userId: Long?,
        isShared: Boolean,
        recurrence: String,
        recurrenceInterval: Int?,
        date: Long // Nueva fecha seleccionada
    ) {
        if (_state.value.isAddingExpense) return
        
        val periodId = _state.value.activePeriod?.id ?: return

        if (concept.isBlank()) {
            _state.value = _state.value.copy(errorResId = R.string.err_empty_concept)
            return
        }

        val amountVal = amount.toBigDecimalOrNull() ?: BigDecimal.ZERO
        
        if (amountVal <= BigDecimal.ZERO) {
            _state.value = _state.value.copy(errorResId = R.string.err_invalid_amount)
            return
        }
        
        val remaining = _state.value.remainingBudget.toBigDecimal()
        if (amountVal > remaining) {
            _state.value = _state.value.copy(
                errorResId = R.string.err_insufficient_budget,
                errorParam = remaining.toPlainString()
            )
            return
        }

        _state.value = _state.value.copy(isAddingExpense = true, errorResId = null, errorParam = null)

        viewModelScope.launch {
            val utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                timeInMillis = date
            }
            val normalizedDate = Calendar.getInstance().apply {
                set(utcCalendar.get(Calendar.YEAR), utcCalendar.get(Calendar.MONTH), utcCalendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            
            try {
                repository.addExpense(
                    ExpenseEntity(
                        amount = amount,
                        concept = concept,
                        category = category,
                        userId = userId,
                        isShared = isShared,
                        date = normalizedDate,
                        recurrence = recurrence,
                        recurrenceInterval = recurrenceInterval,
                        periodId = periodId
                    )
                )
                _state.value = _state.value.copy(infoResId = R.string.msg_expense_added)
                _expenseAddedEvent.emit(Unit)
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorResId = R.string.err_save_expense)
            } finally {
                _state.value = _state.value.copy(isAddingExpense = false)
            }
        }
    }

    fun editExpense(
        expense: ExpenseEntity,
        newAmount: String,
        newConcept: String,
        newCategory: String,
        newUserId: Long?,
        newIsShared: Boolean,
        newRecurrence: String,
        newRecurrenceInterval: Int?,
        newDate: Long // Nueva fecha seleccionada
    ) {
        if (newConcept.isBlank()) {
            _state.value = _state.value.copy(errorResId = R.string.err_empty_concept)
            return
        }

        val isResetDay = isResetDayForExpense(expense)
        
        viewModelScope.launch {
            val utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                timeInMillis = newDate
            }
            val normalizedDate = Calendar.getInstance().apply {
                set(utcCalendar.get(Calendar.YEAR), utcCalendar.get(Calendar.MONTH), utcCalendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            if (isResetDay) {
                dao.updateExpense(
                    expense.copy(
                        amount = newAmount,
                        concept = newConcept,
                        category = newCategory,
                        userId = newUserId,
                        isShared = newIsShared,
                        date = normalizedDate,
                        recurrence = newRecurrence,
                        recurrenceInterval = newRecurrenceInterval,
                        pendingAmount = null,
                        pendingRecurrenceInterval = null
                    )
                )
                _state.value = _state.value.copy(infoResId = R.string.msg_expense_edited)
            } else {
                dao.updateExpense(
                    expense.copy(
                        concept = newConcept, // Siempre instantáneo
                        pendingAmount = newAmount,
                        pendingRecurrenceInterval = newRecurrenceInterval,
                        category = newCategory,
                        userId = newUserId,
                        isShared = newIsShared,
                        date = normalizedDate
                    )
                )
                _state.value = _state.value.copy(infoResId = R.string.msg_pending_changes)
            }
            _expenseAddedEvent.emit(Unit)
        }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        val isResetDay = isResetDayForExpense(expense)
        
        viewModelScope.launch {
            if (isResetDay) {
                // Soft delete instead of physical delete
                dao.updateExpense(expense.copy(isDeleted = true))
                _state.value = _state.value.copy(infoResId = R.string.msg_expense_deleted)
            } else {
                dao.updateExpense(expense.copy(isPendingDeletion = true))
                _state.value = _state.value.copy(infoResId = R.string.msg_pending_deletion)
            }
            _expenseAddedEvent.emit(Unit)
        }
    }

    fun updatePlannedBudget(amount: String) {
        val period = _state.value.activePeriod
        val isResetDay = if (period != null) isResetDayForPeriod(period) else true
        
        viewModelScope.launch {
            if (isResetDay && period != null) {
                dao.updatePeriod(period.copy(totalBudget = amount))
                _state.value = _state.value.copy(infoResId = R.string.msg_budget_updated)
            } else {
                prefs.setPlannedBudget(amount)
                _state.value = _state.value.copy(infoResId = R.string.msg_budget_planned)
            }
            _expenseAddedEvent.emit(Unit)
        }
    }

    private fun isResetDayForPeriod(period: BudgetPeriodEntity): Boolean {
        val calendar = Calendar.getInstance()
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        
        return if (period.cycleType == "MENSUAL") {
            dayOfMonth == 1
        } else {
            dayOfMonth == 1 || dayOfMonth == 15
        }
    }

    private fun isResetDayForExpense(expense: ExpenseEntity): Boolean {
        // Gastos únicos se consideran siempre en su "día de reset" para permitir CRUD libre
        if (expense.recurrence == "NONE") return true
        
        val calendar = Calendar.getInstance()
        val todayMillis = calendar.apply { 
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) 
        }.timeInMillis
        
        val expenseStartCal = Calendar.getInstance().apply { 
            timeInMillis = expense.date
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }
        
        if (expenseStartCal.timeInMillis == todayMillis) return true
        
        if (expense.recurrence == "MONTHLY") {
            // El reset de un gasto mensual es el mismo día del mes de creación
            return calendar.get(Calendar.DAY_OF_MONTH) == expenseStartCal.get(Calendar.DAY_OF_MONTH)
        }
        
        val interval = expense.recurrenceInterval ?: 0
        if (interval <= 0) return false
        
        val diffMillis = todayMillis - expenseStartCal.timeInMillis
        val diffDays = (diffMillis / (1000 * 60 * 60 * 24)).toInt()
        
        return diffDays >= 0 && diffDays % interval == 0
    }

    fun clearError() {
        _state.value = _state.value.copy(errorResId = null, errorParam = null, infoResId = null)
    }
}
