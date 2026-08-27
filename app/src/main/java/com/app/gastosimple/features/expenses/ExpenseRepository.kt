package com.app.gastosimple.features.expenses

import com.app.gastosimple.core.data.local.BudgetPeriodEntity
import com.app.gastosimple.core.data.local.ExpenseEntity
import com.app.gastosimple.core.data.local.GastoSimpleDao
import com.app.gastosimple.core.data.local.UserEntity
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(private val dao: GastoSimpleDao) {
    fun getActivePeriod(): Flow<BudgetPeriodEntity?> = dao.getActivePeriod()
    fun getUsers(): Flow<List<UserEntity>> = dao.getAllUsers()
    fun getExpenses(periodId: Long): Flow<List<ExpenseEntity>> = dao.getExpensesByPeriod(periodId)
    fun getExpensesByDateRange(startTimestamp: Long, endTimestamp: Long): Flow<List<ExpenseEntity>> =
        dao.getExpensesByDateRange(startTimestamp, endTimestamp)
    fun getAllExpenses(): Flow<List<ExpenseEntity>> = dao.getAllNonDeletedExpenses()
    fun getBudgetPeriodsByDateRange(startTimestamp: Long, endTimestamp: Long): Flow<List<BudgetPeriodEntity>> =
        dao.getBudgetPeriodsByDateRange(startTimestamp, endTimestamp)
    fun getAllPeriods(): Flow<List<BudgetPeriodEntity>> = dao.getAllPeriods()
    fun getAllTransactionDates(): Flow<List<Long>> = dao.getAllTransactionDates()
    
    suspend fun getTotalRecordCount(): Int = dao.getTotalRecordCount()
    suspend fun addExpense(expense: ExpenseEntity) = dao.insertExpense(expense)
    suspend fun addPeriod(period: BudgetPeriodEntity) = dao.insertPeriod(period)
    suspend fun updatePeriod(period: BudgetPeriodEntity) = dao.updatePeriod(period)
}
