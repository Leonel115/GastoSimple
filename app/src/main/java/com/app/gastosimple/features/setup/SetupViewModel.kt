package com.app.gastosimple.features.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.gastosimple.R
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
    val isMultiUser: Boolean = false,
    val users: List<UserEntity> = listOf(UserEntity(name = "Usuario Principal", contributionPercentage = 100.0)),
    val currentStep: Int = 0, // 0-2: Onboarding, 3: Budget, 4: Users
    val isFinished: Boolean = false,
    val errorResId: Int? = null
)

class SetupViewModel(
    private val dao: GastoSimpleDao,
    private val prefs: UserPreferencesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SetupState())
    val state = _state.asStateFlow()

    fun nextStep() {
        if (_state.value.currentStep < 4) {
            _state.value = _state.value.copy(currentStep = _state.value.currentStep + 1, errorResId = null)
        }
    }

    fun previousStep() {
        if (_state.value.currentStep > 0) {
            _state.value = _state.value.copy(currentStep = _state.value.currentStep - 1, errorResId = null)
        }
    }

    fun onBudgetChange(newBudget: String) {
        _state.value = _state.value.copy(budget = newBudget)
    }

    fun onCycleTypeChange(type: String) {
        _state.value = _state.value.copy(cycleType = type)
    }

    fun onMultiUserChange(isMulti: Boolean) {
        val newUsers = if (isMulti) {
            // Si cambia a multi-usuario, reseteamos a todos a 0% para configuración manual
            _state.value.users.map { it.copy(contributionPercentage = 0.0) }
        } else {
            // Si vuelve a usuario único, el principal retoma el 100%
            listOf(_state.value.users.first().copy(contributionPercentage = 100.0))
        }
        _state.value = _state.value.copy(isMultiUser = isMulti, users = newUsers)
    }

    fun addUser(name: String) {
        val currentUsers = _state.value.users.toMutableList()
        currentUsers.add(UserEntity(name = name, contributionPercentage = 0.0))
        _state.value = _state.value.copy(users = currentUsers)
    }

    fun removeUser(index: Int) {
        val currentUsers = _state.value.users.toMutableList()
        if (index in currentUsers.indices && currentUsers.size > 1) {
            currentUsers.removeAt(index)
            _state.value = _state.value.copy(users = currentUsers)
        }
    }

    fun updatePercentage(index: Int, percentage: Double) {
        val currentUsers = _state.value.users.toMutableList()
        if (index in currentUsers.indices) {
            currentUsers[index] = currentUsers[index].copy(contributionPercentage = percentage)
            _state.value = _state.value.copy(users = currentUsers)
        }
    }

    fun updateUserName(index: Int, newName: String) {
        val currentUsers = _state.value.users.toMutableList()
        if (index in currentUsers.indices) {
            currentUsers[index] = currentUsers[index].copy(name = newName)
            _state.value = _state.value.copy(users = currentUsers)
        }
    }

    fun finishSetup() {
        if (_state.value.isMultiUser) {
            val totalPercentage = _state.value.users.sumOf { it.contributionPercentage }
            if (Math.abs(totalPercentage - 100.0) > 0.001) {
                _state.value = _state.value.copy(errorResId = R.string.err_sum_percentage)
                return
            }
            if (_state.value.users.any { it.name.isBlank() }) {
                _state.value = _state.value.copy(errorResId = R.string.err_multi_user_empty_name)
                return
            }
        } else {
            if (_state.value.users.first().name.isBlank()) {
                _state.value = _state.value.copy(errorResId = R.string.err_empty_user_name)
                return
            }
            // Ensure 100% for single user
            val currentUsers = _state.value.users.toMutableList()
            currentUsers[0] = currentUsers[0].copy(contributionPercentage = 100.0)
            _state.value = _state.value.copy(users = currentUsers)
        }

        if (_state.value.budget.isBlank()) {
            _state.value = _state.value.copy(errorResId = R.string.err_invalid_budget)
            return
        }

        if (_state.value.budget.toDoubleOrNull() == null || _state.value.budget.toDouble() <= 0) {
            _state.value = _state.value.copy(errorResId = R.string.err_invalid_budget)
            return
        }

        viewModelScope.launch {
            // Save Users
            dao.deleteAllUsers()
            _state.value.users.forEach { dao.insertUser(it) }
            
            // Create First Period
            val period = BudgetPeriodEntity(
                totalBudget = _state.value.budget,
                startDate = Date().time,
                endDate = null,
                cycleType = _state.value.cycleType
            )
            dao.insertPeriod(period)
            
            // Mark Setup Complete
            prefs.setSetupComplete(true)
            _state.value = _state.value.copy(isFinished = true)
        }
    }
}
