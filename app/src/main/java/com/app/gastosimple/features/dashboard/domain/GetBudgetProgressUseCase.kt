package com.app.gastosimple.features.dashboard.domain

import com.app.gastosimple.features.expenses.ExpenseRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Caso de uso para obtener el progreso del presupuesto actual.
 * Aplica las fórmulas financieras y reglas de precisión especificadas en la HU-06.
 */
class GetBudgetProgressUseCase(private val repository: ExpenseRepository) {
    
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<BudgetProgressUiState> {
        return repository.getActivePeriod().flatMapLatest { period ->
            if (period == null || period.totalBudget.toBigDecimalOrNull() == null || period.totalBudget.toBigDecimal() <= BigDecimal.ZERO) {
                flowOf(BudgetProgressUiState(isEmpty = true))
            } else {
                val budgetTotal = period.totalBudget.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
                
                repository.getExpenses(period.id).map { expenses ->
                    val totalSpent = expenses.fold(BigDecimal.ZERO) { acc, expense ->
                        acc.add(expense.amount.toBigDecimal())
                    }.setScale(2, RoundingMode.HALF_UP)
                    
                    val availableBalance = budgetTotal.subtract(totalSpent).setScale(2, RoundingMode.HALF_UP)
                    
                    val percentageConsumed = if (budgetTotal.compareTo(BigDecimal.ZERO) > 0) {
                        totalSpent.multiply(BigDecimal(100))
                            .divide(budgetTotal, 2, RoundingMode.HALF_UP)
                            .toFloat()
                    } else 0f
                    
                    BudgetProgressUiState(
                        budgetTotal = budgetTotal,
                        totalSpent = totalSpent,
                        availableBalance = availableBalance,
                        percentageConsumed = percentageConsumed,
                        isOverBudget = totalSpent > budgetTotal,
                        isEmpty = false
                    )
                }
            }
        }
    }
}
