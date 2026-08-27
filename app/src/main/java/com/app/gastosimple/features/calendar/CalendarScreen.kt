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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
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
import com.app.gastosimple.core.ui.formatAsCurrency
import com.app.gastosimple.core.ui.toCategoryStringRes
import com.app.gastosimple.features.expenses.ExpenseFormDialog
import com.app.gastosimple.features.expenses.ExpenseViewModel
import org.koin.androidx.compose.koinViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = koinViewModel(),
    expenseViewModel: ExpenseViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val expenseState by expenseViewModel.state.collectAsState()
    
    var showAddDialog by remember { mutableStateOf(false) }

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

    LaunchedEffect(Unit) {
        expenseViewModel.expenseAddedEvent.collect {
            showAddDialog = false
        }
    }

    val initialExpenseDateMillis = remember(selectedDay, state.displayedDate) {
        if (selectedDay != null) {
            (state.displayedDate.clone() as Calendar).apply {
                set(Calendar.DAY_OF_MONTH, selectedDay!!)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        } else {
            System.currentTimeMillis()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_calendar)) },
                actions = {
                    IconButton(onClick = { viewModel.previousMonth() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cal_header_prev))
                    }
                    IconButton(onClick = { viewModel.nextMonth() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = stringResource(R.string.cal_header_next))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_expense))
            }
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
                        
                        // Reset Day Logic - FIXED DAYS 1 and 15
                        val isResetDay = if (isPastDay) false else state.activePeriod?.let { period ->
                            if (period.cycleType == "MENSUAL") {
                                dayNum == 1
                            } else {
                                dayNum == 1 || dayNum == 15
                            }
                        } ?: false

                        // Events for Day Logic
                        val eventsForDay = state.allExpenses
                            .filter { !it.isDeleted } // Solo no eliminados
                            .filter { expense ->
                            val expenseStartCal = Calendar.getInstance().apply { 
                                timeInMillis = expense.date 
                                set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                            }
                            
                            if (targetDayMillis < expenseStartCal.timeInMillis) return@filter false

                            if (expense.recurrence == "MONTHLY") {
                                // Mismo día del mes, manejando meses cortos
                                val targetDayOfMonth = targetDayCal.get(Calendar.DAY_OF_MONTH)
                                val originalDayOfMonth = expenseStartCal.get(Calendar.DAY_OF_MONTH)
                                
                                val maxDaysInTargetMonth = targetDayCal.getActualMaximum(Calendar.DAY_OF_MONTH)
                                val effectiveOriginalDay = if (originalDayOfMonth > maxDaysInTargetMonth) maxDaysInTargetMonth else originalDayOfMonth
                                
                                targetDayOfMonth == effectiveOriginalDay
                            } else {
                                val interval = expense.recurrenceInterval ?: 0
                                if (interval <= 0) {
                                    expenseStartCal.timeInMillis == targetDayMillis
                                } else {
                                    val diffDays = ((targetDayMillis - expenseStartCal.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()
                                    diffDays >= 0 && diffDays % interval == 0
                                }
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
                                .clickable { 
                                    selectedDay = if (selectedDay == dayNum) null else dayNum 
                                }
                            ,
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = dayNum.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = when {
                                        isPastDay -> MaterialTheme.colorScheme.outline
                                        hasEvent -> eventColor
                                        else -> MaterialTheme.colorScheme.onSurface
                                    }
                                )
                                if (isResetDay) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = stringResource(R.string.cal_legend_reset),
                                        tint = Color(0xFFF9A825),
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
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(Modifier.height(8.dp))

            if (selectedDay != null) {
                Text(
                    stringResource(R.string.cal_details_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))
            }
            
            val selectedDayExpenses = if (selectedDay == null) emptyList() else {
                val targetDayCal = (state.displayedDate.clone() as Calendar).apply { 
                    set(Calendar.DAY_OF_MONTH, selectedDay ?: 0)
                    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                }
                val targetDayMillis = targetDayCal.timeInMillis

                state.allExpenses.filter { expense ->
                    val expenseStartCal = Calendar.getInstance().apply { 
                        timeInMillis = expense.date 
                        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                    }
                    
                    if (targetDayMillis < expenseStartCal.timeInMillis) return@filter false

                    if (expense.recurrence == "MONTHLY") {
                        val targetDayOfMonth = targetDayCal.get(Calendar.DAY_OF_MONTH)
                        val originalDayOfMonth = expenseStartCal.get(Calendar.DAY_OF_MONTH)
                        val maxDaysInTargetMonth = targetDayCal.getActualMaximum(Calendar.DAY_OF_MONTH)
                        val effectiveOriginalDay = if (originalDayOfMonth > maxDaysInTargetMonth) maxDaysInTargetMonth else originalDayOfMonth
                        targetDayOfMonth == effectiveOriginalDay
                    } else {
                        val interval = expense.recurrenceInterval ?: 0
                        if (interval <= 0) {
                            expenseStartCal.timeInMillis == targetDayMillis
                        } else {
                            val diffDays = ((targetDayMillis - expenseStartCal.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()
                            diffDays >= 0 && diffDays % interval == 0
                        }
                    }
                }
            }

            if (selectedDay == null) {
                CalendarLegend()
            } else if (selectedDayExpenses.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.cal_no_events), color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                                    Text(
                                        expense.concept,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    val categoryLabel = stringResource(expense.category.toCategoryStringRes())
                                    Text(
                                        categoryLabel,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                }
                                Text(
                                    expense.amount.toDouble().formatAsCurrency(),
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        ExpenseFormDialog(
            users = expenseState.users,
            errorResId = expenseState.errorResId,
            errorParam = expenseState.errorParam,
            isSaving = expenseState.isAddingExpense,
            existingExpense = null,
            initialDateMillis = initialExpenseDateMillis,
            onDismiss = {
                if (!expenseState.isAddingExpense) {
                    expenseViewModel.clearError()
                    showAddDialog = false
                }
            },
            onConfirm = { amount, concept, category, userId, isShared, recurrence, interval, date ->
                expenseViewModel.addExpense(amount, concept, category, userId, isShared, recurrence, interval, date)
            },
            onDelete = {}
        )
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CalendarLegend() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            stringResource(R.string.cal_legend_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(12.dp))
        
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LegendItem(icon = { Icon(Icons.Default.Star, null, tint = Color(0xFFF9A825), modifier = Modifier.size(16.dp)) }, label = stringResource(R.string.cal_legend_reset))
            LegendItem(color = Color(0xFF2E7D32), label = stringResource(R.string.cal_legend_multi))
            LegendItem(color = Color(0xFF1565C0), label = stringResource(R.string.cal_legend_monthly))
            LegendItem(color = Color(0xFF00838F), label = stringResource(R.string.cal_legend_biweekly))
            LegendItem(color = Color(0xFFC2185B), label = stringResource(R.string.cal_legend_weekly))
            LegendItem(color = Color(0xFF7B1FA2), label = stringResource(R.string.cal_legend_long))
        }
    }
}

@Composable
fun LegendItem(color: Color? = null, icon: (@Composable () -> Unit)? = null, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (icon != null) {
            icon()
        } else if (color != null) {
            Box(Modifier.size(14.dp).background(color, MaterialTheme.shapes.extraSmall))
        }
        Spacer(Modifier.width(8.dp))
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
    }
}


private fun getEventColor(events: List<ExpenseEntity>): Color {
    if (events.size > 1) return Color(0xFF4CAF50) // Green
    val event = events.firstOrNull() ?: return Color.Transparent
    val interval = event.recurrenceInterval ?: 0
    val recurrence = event.recurrence
    
    return when {
        recurrence == "MONTHLY" || interval in 16..31 -> Color(0xFF0C17E1) // Blue
        interval in 8..15 -> Color(0xFF00D4D4) // Cyan
        interval in 1..7 -> Color(0xFFE91E63) // Pink
        interval > 31 -> Color(0xFFB928D2) // Purple
        else -> Color(0xFF1A73E8) // Default primary blue
    }
}
