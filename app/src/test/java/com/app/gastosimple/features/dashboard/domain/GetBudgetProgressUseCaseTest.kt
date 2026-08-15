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
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

/**
 * Pruebas unitarias para GetBudgetProgressUseCase.
 * Verifica la lógica de cálculo financiero ante diversos escenarios.
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
    fun `when budget is 1000 and spent is 500, then percentage is 50`() = runTest {
        // Arrange
        val period = BudgetPeriodEntity(id = 1, totalBudget = "1000.00", startDate = 0, endDate = null, cycleType = "MENSUAL")
        val expenses = listOf(
            ExpenseEntity(amount = "200.00", concept = "A", category = "Servicios", userId = 1, date = 0, recurrence = "NONE", periodId = 1),
            ExpenseEntity(amount = "300.00", concept = "B", category = "Alimentación", userId = 1, date = 0, recurrence = "NONE", periodId = 1)
        )
        every { repository.getActivePeriod() } returns flowOf(period)
        every { repository.getExpenses(1) } returns flowOf(expenses)

        // Act
        val result = useCase().first()

        // Assert
        assertEquals(BigDecimal("1000.00"), result.budgetTotal)
        assertEquals(BigDecimal("500.00"), result.totalSpent)
        assertEquals(BigDecimal("500.00"), result.availableBalance)
        assertEquals(50f, result.percentageConsumed, 0.01f)
        assertFalse(result.isOverBudget)
        assertFalse(result.isEmpty)
    }

    @Test
    fun `when expenses are registered, then all 5 official categories are present`() = runTest {
        // Arrange
        val period = BudgetPeriodEntity(id = 1, totalBudget = "1000.00", startDate = 0, endDate = null, cycleType = "MENSUAL")
        val expenses = listOf(
            ExpenseEntity(amount = "100.00", concept = "Serv1", category = "Servicios", userId = 1, date = 0, recurrence = "NONE", periodId = 1),
            ExpenseEntity(amount = "200.00", concept = "Alim1", category = "Alimentación", userId = 1, date = 0, recurrence = "NONE", periodId = 1),
            ExpenseEntity(amount = "50.00", concept = "Desconocido", category = "Extra", userId = 1, date = 0, recurrence = "NONE", periodId = 1)
        )
        every { repository.getActivePeriod() } returns flowOf(period)
        every { repository.getExpenses(1) } returns flowOf(expenses)

        // Act
        val result = useCase().first()

        // Assert
        assertEquals(5, result.categories.size)
        
        val servicios = result.categories.find { it.name == "Servicios" }!!
        assertEquals(BigDecimal("100.00"), servicios.amount)
        assertEquals(10f, servicios.percentage, 0.01f)
        
        val alimentacion = result.categories.find { it.name == "Alimentación" }!!
        assertEquals(BigDecimal("200.00"), alimentacion.amount)
        assertEquals(20f, alimentacion.percentage, 0.01f)
        
        val otros = result.categories.find { it.name == "Otros" }!!
        assertEquals(BigDecimal("50.00"), otros.amount)
        assertEquals(5f, otros.percentage, 0.01f)
        
        val alquiler = result.categories.find { it.name == "Alquiler" }!!
        assertEquals(BigDecimal("0.00"), alquiler.amount)
        assertEquals(0f, alquiler.percentage, 0.01f)
    }

    @Test
    fun `when spent exceeds budget, then isOverBudget is true`() = runTest {
        // Arrange
        val period = BudgetPeriodEntity(id = 1, totalBudget = "100.00", startDate = 0, endDate = null, cycleType = "MENSUAL")
        val expenses = listOf(
            ExpenseEntity(amount = "120.00", concept = "A", category = "Servicios", userId = 1, date = 0, recurrence = "NONE", periodId = 1)
        )
        every { repository.getActivePeriod() } returns flowOf(period)
        every { repository.getExpenses(1) } returns flowOf(expenses)

        // Act
        val result = useCase().first()

        // Assert
        assertTrue(result.isOverBudget)
        assertEquals(120f, result.percentageConsumed, 0.01f)
    }

    @Test
    fun `when budget is zero, then isEmpty is true`() = runTest {
        // Arrange
        val period = BudgetPeriodEntity(id = 1, totalBudget = "0.00", startDate = 0, endDate = null, cycleType = "MENSUAL")
        every { repository.getActivePeriod() } returns flowOf(period)

        // Act
        val result = useCase().first()

        // Assert
        assertTrue(result.isEmpty)
    }

    @Test
    fun `when no active period exists, then isEmpty is true`() = runTest {
        // Arrange
        every { repository.getActivePeriod() } returns flowOf(null)

        // Act
        val result = useCase().first()

        // Assert
        assertTrue(result.isEmpty)
    }
}
