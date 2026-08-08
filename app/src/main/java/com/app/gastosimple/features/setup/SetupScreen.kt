package com.app.gastosimple.features.setup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.app.gastosimple.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupScreen(viewModel: SetupViewModel, onFinished: () -> Unit) {
    val state by viewModel.state.collectAsState()
    var newUserName by remember { mutableStateOf("") }

    LaunchedEffect(state.isFinished) {
        if (state.isFinished) onFinished()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.setup_title)) }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = state.budget,
                    onValueChange = { viewModel.onBudgetChange(it) },
                    label = { Text(stringResource(R.string.budget_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }

            item {
                Text(stringResource(R.string.cycle_type_label), style = MaterialTheme.typography.titleMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = state.cycleType == "MENSUAL",
                        onClick = { viewModel.onCycleTypeChange("MENSUAL") }
                    )
                    Text(stringResource(R.string.mensual))
                    Spacer(Modifier.width(16.dp))
                    RadioButton(
                        selected = state.cycleType == "QUINCENAL",
                        onClick = { viewModel.onCycleTypeChange("QUINCENAL") }
                    )
                    Text(stringResource(R.string.quincenal))
                }
            }

            item {
                Text("Tipo de Usuario", style = MaterialTheme.typography.titleMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = !state.isMultiUser,
                        onClick = { viewModel.onMultiUserChange(false) }
                    )
                    Text(stringResource(R.string.single_user))
                    Spacer(Modifier.width(16.dp))
                    RadioButton(
                        selected = state.isMultiUser,
                        onClick = { viewModel.onMultiUserChange(true) }
                    )
                    Text(stringResource(R.string.multi_user))
                }
            }

            if (state.isMultiUser) {
                item {
                    Text("Participantes y Porcentajes", style = MaterialTheme.typography.titleMedium)
                }

                itemsIndexed(state.users) { index, user ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(user.name, modifier = Modifier.weight(1f))
                        OutlinedTextField(
                            value = if (user.contributionPercentage == 0.0) "" else user.contributionPercentage.toString(),
                            onValueChange = { val p = it.toDoubleOrNull() ?: 0.0; viewModel.updatePercentage(index, p) },
                            label = { Text("%") },
                            modifier = Modifier.width(80.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                        if (state.users.size > 1) {
                            IconButton(onClick = { viewModel.removeUser(index) }) {
                                Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.remove_user))
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newUserName,
                            onValueChange = { newUserName = it },
                            label = { Text(stringResource(R.string.user_name_hint)) },
                            modifier = Modifier.weight(1f)
                        )
                        Button(onClick = {
                            if (newUserName.isNotBlank()) {
                                viewModel.addUser(newUserName)
                                newUserName = ""
                            }
                        }) {
                            Text(stringResource(R.string.add_user))
                        }
                    }
                }
            }

            item {
                if (state.error != null) {
                    Text(state.error!!, color = MaterialTheme.colorScheme.error)
                }
                val total = state.users.sumOf { it.contributionPercentage }
                if (state.isMultiUser) {
                    Text("Total: $total%", style = MaterialTheme.typography.bodySmall, color = if (Math.abs(total - 100.0) < 0.001) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                }
                Button(
                    onClick = { viewModel.finishSetup() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.finish))
                }
            }
        }
    }
}
