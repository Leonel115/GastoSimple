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

    private val officialCategories = listOf(
        "Servicios", "Alquiler", "Alimentación", "Suscripciones", "Otros"
    )
    
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
                    
                    val percentageConsumedBD = if (budgetTotal.compareTo(BigDecimal.ZERO) > 0) {
                        totalSpent.multiply(BigDecimal(100))
                            .divide(budgetTotal, 1, RoundingMode.HALF_UP)
                    } else BigDecimal.ZERO
                    
                    val percentageConsumed = percentageConsumedBD.toFloat()
                    val remainingPercentage = BigDecimal("100.0").subtract(percentageConsumedBD).abs().toFloat()

                    // Desglose por categorías (HU-06 Refactor)
                    val categoriesProgress = officialCategories.map { categoryName ->
                        val categoryExpenses = if (categoryName == "Otros") {
                            expenses.filter { it.category !in (officialCategories - "Otros") }
                        } else {
                            expenses.filter { it.category == categoryName }
                        }

                        val categoryAmount = categoryExpenses.fold(BigDecimal.ZERO) { acc, expense ->
                            acc.add(expense.amount.toBigDecimal())
                        }.setScale(2, RoundingMode.HALF_UP)

                        val categoryPercentage = if (budgetTotal.compareTo(BigDecimal.ZERO) > 0) {
                            categoryAmount.multiply(BigDecimal(100))
                                .divide(budgetTotal, 1, RoundingMode.HALF_UP)
                                .toFloat()
                        } else 0f

                        CategoryProgress(
                            name = categoryName,
                            amount = categoryAmount,
                            percentage = categoryPercentage
                        )
                    }

                    // Ajuste de precisión para que la suma de categorías coincida con percentageConsumed
                    val adjustedCategories = if (categoriesProgress.isNotEmpty()) {
                        val sumCategories = categoriesProgress.sumOf { it.percentage.toDouble() }
                        val diff = percentageConsumed.toDouble() - sumCategories
                        
                        if (Math.abs(diff) > 0.001) {
                            categoriesProgress.map { 
                                if (it.name == "Otros") it.copy(percentage = (it.percentage + diff).toFloat())
                                else it
                            }
                        } else categoriesProgress
                    } else categoriesProgress
                    
                    BudgetProgressUiState(
                        budgetTotal = budgetTotal,
                        totalSpent = totalSpent,
                        availableBalance = availableBalance,
                        percentageConsumed = percentageConsumed,
                        remainingPercentage = remainingPercentage,
                        isOverBudget = totalSpent > budgetTotal,
                        isEmpty = false,
                        categories = adjustedCategories
                    )
                }
            }
        }
    }
}
