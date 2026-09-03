package com.app.gastosimple.features.expenses

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.gastosimple.R
import com.app.gastosimple.core.data.local.ExpenseEntity
import com.app.gastosimple.core.data.local.InstallmentFrequency
import com.app.gastosimple.core.ui.theme.CyanBlue
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ExpenseListScreen(viewModel: ExpenseViewModel) {
    val state by viewModel.state.collectAsState()
    var showForm by remember { mutableStateOf(false) }
    var selectedExpense by remember { mutableStateOf<ExpenseEntity?>(null) }
    var showBudgetDialog by remember { mutableStateOf(false) }
    
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.expenseAddedEvent.collect {
            showForm = false
            selectedExpense = null
        }
    }

    LaunchedEffect(state.infoResId) {
        state.infoResId?.let {
            Toast.makeText(context, context.getString(it), Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showForm = true }, containerColor = CyanBlue, contentColor = Color.Black) {
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(stringResource(R.string.budget_label), style = MaterialTheme.typography.labelLarge)
                            Spacer(Modifier.width(8.dp))
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = stringResource(R.string.edit),
                                tint = CyanBlue,
                                modifier = Modifier.size(16.dp).clickable { showBudgetDialog = true }
                            )
                        }
                        Text("$${it.totalBudget}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        
                        state.plannedBudget?.let { planned ->
                            Text(
                                stringResource(R.string.planned_budget_info, planned),
                                style = MaterialTheme.typography.labelSmall, 
                                color = CyanBlue
                            )
                        }

                        Spacer(Modifier.height(8.dp))
                        Text(stringResource(R.string.remaining_budget), style = MaterialTheme.typography.labelLarge)
                        val remainingVal = state.remainingBudget.toDoubleOrNull() ?: 0.0
                        Text("$${state.remainingBudget}", 
                            style = MaterialTheme.typography.titleLarge, 
                            color = if (remainingVal < 0) MaterialTheme.colorScheme.error else Color.White
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
                        val userName = if (expense.isShared) stringResource(R.string.shared_expense) else state.users.find { it.id == expense.userId }?.name ?: stringResource(R.string.unknown)
                        ExpenseItem(expense, userName) {
                            selectedExpense = expense
                            showForm = true
                        }
                    }
                }
            }
        }
    }

    if (showForm) {
        ExpenseFormDialog(
            users = state.users,
            errorResId = state.errorResId,
            errorParam = state.errorParam,
            isSaving = state.isAddingExpense,
            existingExpense = selectedExpense,
            onDismiss = { 
                if (!state.isAddingExpense) { 
                    viewModel.clearError()
                    showForm = false
                    selectedExpense = null
                } 
            },
            onConfirm = { amount, concept, category, userId, isShared, recurrence, interval, isInstallment, totalInstallments, frequency, isEmergency ->
                if (selectedExpense == null) {
                    if (isInstallment) {
                        viewModel.addInstallmentExpense(
                            amount, concept, category, userId, isShared, 
                            totalInstallments ?: 2, frequency ?: InstallmentFrequency.MONTHLY, isEmergency
                        )
                    } else {
                        viewModel.addExpense(amount, concept, category, userId, isShared, recurrence, interval, isEmergency)
                    }
                } else {
                    viewModel.editExpense(selectedExpense!!, amount, concept, category, userId, isShared, recurrence, interval)
                }
            },
            onDelete = {
                selectedExpense?.let { viewModel.deleteExpense(it) }
            }
        )
    }

    if (showBudgetDialog) {
        BudgetEditDialog(
            currentPlanned = state.plannedBudget ?: state.activePeriod?.totalBudget?.toPlainString() ?: "",
            onDismiss = { showBudgetDialog = false },
            onConfirm = { 
                viewModel.updatePlannedBudget(it)
                showBudgetDialog = false
            }
        )
    }
}

