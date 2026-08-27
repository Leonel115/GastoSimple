package com.app.gastosimple.features.dashboard.domain

/**
 * Modos de filtrado temporal para la pantalla de Dashboard (HU-06).
 *
 * - [MONTHLY]: Filtra por un mes y año específicos (Ciclo/Mes).
 * - [ANNUAL]: Filtra de forma acumulada para un año específico.
 * - [TOTAL]: Muestra el acumulado histórico total.
 */
enum class DashboardFilterMode {
    MONTHLY,
    ANNUAL,
    TOTAL
}
