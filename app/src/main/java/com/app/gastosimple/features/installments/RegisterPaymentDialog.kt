package com.app.gastosimple.features.installments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.app.gastosimple.R
import java.math.BigDecimal

@Composable
fun RegisterPaymentDialog(
    balance: PendingBalance,
    errorResId: Int?,
    onDismiss: () -> Unit,
    onConfirm: (BigDecimal, String) -> Unit
) {
    var amount by remember { mutableStateOf(balance.remainingBalance.toPlainString()) }
    var concept by remember { mutableStateOf("Aporte: ${balance.installment.description}") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.register_payment),
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = balance.installment.description,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "${stringResource(R.string.remaining_balance_label)}: $${balance.remainingBalance}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text(stringResource(R.string.amount_hint)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = errorResId != null,
                    supportingText = {
                        if (errorResId != null) {
                            Text(text = stringResource(errorResId), color = MaterialTheme.colorScheme.error)
                        }
                    }
                )

                OutlinedTextField(
                    value = concept,
                    onValueChange = { concept = it },
                    label = { Text(stringResource(R.string.concept_hint)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amountVal = amount.toBigDecimalOrNull() ?: BigDecimal.ZERO
                    onConfirm(amountVal, concept)
                }
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
