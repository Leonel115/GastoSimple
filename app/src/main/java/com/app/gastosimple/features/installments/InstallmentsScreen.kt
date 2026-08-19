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
import com.app.gastosimple.R
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstallmentsScreen(
    viewModel: InstallmentViewModel,
    onNavigateToRegisterPayment: (Long) -> Unit // For future implementation of payment registration
) {
    val balances by viewModel.balances.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val congratulationsMsg = stringResource(R.string.msg_congratulations)

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is InstallmentEvent.Congratulate -> {
                    snackbarHostState.showSnackbar(congratulationsMsg)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.installments_title)) }
            )
        }
    ) { padding ->
        if (balances.isEmpty()) {
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
                items(balances, key = { it.installment.id }) { balance ->
                    PendingBalanceCard(
                        balance = balance,
                        onClick = { onNavigateToRegisterPayment(balance.installment.id) }
                    )
                }
            }
        }
    }
}
