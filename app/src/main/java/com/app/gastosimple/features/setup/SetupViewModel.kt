package com.app.gastosimple.features.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.gastosimple.core.data.local.BudgetPeriodEntity
import com.app.gastosimple.core.data.local.GastoSimpleDao
import com.app.gastosimple.core.data.local.UserEntity
import com.app.gastosimple.core.data.prefs.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

data class SetupState(
    val budget: String = "",
    val cycleType: String = "MENSUAL",
    val users: List<UserEntity> = listOf(UserEntity(name = "Usuario Principal", contributionPercentage = 100.0)),
    val isFinished: Boolean = false,
    val error: String? = null
)

class SetupViewModel(
    private val dao: GastoSimpleDao,
    private val prefs: UserPreferencesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SetupState())
    val state = _state.asStateFlow()

    fun onBudgetChange(newBudget: String) {
        _state.value = _state.value.copy(budget = newBudget)
    }

    fun onCycleTypeChange(type: String) {
        _state.value = _state.value.copy(cycleType = type)
    }

    fun addUser(name: String) {
        val currentUsers = _state.value.users.toMutableList()
        currentUsers.add(UserEntity(name = name, contributionPercentage = 0.0))
        _state.value = _state.value.copy(users = currentUsers)
    }

    fun updatePercentage(index: Int, percentage: Double) {
        val currentUsers = _state.value.users.toMutableList()
        if (index in currentUsers.indices) {
            currentUsers[index] = currentUsers[index].copy(contributionPercentage = percentage)
            _state.value = _state.value.copy(users = currentUsers)
        }
    }

    fun finishSetup() {
        val totalPercentage = _state.value.users.sumOf { it.contributionPercentage }
        if (totalPercentage != 100.0) {
            _state.value = _state.value.copy(error = "La suma de porcentajes debe ser exactamente 100%")
            return
        }

        viewModelScope.launch {
            // Save Users
            _state.value.users.forEach { dao.insertUser(it) }
            
            // Create First Period
            val period = BudgetPeriodEntity(
                totalBudget = _state.value.budget,
                startDate = Date().time,
                endDate = null, // Logic to calculate based on cycleType could be added
                cycleType = _state.value.cycleType
            )
            dao.insertPeriod(period)
            
            // Mark Setup Complete
            prefs.setSetupComplete(true)
            _state.value = _state.value.copy(isFinished = true)
        }
    }
}
