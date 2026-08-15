package com.app.gastosimple.features.dashboard.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.gastosimple.R
import com.app.gastosimple.features.dashboard.domain.BudgetProgressUiState
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

/**
 * Componente Composable que visualiza el progreso del presupuesto.
 * HU-06: Panel Visual de Porcentaje Consumido.
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

    val animatedProgress by animateFloatAsState(
        targetValue = (state.percentageConsumed / 100f).coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 1000),
        label = "BudgetProgressAnimation"
    )

    val progressColor = if (state.isOverBudget) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Cabecera: Título y Porcentaje
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
                    style = MaterialTheme.typography.titleLarge,
                    color = progressColor,
                    fontWeight = FontWeight.Black
                )
            }

            // Barra de Progreso Animada
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp),
                color = progressColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )

            // Detalles: Gastado vs Presupuesto Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BudgetDetailItem(
                    label = "Gastado",
                    amount = state.totalSpent,
                    color = MaterialTheme.colorScheme.onSurface
                )
                BudgetDetailItem(
                    label = "Presupuesto",
                    amount = state.budgetTotal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )

            // Pie: Saldo Disponible
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.remaining_budget),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = formatCurrency(state.availableBalance),
                    style = MaterialTheme.typography.headlineSmall,
                    color = if (state.isOverBudget) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun BudgetDetailItem(
    label: String,
    amount: BigDecimal,
    color: Color
) {
    Column {
        Text(
            text = label, 
            style = MaterialTheme.typography.labelSmall, 
            color = color.copy(alpha = 0.7f)
        )
        Text(
            text = formatCurrency(amount), 
            style = MaterialTheme.typography.bodyLarge, 
            fontWeight = FontWeight.Medium, 
            color = color
        )
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
                .padding(24.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No hay un presupuesto activo configurado.",
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
