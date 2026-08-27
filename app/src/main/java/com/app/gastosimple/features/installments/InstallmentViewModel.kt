package com.app.gastosimple.features.installments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.gastosimple.core.data.local.ExpenseEntity
import com.app.gastosimple.core.data.local.GastoSimpleDao
import com.app.gastosimple.core.data.local.ObligationStatus
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.Date

data class InstallmentUiState(
    val selectedBalance: PendingBalance? = null,
    val showPaymentDialog: Boolean = false,
    val errorResId: Int? = null
)

sealed class InstallmentEvent {
    object Congratulate : InstallmentEvent()
    data class Error(val resId: Int) : InstallmentEvent()
}

class InstallmentViewModel(
    private val dao: GastoSimpleDao,
    private val getActiveBalancesUseCase: GetActiveBalancesUseCase,
    private val processPaymentAndCloseUseCase: ProcessPaymentAndCloseUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(InstallmentUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<InstallmentEvent>()
    val events = _events.asSharedFlow()

    val balances: StateFlow<List<PendingBalance>> = getActiveBalancesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onPaymentClicked(balance: PendingBalance) {
        _uiState.update { it.copy(selectedBalance = balance, showPaymentDialog = true) }
    }

    fun onDismissPaymentDialog() {
        _uiState.update { it.copy(showPaymentDialog = false, selectedBalance = null, errorResId = null) }
    }

    fun registerPayment(
        amount: BigDecimal,
        concept: String
    ) {
        val balance = _uiState.value.selectedBalance ?: return
        
        if (amount <= BigDecimal.ZERO) {
            _uiState.update { it.copy(errorResId = com.app.gastosimple.R.string.err_invalid_amount) }
            return
        }

        if (amount > balance.remainingBalance) {
            _uiState.update { it.copy(errorResId = com.app.gastosimple.R.string.err_insufficient_budget) } // Reusing or need specific string
            return
        }

        viewModelScope.launch {
            try {
                val activePeriod = dao.getActivePeriod().first() ?: return@launch
                
                val payment = ExpenseEntity(
                    amount = amount,
                    concept = concept,
                    category = "Aporte Emergencia",
                    userId = null,
                    isShared = false,
                    date = Date().time,
                    recurrence = "NONE",
                    periodId = activePeriod.id,
                    installmentId = balance.installment.id,
                    isEmergency = balance.installment.isEmergency
                )
                
                processPaymentAndCloseUseCase.invoke(payment)
                
                if (balance.remainingBalance.subtract(amount) <= BigDecimal.ZERO) {
                    _events.emit(InstallmentEvent.Congratulate)
                }
                
                onDismissPaymentDialog()
            } catch (e: Exception) {
                _events.emit(InstallmentEvent.Error(com.app.gastosimple.R.string.err_save_expense))
            }
        }
    }
}
