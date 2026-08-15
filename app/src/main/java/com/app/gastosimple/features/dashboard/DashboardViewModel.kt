package com.app.gastosimple.features.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.gastosimple.features.dashboard.domain.BudgetProgressUiState
import com.app.gastosimple.features.dashboard.domain.GetBudgetProgressUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel para el Dashboard.
 * Gestiona el estado del progreso del presupuesto siguiendo la HU-06.
 */
class DashboardViewModel(
    getBudgetProgressUseCase: GetBudgetProgressUseCase
) : ViewModel() {

    val budgetProgress: StateFlow<BudgetProgressUiState> = getBudgetProgressUseCase()
        .flowOn(Dispatchers.IO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = BudgetProgressUiState(isEmpty = true)
        )
}
