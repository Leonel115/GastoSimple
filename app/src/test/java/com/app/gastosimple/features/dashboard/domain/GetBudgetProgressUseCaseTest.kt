package com.app.gastosimple.features.dashboard.domain

import com.app.gastosimple.core.data.local.BudgetPeriodEntity
import com.app.gastosimple.core.data.local.ExpenseEntity
import com.app.gastosimple.features.expenses.ExpenseRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

/**
 * Pruebas unitarias para GetBudgetProgressUseCase.
 * Verifica la lógica de cálculo financiero y filtrado temporal (HU-06).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GetBudgetProgressUseCaseTest {

    private lateinit var repository: ExpenseRepository
    private lateinit var useCase: GetBudgetProgressUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetBudgetProgressUseCase(repository)
    }

    @Test
    fun `when monthly filter is applied with budget 1000 and spent 500, then percentage is 50`() = runTest {
        // Arrange
        val range = DateRangeCalculator.calculateMonthRange(2026, 8)
        val period = BudgetPeriodEntity(id = 1, totalBudget = "1000.00", startDate = range.startMillis, endDate = range.endMillis, cycleType = "MENSUAL")
        val expenses = listOf(
            ExpenseEntity(amount = "200.00", concept = "A", category = "Servicios", userId = 1, date = range.startMillis + 1000, recurrence = "NONE", periodId = 1),
            ExpenseEntity(amount = "300.00", concept = "B", category = "Alimentación", userId = 1, date = range.startMillis + 2000, recurrence = "NONE", periodId = 1)
        )
        every { repository.getBudgetPeriodsByDateRange(range.startMillis, range.endMillis) } returns flowOf(listOf(period))
        every { repository.getExpensesByDateRange(range.startMillis, range.endMillis) } returns flowOf(expenses)

        // Act
        val result = useCase(DashboardFilterMode.MONTHLY, 2026, 8).first()

        // Assert
        assertEquals(BigDecimal("1000.00"), result.budgetTotal)
        assertEquals(BigDecimal("500.00"), result.totalSpent)
        assertEquals(BigDecimal("500.00"), result.availableBalance)
        assertEquals(50f, result.percentageConsumed, 0.01f)
        assertEquals(50f, result.remainingPercentage, 0.01f)
        assertFalse(result.isOverBudget)
        assertFalse(result.isEmpty)
        assertEquals(5, result.categories.size)
    }

    @Test
    fun `when past period is queried, then isPastPeriod is true`() = runTest {
        // Arrange (2024 is always past)
        val range = DateRangeCalculator.calculateMonthRange(2024, 5)
        val period = BudgetPeriodEntity(id = 1, totalBudget = "1200.00", startDate = range.startMillis, endDate = range.endMillis, cycleType = "MENSUAL")
        val expenses = listOf(
            ExpenseEntity(amount = "400.00", concept = "Super", category = "Alimentación", userId = 1, date = range.startMillis + 5000, recurrence = "NONE", periodId = 1)
        )
        every { repository.getBudgetPeriodsByDateRange(range.startMillis, range.endMillis) } returns flowOf(listOf(period))
        every { repository.getExpensesByDateRange(range.startMillis, range.endMillis) } returns flowOf(expenses)

        // Act
        val result = useCase(DashboardFilterMode.MONTHLY, 2024, 5).first()

        // Assert
        assertTrue(result.isPastPeriod)
        assertEquals(BigDecimal("1200.00"), result.budgetTotal)
        assertEquals(BigDecimal("400.00"), result.totalSpent)
    }

    @Test
    fun `when annual filter is applied, aggregates all periods and expenses for that year`() = runTest {
        // Arrange
        val range = DateRangeCalculator.calculateYearRange(2025)
        val periods = listOf(
            BudgetPeriodEntity(id = 1, totalBudget = "1500.00", startDate = range.startMillis, endDate = null, cycleType = "MENSUAL"),
            BudgetPeriodEntity(id = 2, totalBudget = "1500.00", startDate = range.startMillis + 1000000, endDate = null, cycleType = "MENSUAL")
        )
        val expenses = listOf(
            ExpenseEntity(amount = "600.00", concept = "A", category = "Alquiler", userId = 1, date = range.startMillis + 5000, recurrence = "NONE", periodId = 1),
            ExpenseEntity(amount = "400.00", concept = "B", category = "Servicios", userId = 1, date = range.startMillis + 1005000, recurrence = "NONE", periodId = 2)
        )
        every { repository.getBudgetPeriodsByDateRange(range.startMillis, range.endMillis) } returns flowOf(periods)
        every { repository.getExpensesByDateRange(range.startMillis, range.endMillis) } returns flowOf(expenses)

        // Act
        val result = useCase(DashboardFilterMode.ANNUAL, 2025, 1).first()

        // Assert
        assertEquals(BigDecimal("3000.00"), result.budgetTotal)
        assertEquals(BigDecimal("1000.00"), result.totalSpent)
        assertEquals(BigDecimal("2000.00"), result.availableBalance)
        assertEquals(33.3f, result.percentageConsumed, 0.1f)
    }

    @Test
    fun `when total filter is applied, aggregates all historical periods and expenses`() = runTest {
        // Arrange
        val periods = listOf(
            BudgetPeriodEntity(id = 1, totalBudget = "1000.00", startDate = 0, endDate = null, cycleType = "MENSUAL"),
            BudgetPeriodEntity(id = 2, totalBudget = "2000.00", startDate = 0, endDate = null, cycleType = "MENSUAL")
        )
        val expenses = listOf(
            ExpenseEntity(amount = "500.00", concept = "A", category = "Alimentación", userId = 1, date = 0, recurrence = "NONE", periodId = 1),
            ExpenseEntity(amount = "1000.00", concept = "B", category = "Alquiler", userId = 1, date = 0, recurrence = "NONE", periodId = 2)
        )
        every { repository.getAllPeriods() } returns flowOf(periods)
        every { repository.getAllExpenses() } returns flowOf(expenses)

        // Act
        val result = useCase(DashboardFilterMode.TOTAL, 2026, 8).first()

        // Assert
        assertEquals(BigDecimal("3000.00"), result.budgetTotal)
        assertEquals(BigDecimal("1500.00"), result.totalSpent)
        assertEquals(50f, result.percentageConsumed, 0.01f)
        assertFalse(result.isPastPeriod)
    }

    @Test
    fun `when spent exceeds budget, then isOverBudget is true`() = runTest {
        // Arrange
        val range = DateRangeCalculator.calculateMonthRange(2026, 8)
        val period = BudgetPeriodEntity(id = 1, totalBudget = "100.00", startDate = range.startMillis, endDate = range.endMillis, cycleType = "MENSUAL")
        val expenses = listOf(
            ExpenseEntity(amount = "120.00", concept = "A", category = "Servicios", userId = 1, date = range.startMillis, recurrence = "NONE", periodId = 1)
        )
        every { repository.getBudgetPeriodsByDateRange(range.startMillis, range.endMillis) } returns flowOf(listOf(period))
        every { repository.getExpensesByDateRange(range.startMillis, range.endMillis) } returns flowOf(expenses)

        // Act
        val result = useCase(DashboardFilterMode.MONTHLY, 2026, 8).first()

        // Assert
        assertTrue(result.isOverBudget)
        assertEquals(120f, result.percentageConsumed, 0.01f)
        assertEquals(0f, result.remainingPercentage, 0.01f)
    }

    @Test
    fun `when no periods and no expenses exist, then isEmpty is true`() = runTest {
        // Arrange
        val range = DateRangeCalculator.calculateMonthRange(2026, 8)
        every { repository.getBudgetPeriodsByDateRange(range.startMillis, range.endMillis) } returns flowOf(emptyList())
        every { repository.getExpensesByDateRange(range.startMillis, range.endMillis) } returns flowOf(emptyList())

        // Act
        val result = useCase(DashboardFilterMode.MONTHLY, 2026, 8).first()

        // Assert
        assertTrue(result.isEmpty)
    }
}

