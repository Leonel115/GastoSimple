package com.app.gastosimple.core.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val contributionPercentage: Double // 0.0 to 100.0
)

@Entity(tableName = "installment_expenses")
data class InstallmentExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val description: String,
    val totalAmount: BigDecimal,
    val totalInstallments: Int,
    val frequency: InstallmentFrequency,
    val status: ObligationStatus = ObligationStatus.ACTIVE,
    val isEmergency: Boolean = false,
    val startDate: Long
)

@Entity(
    tableName = "expenses",
    foreignKeys = [
        ForeignKey(
            entity = InstallmentExpenseEntity::class,
            parentColumns = ["id"],
            childColumns = ["installmentId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: BigDecimal,
    val concept: String,
    val category: String,
    val userId: Long?, // Nullable if isShared is true
    val isShared: Boolean = false,
    val date: Long,
    val recurrence: String, // "NONE", "DAILY", "WEEKLY", "MONTHLY", "PERIODIC"
    val recurrenceInterval: Int? = null, // Days for recurrence (15, 30, etc)
    val periodId: Long, // Link to a specific budget period

    // Épica 5: Cuotas e Imprevistos
    val installmentId: Long? = null,
    val isEmergency: Boolean = false,

    // Diferido / Pending Changes
    val pendingAmount: String? = null,
    val pendingRecurrenceInterval: Int? = null,
    val isPendingDeletion: Boolean = false
)

@Entity(tableName = "budget_periods")
data class BudgetPeriodEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val totalBudget: BigDecimal,
    val startDate: Long,
    val endDate: Long?,
    val cycleType: String, // "QUINCENAL", "MENSUAL"
    val isActive: Boolean = true,
    val savings: BigDecimal? = null // Calculated when closed
)
