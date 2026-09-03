package com.app.gastosimple.features.installments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import com.app.gastosimple.R
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstallmentsScreen(
    viewModel: InstallmentViewModel
) {
    val balancesState by viewModel.balancesState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is InstallmentEvent.Congratulate -> {
                    snackbarHostState.showSnackbar(context.getString(R.string.msg_congratulations))
                }
                is InstallmentEvent.Error -> {
                    snackbarHostState.showSnackbar(context.getString(event.resId))
                }
            }
        }
    }

    val hasNoBalances = balancesState.activeBalances.isEmpty() && balancesState.settledBalances.isEmpty()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.installments_title)) }
            )
        }
    ) { padding ->
        if (hasNoBalances) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.empty_installments),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (balancesState.activeBalances.isNotEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.active_obligations_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    items(balancesState.activeBalances, key = { "active_${it.installment.id}" }) { balance ->
                        PendingBalanceCard(
                            balance = balance,
                            onClick = { viewModel.onPaymentClicked(balance) }
                        )
                    }
                }

                if (balancesState.settledBalances.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = stringResource(R.string.settled_history_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    items(balancesState.settledBalances, key = { "settled_${it.installment.id}" }) { balance ->
                        PendingBalanceCard(
                            balance = balance,
                            onClick = { /* Deudas liquidadas son de solo lectura */ },
                            isSettled = true
                        )
                    }
                }
            }
        }

        if (uiState.showPaymentDialog && uiState.selectedBalance != null) {
            RegisterPaymentDialog(
                balance = uiState.selectedBalance!!,
                errorResId = uiState.errorResId,
                onDismiss = { viewModel.onDismissPaymentDialog() },
                onConfirm = { amount, concept ->
                    viewModel.registerPayment(amount, concept)
                }
            )
        }
    }
}
