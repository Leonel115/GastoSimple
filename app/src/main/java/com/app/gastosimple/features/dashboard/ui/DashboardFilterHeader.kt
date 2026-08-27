package com.app.gastosimple.features.dashboard.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.gastosimple.R
import com.app.gastosimple.core.ui.theme.GastoSimpleTheme
import com.app.gastosimple.features.dashboard.domain.DashboardFilterMode
import java.text.DateFormatSymbols
import java.util.Locale

/**
 * Constantes de dimensionamiento para la cabecera de filtros del Dashboard.
 * Previene el uso de 'magic numbers' conforme a principles.md / continuerules.md.
 */
object DashboardFilterDimens {
    val HeaderHorizontalPadding: Dp = 16.dp
    val HeaderTopPadding: Dp = 8.dp
    val HeaderBottomSpacing: Dp = 12.dp
    val SectionSpacing: Dp = 8.dp
    val IconSize: Dp = 20.dp
    val DropdownCornerRadius: Dp = 12.dp
    val TabPaddingVertical: Dp = 6.dp
    val TabPaddingHorizontal: Dp = 4.dp
    val ArrowSpacing: Dp = 1.dp
    val MenuElevation: Dp = 4.dp
    const val MonthSlotWeight: Float = 1.85f
    const val AnnualSlotWeight: Float = 1.0f
    const val TotalSlotWeight: Float = 0.95f
}

/**
 * Cabecera completa del Dashboard que incluye la barra de título con selector de año
 * y la fila de filtros temporales (Mes, Anual, Total).
 *
 * @param selectedFilterMode Modo de filtrado activo actual ([DashboardFilterMode]).
 * @param selectedMonth Mes seleccionado en base 1 (1 = Enero, 12 = Diciembre).
 * @param selectedYear Año seleccionado.
 * @param availableYears Lista de años con transacciones o disponibles para consulta.
 * @param onFilterModeSelected Callback cuando cambia el modo de filtrado.
 * @param onMonthSelected Callback cuando se selecciona un nuevo mes.
 * @param onYearSelected Callback cuando se selecciona un nuevo año.
 * @param modifier Modificador de diseño Compose.
 */
@Composable
fun DashboardHeader(
    selectedFilterMode: DashboardFilterMode,
    selectedMonth: Int,
    selectedYear: Int,
    availableYears: List<Int>,
    onFilterModeSelected: (DashboardFilterMode) -> Unit,
    onMonthSelected: (Int) -> Unit,
    onYearSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = DashboardFilterDimens.HeaderHorizontalPadding,
                end = DashboardFilterDimens.HeaderHorizontalPadding,
                top = DashboardFilterDimens.HeaderTopPadding,
                bottom = DashboardFilterDimens.HeaderBottomSpacing
            ),
        verticalArrangement = Arrangement.spacedBy(DashboardFilterDimens.SectionSpacing)
    ) {
        // 1. Título principal "Dashboard" + Selector de Año a la derecha
        DashboardTopBar(
            selectedYear = selectedYear,
            availableYears = availableYears,
            onYearSelected = onYearSelected
        )

        // 2. Fila de Filtros: [Mes ▼] | [Anual] | [Total]
        DashboardFilterTabsRow(
            selectedFilterMode = selectedFilterMode,
            selectedMonth = selectedMonth,
            onFilterModeSelected = onFilterModeSelected,
            onMonthSelected = onMonthSelected
        )
    }
}

/**
 * Fila superior con el título de la pantalla y el dropdown de selección de año.
 */
@Composable
fun DashboardTopBar(
    selectedYear: Int,
    availableYears: List<Int>,
    onYearSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.nav_dashboard),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        YearDropdownSelector(
            selectedYear = selectedYear,
            availableYears = availableYears,
            onYearSelected = onYearSelected
        )
    }
}

/**
 * Selector desplegable de año con estilo visual integrado.
 */
