package com.app.gastosimple.features.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.app.gastosimple.R
import com.app.gastosimple.core.data.local.ExpenseEntity
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(viewModel: CalendarViewModel) {
    val state by viewModel.state.collectAsState()
    
    // Calendar math based on displayedDate
    val displayedCalendar = state.displayedDate
    val monthName = java.text.SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(displayedCalendar.time)
    
    val daysInMonth = displayedCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfMonth = (displayedCalendar.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, 1) }
    val firstDayOfWeek = firstDayOfMonth.get(Calendar.DAY_OF_WEEK) - 1

    var selectedDay by remember { mutableStateOf<Int?>(null) }

    // Today normalized to midnight
    val todayMidnight = remember {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
    
    // Reset selected day when month changes
    LaunchedEffect(state.displayedDate) {
        selectedDay = null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_calendar)) },
                actions = {
                    IconButton(onClick = { viewModel.previousMonth() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Mes Anterior")
                    }
                    IconButton(onClick = { viewModel.nextMonth() }) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "Mes Siguiente")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Month Header
            Text(
                text = monthName.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )

            // Static Calendar Grid
            Column(modifier = Modifier.fillMaxWidth()) {
                val daysOfWeek = listOf("D", "L", "M", "M", "J", "V", "S")
                Row(modifier = Modifier.fillMaxWidth()) {
                    daysOfWeek.forEach { day ->
                        Text(
                            text = day,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                
                Spacer(Modifier.height(8.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier.fillMaxWidth().height(330.dp),
                    userScrollEnabled = false
                ) {
                    items(firstDayOfWeek) { Box(Modifier.size(40.dp)) }

                    items(daysInMonth) { index ->
                        val dayNum = index + 1
                        
                        val targetDayCal = (state.displayedDate.clone() as Calendar).apply { 
                            set(Calendar.DAY_OF_MONTH, dayNum)
                            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                        }
                        val targetDayMillis = targetDayCal.timeInMillis
                        val isPastDay = targetDayMillis < todayMidnight
                        
                        // Reset Day Logic - Only for today and future
                        val isResetDay = if (isPastDay) false else state.activePeriod?.let { period ->
                            val periodStartCal = Calendar.getInstance().apply { 
                                timeInMillis = period.startDate 
                                set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                            }
                            if (targetDayMillis < periodStartCal.timeInMillis) return@let false
                            
                            if (period.cycleType == "MENSUAL") {
                                targetDayCal.get(Calendar.DAY_OF_MONTH) == periodStartCal.get(Calendar.DAY_OF_MONTH)
                            } else {
                                val diffDays = ((targetDayMillis - periodStartCal.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()
                                diffDays >= 0 && diffDays % 15 == 0
                            }
                        } ?: false

                        // Events for Day Logic - Only for today and future
                        val eventsForDay = if (isPastDay) emptyList() else state.recurringExpenses.filter { expense ->
                            val expenseStartCal = Calendar.getInstance().apply { 
                                timeInMillis = expense.date 
                                set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                            }
                            val interval = expense.recurrenceInterval ?: 0
                            
                            if (interval <= 0) {
                                expenseStartCal.timeInMillis == targetDayMillis
                            } else {
                                if (targetDayMillis < expenseStartCal.timeInMillis) return@filter false
                                val diffDays = ((targetDayMillis - expenseStartCal.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()
                                diffDays >= 0 && diffDays % interval == 0
                            }
                        }

                        val hasEvent = eventsForDay.isNotEmpty()
                        val eventColor = getEventColor(eventsForDay)

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
                                    if (hasEvent && isResetDay) 2.dp else 1.dp,
                                    if (hasEvent && isResetDay) eventColor else if (hasEvent) eventColor else Color.LightGray.copy(alpha = 0.5f),
                                    shape = MaterialTheme.shapes.small
                                )
                                .clickable { selectedDay = dayNum },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = dayNum.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = when {
                                        isPastDay -> Color.Gray
                                        hasEvent -> eventColor
                                        else -> MaterialTheme.colorScheme.onSurface
                                    }
                                )
                                if (isResetDay) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Reset",
                                        tint = Color.Yellow,
                                        modifier = Modifier.size(12.dp)
                                    )
                                } else if (hasEvent) {
                                    Box(Modifier.size(4.dp).background(eventColor, MaterialTheme.shapes.extraSmall))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
            Spacer(Modifier.height(8.dp))

            Text("Detalles del día seleccionado:", style = MaterialTheme.typography.titleMedium)
            
            val selectedDayExpenses = if (selectedDay == null) emptyList() else {
                val targetDayMillis = (state.displayedDate.clone() as Calendar).apply { 
                    set(Calendar.DAY_OF_MONTH, selectedDay ?: 0)
                    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                }.timeInMillis

                state.recurringExpenses.filter { expense ->
                    val expenseStartCal = Calendar.getInstance().apply { 
                        timeInMillis = expense.date 
                        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                    }
                    val interval = expense.recurrenceInterval ?: 0
                    
                    if (interval <= 0) {
                        expenseStartCal.timeInMillis == targetDayMillis
                    } else {
                        if (targetDayMillis < expenseStartCal.timeInMillis) return@filter false
                        val diffDays = ((targetDayMillis - expenseStartCal.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()
                        diffDays >= 0 && diffDays % interval == 0
                    }
                }
            }

            if (selectedDay == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Selecciona un día para ver los detalles.", color = Color.Gray)
                }
            } else if (selectedDayExpenses.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay gastos programados para este día.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(selectedDayExpenses) { expense ->
                        Card(modifier = Modifier.fillMaxWidth()) {
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

private fun getEventColor(events: List<ExpenseEntity>): Color {
    if (events.size > 1) return Color(0xFF4CAF50) // Green
    val event = events.firstOrNull() ?: return Color.Transparent
    val interval = event.recurrenceInterval ?: 0
    return when {
        interval in 16..31 -> Color(0xFF0C17E1) // Blue
        interval in 8..15 -> Color(0xFF00D4D4) // Cyan
        interval in 1..7 -> Color(0xFFE91E63) // Pink
        interval > 31 -> Color(0xFFB928D2) // Purple
        else -> Color(0xFF1A73E8) // Default primary blue
    }
}
