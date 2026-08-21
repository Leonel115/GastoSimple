package com.app.gastosimple.features.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.gastosimple.core.data.local.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

data class CalendarUiState(
    val eventsByDate: Map<Int, List<CalendarEvent>> = emptyMap(),
    val activePeriod: BudgetPeriodEntity? = null,
    val displayedDate: Calendar = Calendar.getInstance(),
    val isLoading: Boolean = true
)

class CalendarViewModel(
    private val dao: GastoSimpleDao,
    private val getCalendarEventsUseCase: GetCalendarEventsUseCase
) : ViewModel() {
    
    private val _displayedDate = MutableStateFlow(Calendar.getInstance())
    
    val state: StateFlow<CalendarUiState> = combine(
        getCalendarEventsUseCase(),
        dao.getActivePeriod(),
        _displayedDate
    ) { sources, period, displayedDate ->
        val events = projectEventsForMonth(sources, displayedDate)
        CalendarUiState(
            eventsByDate = events.groupBy { 
                val cal = Calendar.getInstance().apply { timeInMillis = it.date }
                cal.get(Calendar.DAY_OF_MONTH)
            },
            activePeriod = period,
            displayedDate = displayedDate,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CalendarUiState()
    )

    private fun projectEventsForMonth(sources: List<CalendarEventSource>, monthCal: Calendar): List<CalendarEvent> {
        val projected = mutableListOf<CalendarEvent>()
        val startOfMonth = (monthCal.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, 1); setMidnight() }
        val endOfMonth = (monthCal.clone() as Calendar).apply { 
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            setMidnight(); set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59)
        }

        sources.forEach { source ->
            val eventCal = Calendar.getInstance().apply { timeInMillis = source.startDate; setMidnight() }
            val interval = source.intervalDays
            
            if (interval <= 0) {
                // Evento único o sin intervalo definido (solo se muestra en su fecha original)
                if (eventCal.timeInMillis in startOfMonth.timeInMillis..endOfMonth.timeInMillis) {
                    projected.add(source.toCalendarEvent(eventCal.timeInMillis))
                }
            } else {
                // Proyectar recurrencias
                while (eventCal.timeInMillis <= endOfMonth.timeInMillis) {
                    if (eventCal.timeInMillis >= startOfMonth.timeInMillis) {
                        projected.add(source.toCalendarEvent(eventCal.timeInMillis))
                    }
                    eventCal.add(Calendar.DAY_OF_YEAR, interval)
                }
            }
        }
        return projected
    }

    private fun Calendar.setMidnight() {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    private fun CalendarEventSource.toCalendarEvent(projectedDate: Long) = CalendarEvent(
        id = this.id,
        date = projectedDate,
        concept = this.concept,
        amount = this.amount,
        type = this.type,
        isEmergency = this.isEmergency
    )

    fun nextMonth() {
        val newDate = (state.value.displayedDate.clone() as Calendar).apply {
            add(Calendar.MONTH, 1)
        }
        _displayedDate.value = newDate
    }

    fun previousMonth() {
        val newDate = (state.value.displayedDate.clone() as Calendar).apply {
            add(Calendar.MONTH, -1)
        }
        _displayedDate.value = newDate
    }
}
