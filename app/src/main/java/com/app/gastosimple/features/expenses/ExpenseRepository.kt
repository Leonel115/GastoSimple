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
    
    suspend fun addExpense(expense: ExpenseEntity) = dao.insertExpense(expense)
    suspend fun addPeriod(period: BudgetPeriodEntity) = dao.insertPeriod(period)
    suspend fun updatePeriod(period: BudgetPeriodEntity) = dao.updatePeriod(period)
}
