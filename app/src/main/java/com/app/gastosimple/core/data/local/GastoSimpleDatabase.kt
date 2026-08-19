package com.app.gastosimple.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        UserEntity::class,
        ExpenseEntity::class,
        BudgetPeriodEntity::class,
        InstallmentExpenseEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class GastoSimpleDatabase : RoomDatabase() {
    abstract fun dao(): GastoSimpleDao

    companion object {
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 1. Crear la tabla de compromisos por cuotas
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `installment_expenses` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                        `description` TEXT NOT NULL, 
                        `totalAmount` TEXT NOT NULL, 
                        `totalInstallments` INTEGER NOT NULL, 
                        `frequency` TEXT NOT NULL, 
                        `status` TEXT NOT NULL, 
                        `isEmergency` INTEGER NOT NULL, 
                        `startDate` INTEGER NOT NULL
                    )
                """.trimIndent())

                // 2. Agregar nuevas columnas a la tabla de gastos
                db.execSQL("ALTER TABLE `expenses` ADD COLUMN `installmentId` INTEGER")
                db.execSQL("ALTER TABLE `expenses` ADD COLUMN `isEmergency` INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}
