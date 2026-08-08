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

@OptIn(ExperimentalMaterial3Api::class)
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
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.expenses.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.empty_expenses))
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    state.activePeriod?.let {
                        Text("Presupuesto: ${it.totalBudget}", style = MaterialTheme.typography.headlineSmall)
                        Spacer(Modifier.height(16.dp))
                    }
                }
                items(state.expenses) { expense ->
                    ExpenseItem(expense)
                }
            }
        }
    }

    if (showForm) {
        ExpenseFormDialog(
            users = state.users,
            onDismiss = { showForm = false },
            onConfirm = { amount, concept, category, userId, recurrence ->
                viewModel.addExpense(amount, concept, category, userId, recurrence)
                showForm = false
            }
        )
    }
}

@Composable
fun ExpenseItem(expense: com.app.gastosimple.core.data.local.ExpenseEntity) {
    val dateFormat = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(expense.concept, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(dateFormat.format(Date(expense.date)), style = MaterialTheme.typography.bodySmall)
            }
            Text("$${expense.amount}", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseFormDialog(
    users: List<com.app.gastosimple.core.data.local.UserEntity>,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, Long, String) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var concept by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Servicios") }
    var userId by remember { mutableStateOf(users.firstOrNull()?.id ?: 0L) }
    var recurrence by remember { mutableStateOf("NONE") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_expense)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text(stringResource(R.string.amount_hint)) }, keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal))
                OutlinedTextField(value = concept, onValueChange = { concept = it }, label = { Text(stringResource(R.string.concept_hint)) })
                // Simple category selector (could be a dropdown)
                Text("Categoría: $category")
                // Simple user selector
                Text("Usuario: ${users.find { it.id == userId }?.name}")
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(amount, concept, category, userId, recurrence) }) {
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