@Composable
fun YearDropdownSelector(
    selectedYear: Int,
    availableYears: List<Int>,
    onYearSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(DashboardFilterDimens.DropdownCornerRadius))
                .clickable { expanded = true }
                .padding(
                    horizontal = DashboardFilterDimens.TabPaddingHorizontal,
                    vertical = DashboardFilterDimens.TabPaddingVertical
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedYear.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(DashboardFilterDimens.ArrowSpacing))
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = stringResource(R.string.dashboard_year_content_desc),
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(DashboardFilterDimens.IconSize)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            availableYears.forEach { year ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = year.toString(),
                            fontWeight = if (year == selectedYear) FontWeight.Bold else FontWeight.Normal,
                            color = if (year == selectedYear) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = {
                        onYearSelected(year)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * Fila con opciones de filtro temporal: Mes (dropdown), Anual y Total.
 * Los items se encuentran indexados mediante slots proporcionales para garantizar que
 * la posición de "Anual" y "Total" sea fija y no varíe según la longitud del nombre del mes.
 */
@Composable
fun DashboardFilterTabsRow(
    selectedFilterMode: DashboardFilterMode,
    selectedMonth: Int,
    onFilterModeSelected: (DashboardFilterMode) -> Unit,
    onMonthSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Tab / Dropdown de Mes (Alineado a la izquierda dentro de su slot indexado)
        Box(
            modifier = Modifier.weight(DashboardFilterDimens.MonthSlotWeight),
            contentAlignment = Alignment.CenterStart
        ) {
            MonthDropdownSelector(
                selectedMonth = selectedMonth,
                isActive = selectedFilterMode == DashboardFilterMode.MONTHLY,
                onMonthSelected = { month ->
                    onMonthSelected(month)
                    onFilterModeSelected(DashboardFilterMode.MONTHLY)
                },
                onActivateMonthlyMode = {
                    onFilterModeSelected(DashboardFilterMode.MONTHLY)
                }
            )
        }

        // Tab Anual (Indexado fijamente en su columna, inmune a la longitud del mes)
        Box(
            modifier = Modifier.weight(DashboardFilterDimens.AnnualSlotWeight),
            contentAlignment = Alignment.Center
        ) {
            FilterTabItem(
                text = stringResource(R.string.dashboard_filter_annual),
                isSelected = selectedFilterMode == DashboardFilterMode.ANNUAL,
                onClick = { onFilterModeSelected(DashboardFilterMode.ANNUAL) }
            )
        }

        // Tab Total (Indexado y alineado a la derecha en su columna)
        Box(
            modifier = Modifier.weight(DashboardFilterDimens.TotalSlotWeight),
            contentAlignment = Alignment.CenterEnd
        ) {
            FilterTabItem(
                text = stringResource(R.string.dashboard_filter_total),
                isSelected = selectedFilterMode == DashboardFilterMode.TOTAL,
                onClick = { onFilterModeSelected(DashboardFilterMode.TOTAL) }
            )
        }
    }
}

/**
 * Selector desplegable de mes (1 = Enero a 12 = Diciembre) con nombre capitalizado.
 */
@Composable
fun MonthDropdownSelector(
    selectedMonth: Int,
    isActive: Boolean,
    onMonthSelected: (Int) -> Unit,
    onActivateMonthlyMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val monthName = remember(selectedMonth) { getLocalizedMonthName(selectedMonth) }

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(DashboardFilterDimens.DropdownCornerRadius))
                .clickable {
                    onActivateMonthlyMode()
                    expanded = true
                }
                .padding(
                    horizontal = DashboardFilterDimens.TabPaddingHorizontal,
                    vertical = DashboardFilterDimens.TabPaddingVertical
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = monthName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                color = if (isActive) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                maxLines = 1,
                softWrap = false
            )
            Spacer(modifier = Modifier.width(DashboardFilterDimens.ArrowSpacing))
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = stringResource(R.string.dashboard_month_content_desc),
                tint = if (isActive) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.size(DashboardFilterDimens.IconSize)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            (1..12).forEach { monthIndex ->
                val name = getLocalizedMonthName(monthIndex)
                DropdownMenuItem(
                    text = {
                        Text(
                            text = name,
                            fontWeight = if (monthIndex == selectedMonth) FontWeight.Bold else FontWeight.Normal,
                            color = if (monthIndex == selectedMonth) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = {
                        onMonthSelected(monthIndex)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * Elemento de tab de filtro individual para opciones como "Anual" y "Total".
 */
@Composable
fun FilterTabItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
        color = if (isSelected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
        maxLines = 1,
        softWrap = false,
        modifier = modifier
            .clip(RoundedCornerShape(DashboardFilterDimens.DropdownCornerRadius))
            .clickable(onClick = onClick)
            .padding(
                horizontal = DashboardFilterDimens.TabPaddingHorizontal,
                vertical = DashboardFilterDimens.TabPaddingVertical
            )
    )
}

/**
 * Obtiene el nombre del mes localizado y capitalizado para el índice dado (1..12).
 */
private fun getLocalizedMonthName(month: Int): String {
    return try {
        val symbols = DateFormatSymbols(Locale.getDefault())
        val name = symbols.months[month - 1]
        name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    } catch (_: Exception) {
        "Mes $month"
    }
}

// -------------------------------------------------------------------------
// Previews de Compose
// -------------------------------------------------------------------------

@Preview(name = "Dashboard Header - Noviembre (Long Month)", showBackground = true, backgroundColor = 0xFF0B132B)
@Composable
private fun DashboardHeaderNoviembrePreview() {
    GastoSimpleTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            DashboardHeader(
                selectedFilterMode = DashboardFilterMode.MONTHLY,
                selectedMonth = 11,
                selectedYear = 2026,
                availableYears = listOf(2024, 2025, 2026),
                onFilterModeSelected = {},
                onMonthSelected = {},
                onYearSelected = {}
            )
        }
    }
}

@Preview(name = "Dashboard Header - Septiembre (Long Month)", showBackground = true, backgroundColor = 0xFF0B132B)
@Composable
private fun DashboardHeaderSeptiembrePreview() {
    GastoSimpleTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            DashboardHeader(
                selectedFilterMode = DashboardFilterMode.MONTHLY,
                selectedMonth = 9,
                selectedYear = 2026,
                availableYears = listOf(2024, 2025, 2026),
                onFilterModeSelected = {},
                onMonthSelected = {},
                onYearSelected = {}
            )
        }
    }
}

@Preview(name = "Dashboard Header - Abril (Short Month)", showBackground = true, backgroundColor = 0xFF0B132B)
@Composable
private fun DashboardHeaderAbrilPreview() {
    GastoSimpleTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            DashboardHeader(
                selectedFilterMode = DashboardFilterMode.MONTHLY,
                selectedMonth = 4,
                selectedYear = 2026,
                availableYears = listOf(2024, 2025, 2026),
                onFilterModeSelected = {},
                onMonthSelected = {},
                onYearSelected = {}
            )
        }
    }
}

@Preview(name = "Dashboard Header - Annual Dark", showBackground = true, backgroundColor = 0xFF0B132B)
@Composable
private fun DashboardHeaderAnnualPreview() {
    GastoSimpleTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            DashboardHeader(
                selectedFilterMode = DashboardFilterMode.ANNUAL,
                selectedMonth = 8,
                selectedYear = 2026,
                availableYears = listOf(2024, 2025, 2026),
                onFilterModeSelected = {},
                onMonthSelected = {},
                onYearSelected = {}
            )
        }
    }
}

