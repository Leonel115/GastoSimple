package com.app.gastosimple.features.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.app.gastosimple.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(viewModel: CalendarViewModel) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.nav_calendar)) }) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text("Proyección de Gastos Recurrentes", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))
            
            if (state.recurringExpenses.isEmpty()) {
                Text("No hay gastos recurrentes configurados.")
            } else {
                LazyColumn {
                    items(state.recurringExpenses.size) { index ->
                        val expense = state.recurringExpenses[index]
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Row(Modifier.padding(16.dp)) {
                                Text(expense.concept, modifier = Modifier.weight(1f))
                                Text(expense.recurrence, color = MaterialTheme.colorScheme.secondary)
                            }
                        }
                    }
                }
            }
            
            Spacer(Modifier.height(32.dp))
            Text("Aquí se mostrará la cuadrícula interactiva del calendario en la versión final.", color = androidx.compose.ui.graphics.Color.Gray)
        }
    }
}
