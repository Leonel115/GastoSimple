package com.app.gastosimple.features.dashboard.domain

import com.app.gastosimple.core.data.local.BudgetPeriodEntity
import com.app.gastosimple.core.data.local.ExpenseEntity
import com.app.gastosimple.features.expenses.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

/**
 * Caso de uso para obtener el progreso del presupuesto y métricas financieras del Dashboard (HU-06).
 * Soporta filtrado temporal por Mes/Ciclo (MONTHLY), Anual (ANNUAL) y Total (TOTAL).
 * Aplica aritmética de alta precisión con [BigDecimal] y [RoundingMode.HALF_UP].
 */
class GetBudgetProgressUseCase(private val repository: ExpenseRepository) {

    private val officialCategories = listOf(
        "Servicios", "Alquiler", "Alimentación", "Suscripciones", "Otros"
    )

    /**
     * Consulta el progreso del presupuesto para el periodo actual del sistema (Mes actual).
     */
    operator fun invoke(): Flow<BudgetProgressUiState> {
        val now = LocalDate.now()
        return invoke(DashboardFilterMode.MONTHLY, now.year, now.monthValue)
    }

    /**
     * Consulta el progreso del presupuesto filtrado por modo y fecha seleccionada.
     *
     * @param filterMode Modo de filtrado ([DashboardFilterMode.MONTHLY], [DashboardFilterMode.ANNUAL], [DashboardFilterMode.TOTAL]).
     * @param year Año calendario consultado.
     * @param month Mes consultado (1..12).
     */
    operator fun invoke(
        filterMode: DashboardFilterMode,
        year: Int,
        month: Int
    ): Flow<BudgetProgressUiState> {
        val periodsFlow: Flow<List<BudgetPeriodEntity>>
        val expensesFlow: Flow<List<ExpenseEntity>>

        when (filterMode) {
            DashboardFilterMode.MONTHLY -> {
                val dateRange = DateRangeCalculator.calculateMonthRange(year, month)
                periodsFlow = repository.getBudgetPeriodsByDateRange(dateRange.startMillis, dateRange.endMillis)
                expensesFlow = repository.getExpensesByDateRange(dateRange.startMillis, dateRange.endMillis)
            }
            DashboardFilterMode.ANNUAL -> {
                val dateRange = DateRangeCalculator.calculateYearRange(year)
                periodsFlow = repository.getBudgetPeriodsByDateRange(dateRange.startMillis, dateRange.endMillis)
                expensesFlow = repository.getExpensesByDateRange(dateRange.startMillis, dateRange.endMillis)
            }
            DashboardFilterMode.TOTAL -> {
                periodsFlow = repository.getAllPeriods()
                expensesFlow = repository.getAllExpenses()
            }
        }

        val isPast = DateRangeCalculator.isPastPeriod(filterMode, year, month)

        return periodsFlow.combine(expensesFlow) { periods, expenses ->
            buildUiState(
                periods = periods,
                expenses = expenses,
                filterMode = filterMode,
                year = year,
                month = month,
                isPast = isPast
            )
        }
    }

    private fun buildUiState(
        periods: List<BudgetPeriodEntity>,
        expenses: List<ExpenseEntity>,
        filterMode: DashboardFilterMode,
        year: Int,
        month: Int,
        isPast: Boolean
    ): BudgetProgressUiState {
        val budgetTotal = calculateTotalBudget(periods)
        val totalSpent = calculateTotalSpent(expenses)

        if (budgetTotal <= BigDecimal.ZERO && totalSpent <= BigDecimal.ZERO) {
            return BudgetProgressUiState(
                isEmpty = true,
                selectedFilterMode = filterMode,
                selectedMonth = month,
                selectedYear = year,
                isPastPeriod = isPast
            )
        }

        val availableBalance = budgetTotal.subtract(totalSpent).setScale(2, RoundingMode.HALF_UP)
        val percentageConsumedBD = calculateConsumedPercentage(totalSpent, budgetTotal)
        val percentageConsumed = percentageConsumedBD.toFloat()
        val remainingPercentage = calculateRemainingPercentage(percentageConsumedBD)

        val categoriesProgress = calculateCategoriesProgress(expenses, budgetTotal)
        val adjustedCategories = adjustCategoryPercentages(categoriesProgress, percentageConsumed)

        return BudgetProgressUiState(
            budgetTotal = budgetTotal,
            totalSpent = totalSpent,
            availableBalance = availableBalance,
            percentageConsumed = percentageConsumed,
            remainingPercentage = remainingPercentage,
            isOverBudget = totalSpent > budgetTotal && budgetTotal > BigDecimal.ZERO,
            isEmpty = false,
            categories = adjustedCategories,
            selectedFilterMode = filterMode,
            selectedMonth = month,
            selectedYear = year,
            isPastPeriod = isPast
        )
    }

    private fun calculateTotalBudget(periods: List<BudgetPeriodEntity>): BigDecimal {
        return periods.fold(BigDecimal.ZERO) { acc, period ->
            val amount = period.totalBudget.toBigDecimalOrNull() ?: BigDecimal.ZERO
            acc.add(amount)
        }.setScale(2, RoundingMode.HALF_UP)
    }

    private fun calculateTotalSpent(expenses: List<ExpenseEntity>): BigDecimal {
        return expenses.fold(BigDecimal.ZERO) { acc, expense ->
            val amount = expense.amount.toBigDecimalOrNull() ?: BigDecimal.ZERO
            acc.add(amount)
        }.setScale(2, RoundingMode.HALF_UP)
    }

    private fun calculateConsumedPercentage(spent: BigDecimal, budget: BigDecimal): BigDecimal {
        if (budget.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO
        return spent.multiply(BigDecimal(100))
            .divide(budget, 1, RoundingMode.HALF_UP)
    }

    private fun calculateRemainingPercentage(consumedBD: BigDecimal): Float {
        val hundred = BigDecimal("100.0")
        return if (consumedBD > hundred) {
            0f
        } else {
            hundred.subtract(consumedBD).abs().toFloat()
        }
    }

    private fun calculateCategoriesProgress(
        expenses: List<ExpenseEntity>,
        budgetTotal: BigDecimal
    ): List<CategoryProgress> {
        return officialCategories.map { categoryName ->
            val categoryExpenses = if (categoryName == "Otros") {
                expenses.filter { it.category !in (officialCategories - "Otros") }
            } else {
                expenses.filter { it.category == categoryName }
            }

            val categoryAmount = calculateTotalSpent(categoryExpenses)
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
    }

    private fun adjustCategoryPercentages(
        categories: List<CategoryProgress>,
        targetTotalPercentage: Float
    ): List<CategoryProgress> {
        if (categories.isEmpty()) return categories

        val sumCategories = categories.sumOf { it.percentage.toDouble() }
        val diff = targetTotalPercentage.toDouble() - sumCategories

        return if (Math.abs(diff) > 0.001) {
            categories.map { category ->
                if (category.name == "Otros") {
                    category.copy(percentage = (category.percentage + diff).toFloat())
                } else {
                    category
                }
            }
        } else {
            categories
        }
    }
}

