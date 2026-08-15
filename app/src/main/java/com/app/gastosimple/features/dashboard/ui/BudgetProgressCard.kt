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
import androidx.compose.ui.unit.dp
import com.app.gastosimple.R
import com.app.gastosimple.features.dashboard.domain.BudgetProgressUiState
import com.app.gastosimple.features.dashboard.domain.CategoryProgress
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

/**
 * Componente Composable que visualiza el progreso del presupuesto con un gráfico de anillo y desglose.
 * HU-06 Refactor: Panel Visual con Donut Chart y Leyenda por Categorías.
 */
@Composable
fun BudgetProgressCard(
    state: BudgetProgressUiState,
    modifier: Modifier = Modifier
) {
    if (state.isEmpty) {
        EmptyBudgetState(modifier)
        return
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Cabecera
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.budget_section),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = String.format(Locale.getDefault(), "%.1f%%", state.percentageConsumed),
                    style = MaterialTheme.typography.titleMedium,
                    color = if (state.isOverBudget) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Black
                )
            }

            // Gráfico de Anillo (Donut Chart)
            BudgetDonutChart(state = state)

            // Leyenda de Categorías
            CategoryLegend(categories = state.categories)

            HorizontalDivider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
            )

            // Resumen de Saldo
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
                    text = formatCurrency(state.availableBalance),
                    style = MaterialTheme.typography.titleLarge,
                    color = if (state.isOverBudget) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Black
                )
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
        modifier = modifier.size(160.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 16.dp.toPx()
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

        // Centro del Donut: Balance en porcentaje
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (state.isOverBudget) stringResource(R.string.budget_excess) else stringResource(R.string.budget_free),
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
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        categories.forEach { category ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(
                            color = getCategoryColor(category.name),
                            shape = RoundedCornerShape(2.dp)
                        )
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "${category.name} (${String.format(Locale.getDefault(), "%.1f%%", category.percentage)})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = formatCurrency(category.amount),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

private fun getCategoryColor(name: String): Color {
    return when (name) {
        "Servicios" -> Color(0xFF42A5F5)
        "Alquiler" -> Color(0xFFFFA726)
        "Alimentación" -> Color(0xFF66BB6A)
        "Suscripciones" -> Color(0xFFAB47BC)
        else -> Color(0xFF78909C) // Otros
    }
}

@Composable
private fun EmptyBudgetState(modifier: Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
        )
    ) {
        Box(
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Configura un presupuesto para ver el desglose.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatCurrency(amount: BigDecimal): String {
    return try {
        val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
        format.format(amount)
    } catch (_: Exception) {
        "$${amount.toPlainString()}"
    }
}