@Composable
fun ExpenseItem(expense: ExpenseEntity, userName: String, onClick: () -> Unit) {
    val dateFormat = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
    val isPending = expense.pendingAmount != null || expense.pendingRecurrenceInterval != null || expense.isPendingDeletion
    val isEmergency = expense.isEmergency

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(
                width = if (isEmergency) 1.dp else 0.dp,
                color = if (isEmergency) MaterialTheme.colorScheme.error.copy(alpha = 0.5f) else Color.Transparent,
                shape = CardDefaults.shape
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isEmergency) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f) 
                             else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(expense.concept, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    if (isPending) {
                        Spacer(Modifier.width(8.dp))
                        Surface(color = CyanBlue.copy(alpha = 0.1f), shape = CircleShape) {
                            Text(
                                stringResource(R.string.pending_tag), 
                                style = MaterialTheme.typography.labelSmall, 
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), 
                                color = CyanBlue
                            )
                        }
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val categoryLabel = when(expense.category) {
                        "Alquiler" -> stringResource(R.string.cat_rent)
                        "Alimentación" -> stringResource(R.string.cat_food)
                        "Servicios" -> stringResource(R.string.cat_services)
                        "Suscripciones" -> stringResource(R.string.cat_subscriptions)
                        else -> stringResource(R.string.cat_other)
                    }
                    Text(categoryLabel, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                    Text(" • ", style = MaterialTheme.typography.bodySmall)
                    Text(userName, style = MaterialTheme.typography.bodySmall)
                }
                Text(dateFormat.format(Date(expense.date)), style = MaterialTheme.typography.bodySmall)
            }
            Text("$${expense.amount}", style = MaterialTheme.typography.titleLarge, color = if (expense.isPendingDeletion) Color.Gray else MaterialTheme.colorScheme.primary)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ExpenseFormDialog(
    users: List<com.app.gastosimple.core.data.local.UserEntity>,
    errorResId: Int?,
    errorParam: String?,
    isSaving: Boolean,
    existingExpense: ExpenseEntity? = null,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, Long?, Boolean, String, Int?, Boolean, Int?, InstallmentFrequency?, Boolean) -> Unit,
    onDelete: () -> Unit
) {
    var amount by remember { mutableStateOf(existingExpense?.amount?.toPlainString() ?: "") }
    var concept by remember { mutableStateOf(existingExpense?.concept ?: "") }
    var category by remember { mutableStateOf(existingExpense?.category ?: "Servicios") }
    var userId by remember { mutableStateOf<Long?>(existingExpense?.userId ?: users.firstOrNull()?.id) }
    var isShared by remember { mutableStateOf(existingExpense?.isShared ?: false) }
    
    var isRecurrent by remember { mutableStateOf(existingExpense?.recurrence != "NONE" && existingExpense?.recurrence != null) }
    var recurrenceInterval by remember { mutableStateOf<Int?>(existingExpense?.recurrenceInterval) }
    var customInterval by remember { mutableStateOf(existingExpense?.recurrenceInterval?.toString() ?: "") }

    // Épica 5: Cuotas
    var isInstallment by remember { mutableStateOf(false) }
    var totalInstallments by remember { mutableStateOf("1") }
    var installmentFrequency by remember { mutableStateOf(InstallmentFrequency.MONTHLY) }
    var isEmergency by remember { mutableStateOf(false) }

    val categories = listOf(
        stringResource(R.string.cat_services) to "Servicios",
        stringResource(R.string.cat_rent) to "Alquiler",
        stringResource(R.string.cat_food) to "Alimentación",
        stringResource(R.string.cat_subscriptions) to "Suscripciones",
        stringResource(R.string.cat_other) to "Otros"
    )

    val presets = when (category) {
        "Alquiler" -> listOf(15, 20, 30)
        "Suscripciones" -> listOf(15, 30)
        else -> listOf(7, 15, 30)
    }

    LaunchedEffect(category) {
        if (category == "Suscripciones" || category == "Alquiler" || category == "Servicios") {
            if (recurrenceInterval == null) recurrenceInterval = 30
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existingExpense == null) stringResource(R.string.add_expense) else stringResource(R.string.edit_expense)) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    val isAmountError = errorResId == R.string.err_invalid_amount || errorResId == R.string.err_insufficient_budget
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text(stringResource(R.string.amount_hint)) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal),
                        isError = isAmountError,
                        enabled = !isSaving
                    )
                    if (isAmountError && errorResId != null) {
                        Text(
                            text = if (errorParam != null) stringResource(errorResId, errorParam) else stringResource(errorResId),
                            color = MaterialTheme.colorScheme.error, 
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                item {
                    val isConceptError = errorResId == R.string.err_empty_concept
                    OutlinedTextField(
                        value = concept, 
                        onValueChange = { concept = it }, 
                        label = { Text(stringResource(R.string.concept_hint)) }, 
                        modifier = Modifier.fillMaxWidth(),
                        isError = isConceptError,
                        enabled = !isSaving
                    )
                    if (isConceptError && errorResId != null) {
                        Text(stringResource(errorResId), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
                item {
                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { if (!isSaving) expanded = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = categories.find { it.second == category }?.first ?: category,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(R.string.category_label)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanBlue,
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                                focusedLabelColor = CyanBlue,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.LightGray
                            ),
                            enabled = !isSaving
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            categories.forEach { (label, value) ->
                                DropdownMenuItem(
                                    text = { Text(label) },
                                    onClick = {
                                        category = value
                                        expanded = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }
                }
                
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(stringResource(R.string.is_recurrent_label), style = MaterialTheme.typography.bodyMedium)
                        Switch(
                            checked = isRecurrent,
                            onCheckedChange = { checked ->
                                if (!isSaving) {
                                    isRecurrent = checked
                                    if (checked) {
                                        isInstallment = false
                                        isEmergency = false
                                    }
                                }
                            },
                            enabled = !isSaving && !isInstallment && !isEmergency
                        )
                    }
                }

                if (isRecurrent) {
                    item {
                        Text(stringResource(R.string.recurrence_interval_label), style = MaterialTheme.typography.labelLarge)
                        FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            presets.forEach { days ->
                                FilterChip(
                                    selected = recurrenceInterval == days,
                                    onClick = { 
                                        recurrenceInterval = days
                                        customInterval = days.toString()
                                    },
                                    label = { Text(stringResource(R.string.days_suffix, days)) },
                                    enabled = !isSaving
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = customInterval,
                            onValueChange = { 
                                customInterval = it
                                recurrenceInterval = it.toIntOrNull()
                            },
                            label = { Text(stringResource(R.string.custom_days_hint)) },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                            enabled = !isSaving
                        )
                    }
                }

                if (users.size > 1) {
                    item {
                        Text(stringResource(R.string.payer_label), style = MaterialTheme.typography.labelLarge)
                        Column {
                            users.forEach { user ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = !isShared && userId == user.id, 
                                        onClick = { if (!isSaving) { isShared = false; userId = user.id } },
                                        enabled = !isSaving
                                    )
                                    Text(user.name)
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = isShared, 
                                    onClick = { if (!isSaving) { isShared = true; userId = null } },
                                    enabled = !isSaving
                                )
                                Text(stringResource(R.string.shared_expense))
                            }
                        }
                    }
                }

                // Épica 5: Cuotas e Imprevistos
                if (existingExpense == null) {
                    item {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(stringResource(R.string.is_installment_label), style = MaterialTheme.typography.bodyMedium)
                            Switch(
                                checked = isInstallment,
                                onCheckedChange = { checked ->
                                    if (!isSaving) {
                                        isInstallment = checked
                                        if (checked) {
                                            isRecurrent = false
                                        }
                                    }
                                },
                                enabled = !isSaving && !isRecurrent
                            )
                        }
                    }

                    if (isInstallment) {
                        item {
                            val isInstallmentError = (totalInstallments.toIntOrNull() ?: 0) < 2
                            OutlinedTextField(
                                value = totalInstallments,
                                onValueChange = { totalInstallments = it },
                                label = { Text(stringResource(R.string.total_installments_label)) },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                                isError = isInstallmentError,
                                enabled = !isSaving
                            )
                            if (isInstallmentError) {
                                Text(
                                    text = stringResource(R.string.err_min_installments),
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                        item {
                            var expanded by remember { mutableStateOf(false) }
                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { if (!isSaving) expanded = it },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = if (installmentFrequency == InstallmentFrequency.MONTHLY) 
                                        stringResource(R.string.mensual) else stringResource(R.string.quincenal),
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text(stringResource(R.string.cycle_type_label)) },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                                    enabled = !isSaving
                                )
                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text(stringResource(R.string.mensual)) },
                                        onClick = { installmentFrequency = InstallmentFrequency.MONTHLY; expanded = false }
                                    )
                                    DropdownMenuItem(
                                        text = { Text(stringResource(R.string.quincenal)) },
                                        onClick = { installmentFrequency = InstallmentFrequency.BIWEEKLY; expanded = false }
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(stringResource(R.string.is_emergency_label), style = MaterialTheme.typography.bodyMedium)
                            Switch(
                                checked = isEmergency,
                                onCheckedChange = { checked ->
                                    if (!isSaving) {
                                        isEmergency = checked
                                        if (checked) {
                                            isRecurrent = false
                                        }
                                    }
                                },
                                enabled = !isSaving && !isRecurrent
                            )
                        }
                    }
                }
                
                if (existingExpense != null) {
                    item {
                        TextButton(
                            onClick = onDelete,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                            enabled = !isSaving
                        ) {
                            Text(stringResource(R.string.delete_expense))
                        }
                    }
                }
            }
        },
        confirmButton = {
            val isValid = amount.isNotBlank() && (amount.toBigDecimalOrNull() ?: BigDecimal.ZERO) > BigDecimal.ZERO && 
                         (!isInstallment || (totalInstallments.toIntOrNull() ?: 0) >= 2)
            
            Button(
                onClick = { 
                    onConfirm(
                        amount, 
                        concept, 
                        category, 
                        userId, 
                        isShared, 
                        if (isRecurrent) "PERIODIC" else "NONE", 
                        if (isRecurrent) recurrenceInterval else null,
                        isInstallment,
                        totalInstallments.toIntOrNull(),
                        installmentFrequency,
                        isEmergency
                    ) 
                },
                enabled = !isSaving && isValid
            ) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                } else {
                    Text(stringResource(R.string.save))
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSaving) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
fun BudgetEditDialog(
    currentPlanned: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var amount by remember { mutableStateOf(currentPlanned) }
    val isValid = amount.isNotBlank() && (amount.toDoubleOrNull() ?: 0.0) > 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.edit_expense)) }, // Or new string
        text = {
            Column {
                Text(stringResource(R.string.msg_budget_planned), style = MaterialTheme.typography.bodySmall, color = CyanBlue)
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text(stringResource(R.string.setup_budget_title)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal),
                    prefix = { Text("$ ") },
                    isError = amount.isNotBlank() && !isValid
                )
                if (amount.isNotBlank() && !isValid) {
                    Text(stringResource(R.string.err_invalid_budget), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(amount) },
                enabled = isValid
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
