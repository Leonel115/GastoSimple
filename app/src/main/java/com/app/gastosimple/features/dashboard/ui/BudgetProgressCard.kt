package com.app.gastosimple.features.dashboard.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.app.gastosimple.R
import com.app.gastosimple.core.ui.formatAsCurrency
import com.app.gastosimple.core.ui.toCategoryStringRes
import com.app.gastosimple.features.dashboard.domain.BudgetProgressUiState


import com.app.gastosimple.features.dashboard.domain.CategoryProgress
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

import androidx.compose.ui.tooling.preview.Preview
import com.app.gastosimple.core.ui.theme.GastoSimpleTheme
import com.app.gastosimple.features.dashboard.domain.DashboardFilterMode

/**
 * Constantes de dimensionamiento para la tarjeta de progreso y gráfico Donut.
 * Elimina 'magic numbers' conforme a principles.md / continuerules.md.
 */
object BudgetCardDimens {
    val CardPaddingHorizontal: Dp = 16.dp
    val CardPaddingVertical: Dp = 8.dp
    val CardContentPadding: Dp = 20.dp
    val CardSpacing: Dp = 20.dp
    val CardElevation: Dp = 2.dp
    val DonutChartSize: Dp = 160.dp
    val DonutStrokeWidth: Dp = 16.dp
    val LegendSpacing: Dp = 10.dp
    val CategoryBadgeSize: Dp = 10.dp
    val CategoryBadgeCornerRadius: Dp = 2.dp
    val CategoryBadgeSpacing: Dp = 10.dp
    val EmptyStatePadding: Dp = 32.dp
    val BalanceSummarySpacing: Dp = 8.dp
}

/**
 * Componente Composable que visualiza el progreso del presupuesto con un gráfico de anillo y desglose.
 * HU-06 Refactor: Panel Visual con Donut Chart ("Sin usar"), Leyenda por Categorías y Resumen de Saldos.
 */
@Composable
fun BudgetProgressCard(
    state: BudgetProgressUiState,
    modifier: Modifier = Modifier
) {
    val hasExpenses = state.totalSpent.compareTo(BigDecimal.ZERO) > 0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = BudgetCardDimens.CardPaddingHorizontal,
                vertical = BudgetCardDimens.CardPaddingVertical
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = BudgetCardDimens.CardElevation)
    ) {
        Column(
            modifier = Modifier.padding(BudgetCardDimens.CardContentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(BudgetCardDimens.CardSpacing)
        ) {
            // Cabecera del Card
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.budget_section),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = String.format(Locale.getDefault(), "%.1f%%", state.percentageConsumed),
                    style = MaterialTheme.typography.titleMedium,
                    color = if (state.isOverBudget) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Black
                )
            }

            // Cuerpo del Card: Gráfico con Leyenda si hay consumos, o Estado Vacío (HU-07)
            if (hasExpenses) {
                // Gráfico de Anillo (Donut Chart)
                BudgetDonutChart(state = state)

                // Leyenda de Categorías
                CategoryLegend(categories = state.categories)
            } else {
                DashboardEmptyState()
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
            )

            // Resumen de Saldos: Saldo Original y Saldo Restante
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(BudgetCardDimens.BalanceSummarySpacing)
            ) {
                // Saldo Original
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.original_budget),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = state.budgetTotal.formatAsCurrency(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Saldo Restante
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.remaining_budget),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = state.availableBalance.formatAsCurrency(),
                        style = MaterialTheme.typography.titleLarge,
                        color = if (state.isOverBudget) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

    }
}

