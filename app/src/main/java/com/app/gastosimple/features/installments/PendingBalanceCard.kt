package com.app.gastosimple.features.installments

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.gastosimple.R
import com.app.gastosimple.core.ui.theme.CyanBlue

@Composable
fun PendingBalanceCard(
    balance: PendingBalance,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isEmergency = balance.installment.isEmergency
    val cardColor = if (isEmergency) {
        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = balance.installment.description,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (isEmergency) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.emergency_tag),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                DetailItem(stringResource(R.string.original_capital), "$${balance.originalCapital}")
                DetailItem(stringResource(R.string.total_paid), "$${balance.totalPaid}")
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.remaining_balance_label) + ": $${balance.remainingBalance}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (isEmergency) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { balance.progress },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = if (isEmergency) MaterialTheme.colorScheme.error else CyanBlue,
                trackColor = Color.Gray.copy(alpha = 0.2f)
            )
            
            Text(
                text = "${(balance.progress * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun DetailItem(label: String, value: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}
