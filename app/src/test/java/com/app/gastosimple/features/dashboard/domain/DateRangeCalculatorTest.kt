package com.app.gastosimple.features.dashboard.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

/**
 * Pruebas unitarias para DateRangeCalculator.
 * Verifica la precisión de rangos temporales, manejo de fechas pasadas y cláusulas de guarda.
 */
class DateRangeCalculatorTest {

    private val zoneId = ZoneId.of("UTC")
    private val fixedReferenceDate = LocalDate.of(2026, 8, 26)

    @Test
    fun `calculateMonthRange generates correct start and end epoch millis for August 2026`() {
        val range = DateRangeCalculator.calculateMonthRange(2026, 8, zoneId)
        
        // Aug 1, 2026 00:00:00 UTC = 1785542400000 ms
        val expectedStart = LocalDate.of(2026, 8, 1).atStartOfDay(zoneId).toInstant().toEpochMilli()
        // Aug 31, 2026 23:59:59.999 UTC
        val expectedEnd = LocalDate.of(2026, 8, 31).atTime(23, 59, 59, 999_000_000).atZone(zoneId).toInstant().toEpochMilli()

        assertEquals(expectedStart, range.startMillis)
        assertEquals(expectedEnd, range.endMillis)
        assertTrue(range.startMillis < range.endMillis)
    }

    @Test
    fun `calculateYearRange generates correct start and end epoch millis for 2025`() {
        val range = DateRangeCalculator.calculateYearRange(2025, zoneId)

        val expectedStart = LocalDate.of(2025, 1, 1).atStartOfDay(zoneId).toInstant().toEpochMilli()
        val expectedEnd = LocalDate.of(2025, 12, 31).atTime(23, 59, 59, 999_000_000).atZone(zoneId).toInstant().toEpochMilli()

        assertEquals(expectedStart, range.startMillis)
        assertEquals(expectedEnd, range.endMillis)
        assertTrue(range.startMillis < range.endMillis)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `calculateMonthRange throws on invalid month`() {
        DateRangeCalculator.calculateMonthRange(2026, 13)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `calculateYearRange throws on invalid year`() {
        DateRangeCalculator.calculateYearRange(1990)
    }

    @Test
    fun `isPastPeriod correctly identifies past months`() {
        // Month before reference month (July 2026 < Aug 2026) -> true
        assertTrue(
            DateRangeCalculator.isPastPeriod(
                filterMode = DashboardFilterMode.MONTHLY,
                selectedYear = 2026,
                selectedMonth = 7,
                referenceDate = fixedReferenceDate
            )
        )

        // Previous year (Dec 2025 < Aug 2026) -> true
        assertTrue(
            DateRangeCalculator.isPastPeriod(
                filterMode = DashboardFilterMode.MONTHLY,
                selectedYear = 2025,
                selectedMonth = 12,
                referenceDate = fixedReferenceDate
            )
        )

        // Current month (Aug 2026 == Aug 2026) -> false
        assertFalse(
            DateRangeCalculator.isPastPeriod(
                filterMode = DashboardFilterMode.MONTHLY,
                selectedYear = 2026,
                selectedMonth = 8,
                referenceDate = fixedReferenceDate
            )
        )

        // Future month (Sep 2026 > Aug 2026) -> false
        assertFalse(
            DateRangeCalculator.isPastPeriod(
                filterMode = DashboardFilterMode.MONTHLY,
                selectedYear = 2026,
                selectedMonth = 9,
                referenceDate = fixedReferenceDate
            )
        )
    }

    @Test
    fun `isPastPeriod correctly identifies past years`() {
        // 2025 < 2026 -> true
        assertTrue(
            DateRangeCalculator.isPastPeriod(
                filterMode = DashboardFilterMode.ANNUAL,
                selectedYear = 2025,
                selectedMonth = 1,
                referenceDate = fixedReferenceDate
            )
        )

        // 2026 == 2026 -> false
        assertFalse(
            DateRangeCalculator.isPastPeriod(
                filterMode = DashboardFilterMode.ANNUAL,
                selectedYear = 2026,
                selectedMonth = 1,
                referenceDate = fixedReferenceDate
            )
        )
    }

    @Test
    fun `isPastPeriod returns false for TOTAL mode`() {
        assertFalse(
            DateRangeCalculator.isPastPeriod(
                filterMode = DashboardFilterMode.TOTAL,
                selectedYear = 2020,
                selectedMonth = 1,
                referenceDate = fixedReferenceDate
            )
        )
    }

    @Test
    fun `extractAvailableYears merges timestamps with defaults and current year`() {
        val timestamps = listOf(
            LocalDate.of(2023, 5, 10).atStartOfDay(zoneId).toInstant().toEpochMilli(),
            LocalDate.of(2025, 2, 20).atStartOfDay(zoneId).toInstant().toEpochMilli()
        )

        val years = DateRangeCalculator.extractAvailableYears(
            timestamps = timestamps,
            defaultYears = listOf(2024, 2025, 2026),
            zoneId = zoneId
        )

        assertEquals(listOf(2023, 2024, 2025, 2026), years)
    }
}
