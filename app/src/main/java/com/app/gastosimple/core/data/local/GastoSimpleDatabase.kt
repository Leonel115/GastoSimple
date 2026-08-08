package com.app.gastosimple.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [UserEntity::class, ExpenseEntity::class, BudgetPeriodEntity::class],
    version = 1,
    exportSchema = false
)
abstract class GastoSimpleDatabase : RoomDatabase() {
    abstract fun dao(): GastoSimpleDao
}
