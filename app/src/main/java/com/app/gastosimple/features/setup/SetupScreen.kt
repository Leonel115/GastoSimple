package com.app.gastosimple.features.setup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.setup_title), fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Sección de Presupuesto
            item {
                SetupSectionCard(
                    title = stringResource(R.string.budget_section),
                    icon = Icons.Default.ShoppingCart
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = state.budget,
                            onValueChange = { viewModel.onBudgetChange(it) },
                            label = { Text(stringResource(R.string.budget_label)) },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            prefix = { Text("$") }
                        )

                        Text(stringResource(R.string.cycle_type_label), style = MaterialTheme.typography.labelLarge)
                        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                            SegmentedButton(
                                selected = state.cycleType == "MENSUAL",
                                onClick = { viewModel.onCycleTypeChange("MENSUAL") },
                                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                            ) {
                                Text(stringResource(R.string.mensual))
                            }
                            SegmentedButton(
                                selected = state.cycleType == "QUINCENAL",
                                onClick = { viewModel.onCycleTypeChange("QUINCENAL") },
                                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                            ) {
                                Text(stringResource(R.string.quincenal))
                            }
                        }
                    }
                }
            }

            // Sección de Usuarios
            item {
                SetupSectionCard(
                    title = stringResource(R.string.users_section),
                    icon = Icons.Default.Person
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Configuración de Participantes", style = MaterialTheme.typography.labelLarge)
                        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                            SegmentedButton(
                                selected = !state.isMultiUser,
                                onClick = { viewModel.onMultiUserChange(false) },
                                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                            ) {
                                Text(stringResource(R.string.single_user))
                            }
                            SegmentedButton(
                                selected = state.isMultiUser,
                                onClick = { viewModel.onMultiUserChange(true) },
                                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                            ) {
                                Text(stringResource(R.string.multi_user))
                            }
                        }

                        if (!state.isMultiUser) {
                            // Modo Usuario Único: Editar nombre principal
                            OutlinedTextField(
                                value = state.users[0].name,
                                onValueChange = { viewModel.updateUserName(0, it) },
                                label = { Text(stringResource(R.string.user_name_label)) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            if (state.isMultiUser) {
                itemsIndexed(state.users) { index, user ->
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = user.name,
                                onValueChange = { viewModel.updateUserName(index, it) },
                                label = { Text("Nombre") },
                                modifier = Modifier.weight(1.5f)
                            )
                            OutlinedTextField(
                                value = if (user.contributionPercentage == 0.0) "" else user.contributionPercentage.toString(),
                                onValueChange = { val p = it.toDoubleOrNull() ?: 0.0; viewModel.updatePercentage(index, p) },
                                label = { Text("%") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                            )
                            if (state.users.size > 1) {
                                IconButton(onClick = { viewModel.removeUser(index) }) {
                                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.remove_user), tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }

                item {
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = newUserName,
                                onValueChange = { newUserName = it },
                                label = { Text(stringResource(R.string.user_name_hint)) },
                                modifier = Modifier.weight(1f)
                            )
                            FilledIconButton(
                                onClick = {
                                    if (newUserName.isNotBlank()) {
                                        viewModel.addUser(newUserName)
                                        newUserName = ""
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_user))
                            }
                        }
                    }
                }
            }

            item {
                val total = state.users.sumOf { it.contributionPercentage }
                if (state.isMultiUser) {
                    Surface(
                        color = if (Math.abs(total - 100.0) < 0.001) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Total Aportes: $total%",
                            modifier = Modifier.padding(8.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (state.error != null) {
                    Text(state.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                Button(
                    onClick = { viewModel.finishSetup() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(stringResource(R.string.finish), style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Composable
fun SetupSectionCard(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(16.dp))
            content()
        }
    }
}
