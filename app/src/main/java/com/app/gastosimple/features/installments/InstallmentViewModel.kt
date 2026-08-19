package com.app.gastosimple.features.installments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.gastosimple.core.data.local.ExpenseEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.Date

sealed class InstallmentEvent {
    object Congratulate : InstallmentEvent()
}

class InstallmentViewModel(
    private val getActiveBalancesUseCase: GetActiveBalancesUseCase,
    private val processPaymentAndCloseUseCase: ProcessPaymentAndCloseUseCase
) : ViewModel() {

    private val _events = MutableSharedFlow<InstallmentEvent>()
    val events = _events.asSharedFlow()

    val balances: StateFlow<List<PendingBalance>> = getActiveBalancesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun registerPayment(
        installmentId: Long,
        amount: BigDecimal,
        concept: String,
        periodId: Long,
        isEmergency: Boolean
    ) {
        viewModelScope.launch {
            val payment = ExpenseEntity(
                amount = amount,
                concept = concept,
                category = "Cuotas", // Or a specific category
                userId = null, // Can be refined
                isShared = false,
                date = Date().time,
                recurrence = "NONE",
                periodId = periodId,
                installmentId = installmentId,
                isEmergency = isEmergency
            )
            
            processPaymentAndCloseUseCase.invoke(payment)
            
            // Check if it was settled to trigger congratulations
            val currentBalances = balances.value
            val installment = currentBalances.find { it.installment.id == installmentId }
            if (installment != null) {
                val newRemaining = installment.remainingBalance.subtract(amount)
                if (newRemaining <= BigDecimal.ZERO) {
                    _events.emit(InstallmentEvent.Congratulate)
                }
            }
        }
    }
}
