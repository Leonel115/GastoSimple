package com.app.gastosimple.core.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GastoSimpleDao {
    // Users
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Unit

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers(): Unit

    // Expenses
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity): Unit

    @Update
    suspend fun updateExpense(expense: ExpenseEntity): Unit

    @androidx.room.Delete
    suspend fun deleteExpense(expense: ExpenseEntity): Unit

    @Query("SELECT * FROM expenses WHERE periodId = :periodId ORDER BY date DESC")
    fun getExpensesByPeriod(periodId: Long): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE recurrence != 'NONE'")
    fun getRecurringExpenses(): Flow<List<ExpenseEntity>>

    // Budget Periods
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPeriod(period: BudgetPeriodEntity): Long

    @Update
    suspend fun updatePeriod(period: BudgetPeriodEntity): Unit

    @Query("SELECT * FROM budget_periods WHERE isActive = 1 LIMIT 1")
    fun getActivePeriod(): Flow<BudgetPeriodEntity?>

    @Query("SELECT * FROM budget_periods ORDER BY startDate DESC")
    fun getAllPeriods(): Flow<List<BudgetPeriodEntity>>
}
