package com.app.gastosimple.features.dashboard.domain

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId

/**
 * Representa un rango de tiempo en milisegundos (epoch timestamps).
 */
data class DateRange(
    val startMillis: Long,
    val endMillis: Long
)

/**
 * Calculador puro de rangos temporales para el Dashboard (HU-06).
 * Aplica cláusulas de guarda (fail-fast) y SRP conforme a principles.md.
 */
object DateRangeCalculator {

    private const val MIN_VALID_YEAR = 2000
    private const val MAX_VALID_YEAR = 2100
    private const val MIN_MONTH = 1
    private const val MAX_MONTH = 12

    /**
     * Calcula el rango de inicio y fin de un mes en milisegundos.
     *
     * @param year Año calendario (ej. 2026).
     * @param month Mes base 1 (1 = Enero, 12 = Diciembre).
     * @param zoneId Zona horaria a utilizar (por defecto la del sistema).
     * @return [DateRange] con timestamps de inicio y fin.
     */
    fun calculateMonthRange(
        year: Int,
        month: Int,
        zoneId: ZoneId = ZoneId.systemDefault()
    ): DateRange {
        require(year in MIN_VALID_YEAR..MAX_VALID_YEAR) {
            "El año debe estar entre $MIN_VALID_YEAR y $MAX_VALID_YEAR"
        }
        require(month in MIN_MONTH..MAX_MONTH) {
            "El mes debe estar entre $MIN_MONTH y $MAX_MONTH"
        }

        val yearMonth = YearMonth.of(year, month)
        val startDateTime = yearMonth.atDay(1).atStartOfDay()
        val endDateTime = yearMonth.atEndOfMonth().atTime(LocalTime.MAX)

        val startMillis = startDateTime.atZone(zoneId).toInstant().toEpochMilli()
        val endMillis = endDateTime.atZone(zoneId).toInstant().toEpochMilli()

        return DateRange(startMillis = startMillis, endMillis = endMillis)
    }

    /**
     * Calcula el rango de inicio y fin de un año calendario en milisegundos.
     *
     * @param year Año calendario (ej. 2026).
     * @param zoneId Zona horaria a utilizar (por defecto la del sistema).
     * @return [DateRange] con timestamps del 1 de enero al 31 de diciembre.
     */
    fun calculateYearRange(
        year: Int,
        zoneId: ZoneId = ZoneId.systemDefault()
    ): DateRange {
        require(year in MIN_VALID_YEAR..MAX_VALID_YEAR) {
            "El año debe estar entre $MIN_VALID_YEAR y $MAX_VALID_YEAR"
        }

        val startDateTime = LocalDateTime.of(year, 1, 1, 0, 0, 0, 0)
        val endDateTime = LocalDateTime.of(year, 12, 31, 23, 59, 59, 999_000_000)

        val startMillis = startDateTime.atZone(zoneId).toInstant().toEpochMilli()
        val endMillis = endDateTime.atZone(zoneId).toInstant().toEpochMilli()

        return DateRange(startMillis = startMillis, endMillis = endMillis)
    }

    /**
     * Determina si el periodo consultado es anterior al periodo actual del sistema.
     *
     * @param filterMode Modo de filtrado activo ([DashboardFilterMode]).
     * @param selectedYear Año seleccionado.
     * @param selectedMonth Mes seleccionado (1..12).
     * @param referenceDate Fecha de referencia (por defecto la fecha actual del sistema).
     * @return true si el periodo ya finalizó, false en caso contrario.
     */
    fun isPastPeriod(
        filterMode: DashboardFilterMode,
        selectedYear: Int,
        selectedMonth: Int,
        referenceDate: LocalDate = LocalDate.now()
    ): Boolean {
        return when (filterMode) {
            DashboardFilterMode.MONTHLY -> {
                selectedYear < referenceDate.year ||
                    (selectedYear == referenceDate.year && selectedMonth < referenceDate.monthValue)
            }
            DashboardFilterMode.ANNUAL -> {
                selectedYear < referenceDate.year
            }
            DashboardFilterMode.TOTAL -> false
        }
    }

    /**
     * Extrae la lista de años únicos a partir de los timestamps registrados en la base de datos.
     *
     * @param timestamps Lista de timestamps en milisegundos.
     * @param defaultYears Años base en caso de lista vacía.
     * @param zoneId Zona horaria de referencia.
     * @return Lista ordenada ascendentemente de años disponibles.
     */
    fun extractAvailableYears(
        timestamps: List<Long>,
        defaultYears: List<Int> = listOf(2024, 2025, 2026),
        zoneId: ZoneId = ZoneId.systemDefault()
    ): List<Int> {
        val extractedYears = timestamps.map { millis ->
            Instant.ofEpochMilli(millis).atZone(zoneId).year
        }
        val currentYear = LocalDate.now(zoneId).year
        return (extractedYears + defaultYears + currentYear).distinct().sorted()
    }
}
