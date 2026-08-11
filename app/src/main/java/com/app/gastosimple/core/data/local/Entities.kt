package com.app.gastosimple.core.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val contributionPercentage: Double // 0.0 to 100.0
)

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: String, // Stored as String for BigDecimal precision
    val concept: String,
    val category: String,
    val userId: Long?, // Nullable if isShared is true
    val isShared: Boolean = false,
    val date: Long,
    val recurrence: String, // "NONE", "DAILY", "WEEKLY", "MONTHLY", "PERIODIC"
    val recurrenceInterval: Int? = null, // Days for recurrence (15, 30, etc)
    val periodId: Long, // Link to a specific budget period

    // Diferido / Pending Changes
    val pendingAmount: String? = null,
    val pendingRecurrenceInterval: Int? = null,
    val isPendingDeletion: Boolean = false
)

@Entity(tableName = "budget_periods")
data class BudgetPeriodEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val totalBudget: String,
    val startDate: Long,
    val endDate: Long?,
    val cycleType: String, // "QUINCENAL", "MENSUAL"
    val isActive: Boolean = true,
    val savings: String? = null // Calculated when closed
)
