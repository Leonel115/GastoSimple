package com.app.gastosimple.features.dashboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.app.gastosimple.core.ui.theme.GastoSimpleTheme
import com.app.gastosimple.features.dashboard.DashboardViewModel
import com.app.gastosimple.features.dashboard.domain.BudgetProgressUiState
import com.app.gastosimple.features.dashboard.domain.CategoryProgress
import com.app.gastosimple.features.dashboard.domain.DashboardFilterMode
import org.koin.androidx.compose.koinViewModel
import java.math.BigDecimal
import java.util.Calendar

/**
 * Pantalla principal del Dashboard (HU-06).
 * Integra la cabecera con selectores temporales y el panel de progreso de presupuesto.
 */
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = koinViewModel()
) {
    val budgetProgress by viewModel.budgetProgress.collectAsState()

    DashboardContent(
        state = budgetProgress,
        onFilterModeSelected = viewModel::setFilterMode,
        onMonthSelected = viewModel::setMonth,
        onYearSelected = viewModel::setYear
    )
}

/**
 * Contenido Composable desacoplado de la pantalla de Dashboard para máxima testeabilidad y soporte de Previews.
 */
@Composable
fun DashboardContent(
    state: BudgetProgressUiState,
    onFilterModeSelected: (DashboardFilterMode) -> Unit,
    onMonthSelected: (Int) -> Unit,
    onYearSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Cabecera superior con Título, Selector de Año y Tabs de filtro temporal
        DashboardHeader(
            selectedFilterMode = state.selectedFilterMode,
            selectedMonth = state.selectedMonth,
            selectedYear = state.selectedYear,
            availableYears = state.availableYears,
            onFilterModeSelected = onFilterModeSelected,
            onMonthSelected = onMonthSelected,
            onYearSelected = onYearSelected
        )

        // Tarjeta con Donut Chart y desglose de presupuesto
        BudgetProgressCard(state = state)
    }
}

// -------------------------------------------------------------------------
// Previews de Compose
// -------------------------------------------------------------------------

@Preview(name = "Dashboard - Monthly (Current)", showBackground = true, backgroundColor = 0xFF0B132B)
@Composable
private fun DashboardScreenMonthlyPreview() {
    GastoSimpleTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            DashboardContent(
                state = BudgetProgressUiState(
                    budgetTotal = BigDecimal("1500.00"),
                    totalSpent = BigDecimal("863.69"),
                    availableBalance = BigDecimal("636.31"),
                    percentageConsumed = 57.6f,
                    remainingPercentage = 42.4f,
                    isOverBudget = false,
                    isEmpty = false,
                    isPastPeriod = false,
                    selectedFilterMode = DashboardFilterMode.MONTHLY,
                    selectedMonth = 8,
                    selectedYear = 2026,
                    availableYears = listOf(2024, 2025, 2026),
                    categories = listOf(
                        CategoryProgress("Servicios", BigDecimal("0.00"), 0.0f),
                        CategoryProgress("Alquiler", BigDecimal("0.00"), 0.0f),
                        CategoryProgress("Alimentación", BigDecimal("363.69"), 24.2f),
                        CategoryProgress("Suscripciones", BigDecimal("0.00"), 0.0f),
                        CategoryProgress("Otros", BigDecimal("500.00"), 33.4f)
                    )
                ),
                onFilterModeSelected = {},
                onMonthSelected = {},
                onYearSelected = {}
            )
        }
    }
}

@Preview(name = "Dashboard - Past Period (Sin Usar)", showBackground = true, backgroundColor = 0xFF0B132B)
@Composable
private fun DashboardScreenPastPeriodPreview() {
    GastoSimpleTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            DashboardContent(
                state = BudgetProgressUiState(
                    budgetTotal = BigDecimal("1500.00"),
                    totalSpent = BigDecimal("863.69"),
                    availableBalance = BigDecimal("636.31"),
                    percentageConsumed = 57.6f,
                    remainingPercentage = 42.4f,
                    isOverBudget = false,
                    isEmpty = false,
                    isPastPeriod = true,
                    selectedFilterMode = DashboardFilterMode.MONTHLY,
                    selectedMonth = 5,
                    selectedYear = 2026,
                    availableYears = listOf(2024, 2025, 2026),
                    categories = listOf(
                        CategoryProgress("Servicios", BigDecimal("0.00"), 0.0f),
                        CategoryProgress("Alquiler", BigDecimal("0.00"), 0.0f),
                        CategoryProgress("Alimentación", BigDecimal("363.69"), 24.2f),
                        CategoryProgress("Suscripciones", BigDecimal("0.00"), 0.0f),
                        CategoryProgress("Otros", BigDecimal("500.00"), 33.4f)
                    )
                ),
                onFilterModeSelected = {},
                onMonthSelected = {},
                onYearSelected = {}
            )
        }
    }
}

@Preview(name = "Dashboard - Empty Expenses (HU-07)", showBackground = true, backgroundColor = 0xFF0B132B)
@Composable
private fun DashboardScreenEmptyExpensesPreview() {
    GastoSimpleTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            DashboardContent(
                state = BudgetProgressUiState(
                    budgetTotal = BigDecimal("1500.00"),
                    totalSpent = BigDecimal.ZERO,
                    availableBalance = BigDecimal("1500.00"),
                    percentageConsumed = 0.0f,
                    remainingPercentage = 100.0f,
                    isOverBudget = false,
                    isEmpty = false,
                    isPastPeriod = false,
                    selectedFilterMode = DashboardFilterMode.MONTHLY,
                    selectedMonth = 8,
                    selectedYear = 2026,
                    availableYears = listOf(2024, 2025, 2026),
                    categories = emptyList()
                ),
                onFilterModeSelected = {},
                onMonthSelected = {},
                onYearSelected = {}
            )
        }
    }
}


