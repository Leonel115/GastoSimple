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
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(viewModel: CalendarViewModel) {
    val state by viewModel.state.collectAsState()
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedDay by remember { mutableStateOf<Int?>(null) }
    
    // Calendar math based on displayedDate
    val displayedCalendar = state.displayedDate
    val monthName = java.text.SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(displayedCalendar.time)
    
    val daysInMonth = displayedCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfMonth = (displayedCalendar.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, 1) }
    val firstDayOfWeek = firstDayOfMonth.get(Calendar.DAY_OF_WEEK) - 1

    // Today normalized to midnight
    val todayMidnight = remember {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
    
    LaunchedEffect(state.displayedDate) {
        selectedDay = null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_calendar)) },
                actions = {
                    IconButton(onClick = { viewModel.previousMonth() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.cal_header_prev))
                    }
                    IconButton(onClick = { viewModel.nextMonth() }) {
                        Icon(Icons.Default.ArrowForward, contentDescription = stringResource(R.string.cal_header_next))
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
            Text(
                text = monthName.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )

            // Calendar Grid
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
                        val eventsForDay = state.eventsByDate[dayNum] ?: emptyList()
                        val hasEvent = eventsForDay.isNotEmpty()
                        val hasEmergency = eventsForDay.any { it.isEmergency }
                        
                        val targetDayCal = (state.displayedDate.clone() as Calendar).apply { 
                            set(Calendar.DAY_OF_MONTH, dayNum)
                            setMidnight()
                        }
                        val isPastDay = targetDayCal.timeInMillis < todayMidnight
                        
                        val isResetDay = state.activePeriod?.let { period ->
                            val periodStartCal = Calendar.getInstance().apply { 
                                timeInMillis = period.startDate; setMidnight() 
                            }
                            if (targetDayCal.timeInMillis < periodStartCal.timeInMillis) return@let false
                            
                            if (period.cycleType == "MENSUAL") {
                                targetDayCal.get(Calendar.DAY_OF_MONTH) == periodStartCal.get(Calendar.DAY_OF_MONTH)
                            } else {
                                val diffDays = ((targetDayCal.timeInMillis - periodStartCal.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()
                                diffDays >= 0 && diffDays % 15 == 0
                            }
                        } ?: false

                        val eventColor = when {
                            hasEmergency -> MaterialTheme.colorScheme.error
                            hasEvent -> getEventColor(eventsForDay)
                            else -> Color.Transparent
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
                                    if (hasEvent) eventColor else Color.LightGray.copy(alpha = 0.3f),
                                    shape = MaterialTheme.shapes.small
                                )
                                .clickable { 
                                    selectedDay = dayNum
                                    if (hasEvent) showBottomSheet = true
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = dayNum.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isPastDay) Color.Gray else MaterialTheme.colorScheme.onSurface
                                )
                                if (isResetDay) {
                                    Icon(Icons.Default.Star, null, tint = Color.Yellow, modifier = Modifier.size(10.dp))
                                } else if (hasEvent) {
                                    Box(Modifier.size(4.dp).background(eventColor, MaterialTheme.shapes.extraSmall))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            CalendarLegend()
        }
    }

    if (showBottomSheet && selectedDay != null) {
        val events = state.eventsByDate[selectedDay!!] ?: emptyList()
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text(
                    text = stringResource(R.string.cal_details_title) + " ($selectedDay)",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(16.dp))
                
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(events) { event ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (event.isEmergency) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text(event.concept, fontWeight = FontWeight.Bold)
                                    val typeLabel = when(event.type) {
                                        CalendarEventType.RECURRING -> stringResource(R.string.one_time_expense)
                                        CalendarEventType.INSTALLMENT -> stringResource(R.string.total_paid) // Reusing existing strings or add new
                                        CalendarEventType.EMERGENCY -> stringResource(R.string.emergency_tag)
                                    }
                                    Text(typeLabel, style = MaterialTheme.typography.labelSmall)
                                }
                                Text("$${event.amount}", color = if (event.isEmergency) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun Calendar.setMidnight() {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}

private fun getEventColor(events: List<CalendarEvent>): Color {
    if (events.any { it.isEmergency }) return Color.Red
    if (events.size > 1) return Color(0xFF4CAF50)
    val event = events.firstOrNull() ?: return Color.Transparent
    return when(event.type) {
        CalendarEventType.INSTALLMENT -> Color(0xFFB928D2) // Purple for installments
        else -> Color(0xFF1A73E8) // Blue for regular recurring
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CalendarLegend() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(stringResource(R.string.cal_legend_title), style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(12.dp))
        
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LegendItem(icon = { Icon(Icons.Default.Star, null, tint = Color.Yellow, modifier = Modifier.size(16.dp)) }, label = stringResource(R.string.cal_legend_reset))
            LegendItem(color = Color.Red, label = stringResource(R.string.emergency_tag))
            LegendItem(color = Color(0xFF4CAF50), label = stringResource(R.string.cal_legend_multi))
            LegendItem(color = Color(0xFFB928D2), label = stringResource(R.string.installments_title))
            LegendItem(color = Color(0xFF1A73E8), label = stringResource(R.string.one_time_expense))
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
        Text(label, style = MaterialTheme.typography.bodySmall)
    }
}