@Composable
private fun BudgetDonutChart(
    state: BudgetProgressUiState,
    modifier: Modifier = Modifier
) {
    val totalAngle = 360f
    
    // Animación del progreso total para el efecto de llenado inicial
    val animationProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 1200),
        label = "DonutChartAnimation"
    )

    val errorColor = MaterialTheme.colorScheme.error
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    
    Box(
        modifier = modifier.size(BudgetCardDimens.DonutChartSize),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = BudgetCardDimens.DonutStrokeWidth.toPx()
            var startAngle = -90f

            if (state.isOverBudget) {
                // Si hay sobregiro, mostramos el anillo en color de error
                drawArc(
                    color = errorColor,
                    startAngle = 0f,
                    sweepAngle = totalAngle * animationProgress,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            } else {
                // Dibujar fondo (track)
                drawArc(
                    color = trackColor,
                    startAngle = 0f,
                    sweepAngle = totalAngle,
                    useCenter = false,
                    style = Stroke(width = strokeWidth)
                )

                // Dibujar segmentos por categoría
                state.categories.forEach { category ->
                    val sweepAngle = (category.percentage / 100f * totalAngle) * animationProgress
                    if (sweepAngle > 0f) {
                        drawArc(
                            color = getCategoryColor(category.name),
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                        )
                        startAngle += sweepAngle
                    }
                }
            }
        }

        // Centro del Donut: Etiqueta fija "Sin usar" (o "Exceso" en sobregiro)
        val centerLabel = if (state.isOverBudget) {
            stringResource(R.string.budget_excess)
        } else {
            stringResource(R.string.budget_unused)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = centerLabel,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = String.format(Locale.getDefault(), "%.1f%%", state.remainingPercentage),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = if (state.isOverBudget) errorColor else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun CategoryLegend(categories: List<CategoryProgress>) {

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(BudgetCardDimens.LegendSpacing)
    ) {
        categories.forEach { category ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(BudgetCardDimens.CategoryBadgeSize)
                        .background(
                            color = getCategoryColor(category.name),
                            shape = RoundedCornerShape(BudgetCardDimens.CategoryBadgeCornerRadius)
                        )
                )
                Spacer(modifier = Modifier.width(BudgetCardDimens.CategoryBadgeSpacing))
                val categoryLabel = stringResource(category.name.toCategoryStringRes())
                Text(
                    text = "$categoryLabel (${String.format(Locale.getDefault(), "%.1f%%", category.percentage)})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = category.amount.formatAsCurrency(),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

private fun getCategoryColor(name: String): Color {
    return when (name.trim().lowercase()) {
        "servicios", "services" -> Color(0xFF42A5F5)
        "alquiler", "rent" -> Color(0xFFFFA726)
        "alimentación", "alimentacion", "food" -> Color(0xFF66BB6A)
        "suscripciones", "subscriptions" -> Color(0xFFAB47BC)
        else -> Color(0xFF78909C) // Otros
    }
}


// -------------------------------------------------------------------------
// Previews de Compose
// -------------------------------------------------------------------------

@Preview(name = "Budget Card - Current Period Dark", showBackground = true, backgroundColor = 0xFF0B132B)
@Composable
private fun BudgetProgressCardCurrentPreview() {
    GastoSimpleTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            BudgetProgressCard(
                state = BudgetProgressUiState(
                    budgetTotal = BigDecimal("1500.00"),
                    totalSpent = BigDecimal("863.69"),
                    availableBalance = BigDecimal("636.31"),
                    percentageConsumed = 57.6f,
                    remainingPercentage = 42.4f,
                    isOverBudget = false,
                    isEmpty = false,
                    isPastPeriod = false,
                    categories = listOf(
                        CategoryProgress("Servicios", BigDecimal("0.00"), 0.0f),
                        CategoryProgress("Alquiler", BigDecimal("0.00"), 0.0f),
                        CategoryProgress("Alimentación", BigDecimal("363.69"), 24.2f),
                        CategoryProgress("Suscripciones", BigDecimal("0.00"), 0.0f),
                        CategoryProgress("Otros", BigDecimal("500.00"), 33.4f)
                    )
                )
            )
        }
    }
}

@Preview(name = "Budget Card - Empty Expenses (HU-07) Dark", showBackground = true, backgroundColor = 0xFF0B132B)
@Composable
private fun BudgetProgressCardEmptyExpensesPreview() {
    GastoSimpleTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            BudgetProgressCard(
                state = BudgetProgressUiState(
                    budgetTotal = BigDecimal("1500.00"),
                    totalSpent = BigDecimal.ZERO,
                    availableBalance = BigDecimal("1500.00"),
                    percentageConsumed = 0.0f,
                    remainingPercentage = 100.0f,
                    isOverBudget = false,
                    isEmpty = false,
                    isPastPeriod = false,
                    categories = emptyList()
                )
            )
        }
    }
}

@Preview(name = "Budget Card - Past Period (Sin usar) Dark", showBackground = true, backgroundColor = 0xFF0B132B)
@Composable
private fun BudgetProgressCardPastPeriodPreview() {
    GastoSimpleTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            BudgetProgressCard(
                state = BudgetProgressUiState(
                    budgetTotal = BigDecimal("1500.00"),
                    totalSpent = BigDecimal("863.69"),
                    availableBalance = BigDecimal("636.31"),
                    percentageConsumed = 57.6f,
                    remainingPercentage = 42.4f,
                    isOverBudget = false,
                    isEmpty = false,
                    isPastPeriod = true,
                    categories = listOf(
                        CategoryProgress("Servicios", BigDecimal("0.00"), 0.0f),
                        CategoryProgress("Alquiler", BigDecimal("0.00"), 0.0f),
                        CategoryProgress("Alimentación", BigDecimal("363.69"), 24.2f),
                        CategoryProgress("Suscripciones", BigDecimal("0.00"), 0.0f),
                        CategoryProgress("Otros", BigDecimal("500.00"), 33.4f)
                    )
                )
            )
        }
    }
}

@Preview(name = "Budget Card - Over Budget Dark", showBackground = true, backgroundColor = 0xFF0B132B)
@Composable
private fun BudgetProgressCardOverBudgetPreview() {
    GastoSimpleTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            BudgetProgressCard(
                state = BudgetProgressUiState(
                    budgetTotal = BigDecimal("1000.00"),
                    totalSpent = BigDecimal("1200.00"),
                    availableBalance = BigDecimal("-200.00"),
                    percentageConsumed = 120.0f,
                    remainingPercentage = 0.0f,
                    isOverBudget = true,
                    isEmpty = false,
                    isPastPeriod = false,
                    categories = listOf(
                        CategoryProgress("Servicios", BigDecimal("300.00"), 30.0f),
                        CategoryProgress("Alquiler", BigDecimal("600.00"), 60.0f),
                        CategoryProgress("Alimentación", BigDecimal("300.00"), 30.0f)
                    )
                )
            )
        }
    }
}

