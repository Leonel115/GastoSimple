package com.app.gastosimple.features.installments

import com.app.gastosimple.core.data.local.ExpenseEntity
import com.app.gastosimple.core.data.local.GastoSimpleDao
import com.app.gastosimple.core.data.local.ObligationStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.math.RoundingMode

class CalculateInstallmentQuotaUseCase {
    operator fun invoke(totalAmount: BigDecimal, installments: Int): BigDecimal {
        if (installments <= 0) return totalAmount
        return totalAmount.divide(installments.toBigDecimal(), 2, RoundingMode.HALF_UP)
    }
}

class GetActiveBalancesUseCase(private val dao: GastoSimpleDao) {
    operator fun invoke(): Flow<InstallmentBalancesState> {
        return dao.getAllInstallmentsWithPaidAmount().map { tupleList ->
            val allBalances = tupleList.map { tuple ->
                val paidAmount = tuple.totalPaid ?: BigDecimal.ZERO
                PendingBalance.create(tuple.installment, paidAmount)
            }
            val active = allBalances.filter { it.installment.status == ObligationStatus.ACTIVE }
            val settled = allBalances.filter { it.installment.status == ObligationStatus.SETTLED }
            InstallmentBalancesState(activeBalances = active, settledBalances = settled)
        }.flowOn(Dispatchers.IO)
    }
}

class ProcessPaymentAndCloseUseCase(private val dao: GastoSimpleDao) {
    suspend fun invoke(payment: ExpenseEntity) = withContext(Dispatchers.IO) {
        // 1. Insertar el pago
        dao.insertExpense(payment)

        // 2. Si el pago está asociado a un compromiso, verificar saldo
        payment.installmentId?.let { id ->
            val installment = dao.getInstallmentById(id) ?: return@let
            val payments = dao.getPaymentsForInstallment(id).first()
            val totalPaid = payments.sumOf { it.amount }
            
            val remaining = installment.totalAmount.subtract(totalPaid)
            
            if (remaining <= BigDecimal.ZERO) {
                dao.updateInstallment(installment.copy(status = ObligationStatus.SETTLED))
            }
        }
    }
}

// Extension to sum BigDecimal in a list
private fun Iterable<ExpenseEntity>.sumOf(selector: (ExpenseEntity) -> BigDecimal): BigDecimal {
    var sum = BigDecimal.ZERO
    for (element in this) {
        sum = sum.add(selector(element))
    }
    return sum
}
