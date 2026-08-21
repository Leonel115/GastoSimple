package com.app.gastosimple.features.calendar

import com.app.gastosimple.core.data.local.GastoSimpleDao
import com.app.gastosimple.core.data.local.InstallmentFrequency
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import java.math.BigDecimal

class GetCalendarEventsUseCase(private val dao: GastoSimpleDao) {

    /**
     * Retorna una lista unificada de fuentes de eventos.
     * La proyección de cada instancia se realiza en la UI o ViewModel
     * para evitar generar listas infinitas en el Flow.
     */
    operator fun invoke(): Flow<List<CalendarEventSource>> = combine(
        dao.getRecurringExpenses(),
        dao.getActiveInstallments()
    ) { expenses, installments ->
        val expenseEvents = expenses.map { 
            CalendarEventSource(
                id = it.id,
                concept = it.concept,
                amount = it.amount,
                startDate = it.date,
                intervalDays = it.recurrenceInterval ?: 0,
                type = CalendarEventType.RECURRING,
                isEmergency = it.isEmergency
            )
        }

        val installmentEvents = installments.map {
            CalendarEventSource(
                id = it.id,
                concept = it.description,
                amount = it.totalAmount.divide(it.totalInstallments.toBigDecimal(), 2, java.math.RoundingMode.HALF_UP),
                startDate = it.startDate,
                intervalDays = if (it.frequency == InstallmentFrequency.BIWEEKLY) 15 else 30,
                type = CalendarEventType.INSTALLMENT,
                isEmergency = it.isEmergency,
                totalInstallments = it.totalInstallments
            )
        }

        expenseEvents + installmentEvents
    }.flowOn(Dispatchers.IO)
}

/**
 * Representa una fuente de eventos (ej. un gasto que se repite).
 * Contiene la lógica necesaria para proyectar sus ocurrencias.
 */
data class CalendarEventSource(
    val id: Long,
    val concept: String,
    val amount: BigDecimal,
    val startDate: Long,
    val intervalDays: Int,
    val type: CalendarEventType,
    val isEmergency: Boolean,
    val totalInstallments: Int? = null // Solo para cuotas
)
