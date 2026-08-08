package com.app.gastosimple.features.setup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
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
                Text("Usuarios y Aportes", style = MaterialTheme.typography.titleMedium)
            }

            itemsIndexed(state.users) { index, user ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(user.name, modifier = Modifier.weight(1f))
                    OutlinedTextField(
                        value = user.contributionPercentage.toString(),
                        onValueChange = { val p = it.toDoubleOrNull() ?: 0.0; viewModel.updatePercentage(index, p) },
                        label = { Text("%") },
                        modifier = Modifier.width(80.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }
            }

            item {
                if (state.error != null) {
                    Text(state.error!!, color = MaterialTheme.colorScheme.error)
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
