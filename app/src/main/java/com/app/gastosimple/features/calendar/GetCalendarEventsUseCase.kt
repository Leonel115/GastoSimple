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
     * Retorna una lista unificada de fuentes de eventos:
     * - Pagos reales (históricos) grabados en la tabla expenses.
     * - Plantillas de gastos recurrentes.
     * - Compromisos por cuotas ACTIVOS exclusivamente.
     */
    operator fun invoke(): Flow<List<CalendarEventSource>> = combine(
        dao.getAllExpenses(),
        dao.getRecurringExpenses(),
        dao.getActiveInstallments()
    ) { allExpenses, recurringExpenses, activeInstallments ->
        val realPaymentSources = allExpenses.map { expense ->
            CalendarEventSource(
                id = expense.id,
                concept = expense.concept,
                amount = expense.amount,
                startDate = expense.date,
                intervalDays = 0, // No proyecta hacia el futuro, es un evento real
                type = if (expense.installmentId != null) CalendarEventType.INSTALLMENT else CalendarEventType.RECURRING,
                isEmergency = expense.isEmergency
            )
        }

        val recurringSources = recurringExpenses.map { expense ->
            CalendarEventSource(
                id = expense.id,
                concept = expense.concept,
                amount = expense.amount,
                startDate = expense.date,
                intervalDays = expense.recurrenceInterval ?: 0,
                type = CalendarEventType.RECURRING,
                isEmergency = expense.isEmergency
            )
        }

        val activeInstallmentSources = activeInstallments.map { installment ->
            CalendarEventSource(
                id = installment.id,
                concept = installment.description,
                amount = installment.totalAmount.divide(installment.totalInstallments.toBigDecimal(), 2, java.math.RoundingMode.HALF_UP),
                startDate = installment.startDate,
                intervalDays = if (installment.frequency == InstallmentFrequency.BIWEEKLY) 15 else 30,
                type = CalendarEventType.INSTALLMENT,
                isEmergency = installment.isEmergency,
                totalInstallments = installment.totalInstallments
            )
        }

        realPaymentSources + recurringSources + activeInstallmentSources
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
