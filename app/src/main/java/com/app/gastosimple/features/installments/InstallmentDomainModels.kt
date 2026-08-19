package com.app.gastosimple.features.installments

import com.app.gastosimple.core.data.local.InstallmentExpenseEntity
import java.math.BigDecimal
import java.math.RoundingMode

data class PendingBalance(
    val installment: InstallmentExpenseEntity,
    val originalCapital: BigDecimal,
    val totalPaid: BigDecimal,
    val remainingBalance: BigDecimal,
    val progress: Float // 0.0f to 1.0f
) {
    companion object {
        fun create(installment: InstallmentExpenseEntity, paidAmount: BigDecimal): PendingBalance {
            val original = installment.totalAmount
            val remaining = original.subtract(paidAmount).max(BigDecimal.ZERO)
            
            val progressFactor = if (original > BigDecimal.ZERO) {
                paidAmount.divide(original, 4, RoundingMode.HALF_UP).toFloat()
            } else {
                1.0f
            }
            
            return PendingBalance(
                installment = installment,
                originalCapital = original,
                totalPaid = paidAmount,
                remainingBalance = remaining,
                progress = progressFactor.coerceIn(0.0f, 1.0f)
            )
        }
    }
}
