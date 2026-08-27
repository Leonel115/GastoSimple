package com.app.gastosimple.features.dashboard.domain

import java.math.BigDecimal

/**
 * Representa el progreso de una categoría específica.
 */
data class CategoryProgress(
    val name: String,
    val amount: BigDecimal,
    val percentage: Float
)

/**
 * Representa el estado de UI para el panel de progreso del presupuesto y filtrado temporal.
 * HU-06: Panel Visual de Porcentaje Consumido con desglose por categorías y selector temporal.
 */
data class BudgetProgressUiState(
    val budgetTotal: BigDecimal = BigDecimal.ZERO,
    val totalSpent: BigDecimal = BigDecimal.ZERO,
    val availableBalance: BigDecimal = BigDecimal.ZERO,
    val percentageConsumed: Float = 0f,
    val remainingPercentage: Float = 0f,
    val isOverBudget: Boolean = false,
    val isEmpty: Boolean = true,
    val categories: List<CategoryProgress> = emptyList(),
    val selectedFilterMode: DashboardFilterMode = DashboardFilterMode.MONTHLY,
    val selectedMonth: Int = 8,
    val selectedYear: Int = 2026,
    val availableYears: List<Int> = listOf(2024, 2025, 2026),
    val isPastPeriod: Boolean = false
)

