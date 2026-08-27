package com.app.gastosimple.core.data.debug

import com.app.gastosimple.core.data.local.BudgetPeriodEntity
import com.app.gastosimple.core.data.local.ExpenseEntity
import com.app.gastosimple.core.data.local.GastoSimpleDao
import com.app.gastosimple.core.data.local.UserEntity
import java.time.LocalDate
import java.time.ZoneId

/**
 * Módulo aislado de generación de datos de prueba (Mock Data Seeder).
 * HU-06: Permite probar visual y funcionalmente el filtrado temporal por meses, años (2024..2026) y totales.
 *
 * NOTA: Este archivo es completamente desacoplado y puede ser deshabilitado mediante [ENABLE_MOCK_DATA]
 * o eliminado sin impactar la arquitectura principal.
 */
object MockDataSeeder {

    /**
     * Bandera para habilitar o deshabilitar la siembra de datos de prueba.
     */
    const val ENABLE_MOCK_DATA: Boolean = true

    /**
     * Puebla la base de datos con presupuestos y transacciones de prueba si la base de datos está vacía.
     *
     * @param dao Instancia del DAO de Room para operaciones directas de persistencia.
     * @param force Si es true, omite la validación de base de datos vacía.
     */
    suspend fun seedDatabaseIfNeeded(dao: GastoSimpleDao, force: Boolean = false) {
        if (!ENABLE_MOCK_DATA && !force) return

        val existingRecordsCount = dao.getTotalRecordCount()
        if (existingRecordsCount > 0 && !force) return

        val defaultUser = UserEntity(
            name = "Usuario Principal",
            contributionPercentage = 100.0
        )
        dao.insertUser(defaultUser)

        seed2024Data(dao)
        seed2025Data(dao)
        seed2026Data(dao)
    }

    private suspend fun seed2024Data(dao: GastoSimpleDao) {
        // Noviembre 2024
        val nov2024Start = toEpochMillis(2024, 11, 1)
        val nov2024End = toEpochMillis(2024, 11, 30, 23, 59)
        val novPeriodId = dao.insertPeriod(
            BudgetPeriodEntity(
                totalBudget = "1200.00",
                startDate = nov2024Start,
                endDate = nov2024End,
                cycleType = "MENSUAL",
                isActive = false
            )
        )
        dao.insertExpense(ExpenseEntity(amount = "500.00", concept = "Renta Depto", category = "Alquiler", userId = 1, date = toEpochMillis(2024, 11, 5), recurrence = "NONE", periodId = novPeriodId))
        dao.insertExpense(ExpenseEntity(amount = "280.50", concept = "Supermercado Mensual", category = "Alimentación", userId = 1, date = toEpochMillis(2024, 11, 12), recurrence = "NONE", periodId = novPeriodId))
        dao.insertExpense(ExpenseEntity(amount = "95.00", concept = "Luz y Gas", category = "Servicios", userId = 1, date = toEpochMillis(2024, 11, 18), recurrence = "NONE", periodId = novPeriodId))
        dao.insertExpense(ExpenseEntity(amount = "15.99", concept = "Spotify Familiar", category = "Suscripciones", userId = 1, date = toEpochMillis(2024, 11, 20), recurrence = "NONE", periodId = novPeriodId))

        // Diciembre 2024
        val dec2024Start = toEpochMillis(2024, 12, 1)
        val dec2024End = toEpochMillis(2024, 12, 31, 23, 59)
        val decPeriodId = dao.insertPeriod(
            BudgetPeriodEntity(
                totalBudget = "1500.00",
                startDate = dec2024Start,
                endDate = dec2024End,
                cycleType = "MENSUAL",
                isActive = false
            )
        )
        dao.insertExpense(ExpenseEntity(amount = "500.00", concept = "Renta Depto", category = "Alquiler", userId = 1, date = toEpochMillis(2024, 12, 5), recurrence = "NONE", periodId = decPeriodId))
        dao.insertExpense(ExpenseEntity(amount = "420.00", concept = "Cena y Compras Navideñas", category = "Alimentación", userId = 1, date = toEpochMillis(2024, 12, 23), recurrence = "NONE", periodId = decPeriodId))
        dao.insertExpense(ExpenseEntity(amount = "310.00", concept = "Regalos y Ropa", category = "Otros", userId = 1, date = toEpochMillis(2024, 12, 24), recurrence = "NONE", periodId = decPeriodId))
        dao.insertExpense(ExpenseEntity(amount = "110.00", concept = "Internet y Electricidad", category = "Servicios", userId = 1, date = toEpochMillis(2024, 12, 28), recurrence = "NONE", periodId = decPeriodId))
    }

