package com.app.gastosimple.features.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.gastosimple.R
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(viewModel: CalendarViewModel) {
    val state by viewModel.state.collectAsState()
    val calendar = Calendar.getInstance()
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1

    var selectedDay by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.nav_calendar)) }) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text(
                text = java.text.SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date()),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.fillMaxWidth().heightIn(max = 300.dp)
            ) {
                val daysOfWeek = listOf("D", "L", "M", "M", "J", "V", "S")
                items(daysOfWeek) { day ->
                    Box(Modifier.padding(4.dp), contentAlignment = Alignment.Center) {
                        Text(day, fontWeight = FontWeight.Bold)
                    }
                }

                items(firstDayOfWeek) { Box(Modifier.size(40.dp)) }

                items(daysInMonth) { day ->
                    val dayNum = day + 1
                    val hasEvent = state.recurringExpenses.any {
                        val cal = Calendar.getInstance().apply { timeInMillis = it.date }
                        cal.get(Calendar.DAY_OF_MONTH) == dayNum
                    }

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .padding(2.dp)
                            .background(
                                if (selectedDay == dayNum) MaterialTheme.colorScheme.primaryContainer
                                else Color.Transparent,
                                shape = MaterialTheme.shapes.small
                            )
                            .border(
                                1.dp,
                                if (hasEvent) MaterialTheme.colorScheme.primary else Color.LightGray,
                                shape = MaterialTheme.shapes.small
                            )
                            .clickable { selectedDay = dayNum },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(dayNum.toString())
                            if (hasEvent) {
                                Box(Modifier.size(4.dp).background(MaterialTheme.colorScheme.primary, MaterialTheme.shapes.extraSmall))
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Text("Gastos para el día seleccionado:", style = MaterialTheme.typography.titleMedium)
            
            val dayExpenses = state.recurringExpenses.filter {
                val cal = Calendar.getInstance().apply { timeInMillis = it.date }
                cal.get(Calendar.DAY_OF_MONTH) == selectedDay
            }

            if (selectedDay == null) {
                Text("Selecciona un día para ver los detalles.", color = Color.Gray)
            } else if (dayExpenses.isEmpty()) {
                Text("No hay gastos programados para este día.", color = Color.Gray)
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(dayExpenses) { expense ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text(expense.concept, fontWeight = FontWeight.Bold)
                                    Text(expense.category, style = MaterialTheme.typography.bodySmall)
                                }
                                Text("$${expense.amount}", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}