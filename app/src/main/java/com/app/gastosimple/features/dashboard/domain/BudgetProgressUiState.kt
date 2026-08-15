package com.app.gastosimple.features.dashboard.domain

import java.math.BigDecimal

/**
 * Representa el estado de UI para el panel de progreso del presupuesto.
 * HU-06: Panel Visual de Porcentaje Consumido.
 */
data class BudgetProgressUiState(
    val budgetTotal: BigDecimal = BigDecimal.ZERO,
    val totalSpent: BigDecimal = BigDecimal.ZERO,
    val availableBalance: BigDecimal = BigDecimal.ZERO,
    val percentageConsumed: Float = 0f,
    val isOverBudget: Boolean = false,
    val isEmpty: Boolean = true
)