    private suspend fun seed2025Data(dao: GastoSimpleDao) {
        // Marzo 2025
        val mar2025Start = toEpochMillis(2025, 3, 1)
        val mar2025End = toEpochMillis(2025, 3, 31, 23, 59)
        val marPeriodId = dao.insertPeriod(
            BudgetPeriodEntity(
                totalBudget = "1400.00",
                startDate = mar2025Start,
                endDate = mar2025End,
                cycleType = "MENSUAL",
                isActive = false
            )
        )
        dao.insertExpense(ExpenseEntity(amount = "550.00", concept = "Alquiler", category = "Alquiler", userId = 1, date = toEpochMillis(2025, 3, 3), recurrence = "NONE", periodId = marPeriodId))
        dao.insertExpense(ExpenseEntity(amount = "340.00", concept = "Alimentos y Verdulería", category = "Alimentación", userId = 1, date = toEpochMillis(2025, 3, 15), recurrence = "NONE", periodId = marPeriodId))
        dao.insertExpense(ExpenseEntity(amount = "80.00", concept = "Telefonía", category = "Servicios", userId = 1, date = toEpochMillis(2025, 3, 20), recurrence = "NONE", periodId = marPeriodId))
        dao.insertExpense(ExpenseEntity(amount = "25.00", concept = "Streaming Video", category = "Suscripciones", userId = 1, date = toEpochMillis(2025, 3, 22), recurrence = "NONE", periodId = marPeriodId))

        // Octubre 2025
        val oct2025Start = toEpochMillis(2025, 10, 1)
        val oct2025End = toEpochMillis(2025, 10, 31, 23, 59)
        val octPeriodId = dao.insertPeriod(
            BudgetPeriodEntity(
                totalBudget = "1450.00",
                startDate = oct2025Start,
                endDate = oct2025End,
                cycleType = "MENSUAL",
                isActive = false
            )
        )
        dao.insertExpense(ExpenseEntity(amount = "550.00", concept = "Alquiler", category = "Alquiler", userId = 1, date = toEpochMillis(2025, 10, 5), recurrence = "NONE", periodId = octPeriodId))
        dao.insertExpense(ExpenseEntity(amount = "390.00", concept = "Despensa", category = "Alimentación", userId = 1, date = toEpochMillis(2025, 10, 14), recurrence = "NONE", periodId = octPeriodId))
        dao.insertExpense(ExpenseEntity(amount = "140.00", concept = "Mantenimiento Hogar", category = "Otros", userId = 1, date = toEpochMillis(2025, 10, 26), recurrence = "NONE", periodId = octPeriodId))
    }

    private suspend fun seed2026Data(dao: GastoSimpleDao) {
        // Abril 2026 (Periodo Pasado)
        val apr2026Start = toEpochMillis(2026, 4, 1)
        val apr2026End = toEpochMillis(2026, 4, 30, 23, 59)
        val aprPeriodId = dao.insertPeriod(
            BudgetPeriodEntity(
                totalBudget = "1500.00",
                startDate = apr2026Start,
                endDate = apr2026End,
                cycleType = "MENSUAL",
                isActive = false
            )
        )
        dao.insertExpense(ExpenseEntity(amount = "600.00", concept = "Alquiler Departamento", category = "Alquiler", userId = 1, date = toEpochMillis(2026, 4, 2), recurrence = "NONE", periodId = aprPeriodId))
        dao.insertExpense(ExpenseEntity(amount = "420.00", concept = "Supermercado Mensual", category = "Alimentación", userId = 1, date = toEpochMillis(2026, 4, 10), recurrence = "NONE", periodId = aprPeriodId))
        dao.insertExpense(ExpenseEntity(amount = "120.00", concept = "Servicios Básicos", category = "Servicios", userId = 1, date = toEpochMillis(2026, 4, 18), recurrence = "NONE", periodId = aprPeriodId))
        dao.insertExpense(ExpenseEntity(amount = "29.99", concept = "Suscripciones Varias", category = "Suscripciones", userId = 1, date = toEpochMillis(2026, 4, 25), recurrence = "NONE", periodId = aprPeriodId))

        // Agosto 2026 (Periodo Actual Activo)
        val aug2026Start = toEpochMillis(2026, 8, 1)
        val aug2026End = toEpochMillis(2026, 8, 31, 23, 59)
        val augPeriodId = dao.insertPeriod(
            BudgetPeriodEntity(
                totalBudget = "1500.00",
                startDate = aug2026Start,
                endDate = aug2026End,
                cycleType = "MENSUAL",
                isActive = true
            )
        )
        dao.insertExpense(ExpenseEntity(amount = "363.69", concept = "Compras de Supermercado", category = "Alimentación", userId = 1, date = toEpochMillis(2026, 8, 10), recurrence = "NONE", periodId = augPeriodId))
        dao.insertExpense(ExpenseEntity(amount = "500.00", concept = "Mantenimiento Vehículo", category = "Otros", userId = 1, date = toEpochMillis(2026, 8, 15), recurrence = "NONE", periodId = augPeriodId))
    }

    private fun toEpochMillis(year: Int, month: Int, day: Int, hour: Int = 12, minute: Int = 0): Long {
        return LocalDate.of(year, month, day)
            .atTime(hour, minute)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }
}
