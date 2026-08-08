package com.app.gastosimple.features.expenses

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.gastosimple.R
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ExpenseListScreen(viewModel: ExpenseViewModel) {
    val state by viewModel.state.collectAsState()
    var showForm by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showForm = true }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_expense))
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            state.activePeriod?.let {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(stringResource(R.string.budget_label), style = MaterialTheme.typography.labelLarge)
                        Text("$${it.totalBudget}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text("Saldo Restante", style = MaterialTheme.typography.labelLarge)
                        Text("$${state.remainingBudget}", 
                            style = MaterialTheme.typography.titleLarge, 
                            color = if ((state.remainingBudget.toDoubleOrNull() ?: 0.0) < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            if (state.isLoading) {
                Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.expenses.isEmpty()) {
                Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.empty_expenses))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.expenses) { expense ->
                        val userName = if (expense.isShared) stringResource(R.string.shared_expense) else state.users.find { it.id == expense.userId }?.name ?: "Unknown"
                        ExpenseItem(expense, userName)
                    }
                }
            }
        }
    }
    // ... rest of the code

    if (showForm) {
        ExpenseFormDialog(
            users = state.users,
            error = state.error,
            onDismiss = { viewModel.clearError(); showForm = false },
            onConfirm = { amount, concept, category, userId, isShared, recurrence ->
                viewModel.addExpense(amount, concept, category, userId, isShared, recurrence)
                if (state.error == null) showForm = false
            }
        )
    }
}

@Composable
fun ExpenseItem(expense: com.app.gastosimple.core.data.local.ExpenseEntity, userName: String) {
    val dateFormat = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(expense.concept, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(expense.category, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                    Text(" • ", style = MaterialTheme.typography.bodySmall)
                    Text(userName, style = MaterialTheme.typography.bodySmall)
                }
                Text(dateFormat.format(Date(expense.date)), style = MaterialTheme.typography.bodySmall)
            }
            Text("$${expense.amount}", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ExpenseFormDialog(
    users: List<com.app.gastosimple.core.data.local.UserEntity>,
    error: String?,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, Long?, Boolean, String) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var concept by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Servicios") }
    var userId by remember { mutableStateOf<Long?>(users.firstOrNull()?.id) }
    var isShared by remember { mutableStateOf(false) }
    var recurrence by remember { mutableStateOf("NONE") }

    val categories = listOf(
        stringResource(R.string.cat_services) to "Servicios",
        stringResource(R.string.cat_rent) to "Alquiler",
        stringResource(R.string.cat_food) to "Alimentación",
        stringResource(R.string.cat_subscriptions) to "Suscripciones",
        stringResource(R.string.cat_other) to "Otros"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_expense)) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text(stringResource(R.string.amount_hint)) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal),
                        isError = error != null
                    )
                    if (error != null) {
                        Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
                item {
                    OutlinedTextField(value = concept, onValueChange = { concept = it }, label = { Text(stringResource(R.string.concept_hint)) }, modifier = Modifier.fillMaxWidth())
                }
                item {
                    Text(stringResource(R.string.category_label), style = MaterialTheme.typography.labelLarge)
                    FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        categories.forEach { (label, value) ->
                            FilterChip(
                                selected = category == value,
                                onClick = { category = value },
                                label = { Text(label) }
                            )
                        }
                    }
                }
                if (users.size > 1) {
                    item {
                        Text(stringResource(R.string.payer_label), style = MaterialTheme.typography.labelLarge)
                        Column {
                            users.forEach { user ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(selected = !isShared && userId == user.id, onClick = { isShared = false; userId = user.id })
                                    Text(user.name)
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(selected = isShared, onClick = { isShared = true; userId = null })
                                Text(stringResource(R.string.shared_expense))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(amount, concept, category, userId, isShared, recurrence) }) {
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
