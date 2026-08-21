package com.app.gastosimple.features.calendar

import java.math.BigDecimal

enum class CalendarEventType {
    RECURRING,
    INSTALLMENT,
    EMERGENCY
}

data class CalendarEvent(
    val id: Long,
    val date: Long, // Fecha de la ocurrencia (proyectada)
    val concept: String,
    val amount: BigDecimal,
    val type: CalendarEventType,
    val isEmergency: Boolean = false
)
