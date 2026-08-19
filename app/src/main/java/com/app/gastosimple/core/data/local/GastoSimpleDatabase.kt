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
    version = 4,
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

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Para agregar una ForeignKey en SQLite, es necesario recrear la tabla
                db.execSQL("""
                    CREATE TABLE `expenses_new` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                        `amount` TEXT NOT NULL, 
                        `concept` TEXT NOT NULL, 
                        `category` TEXT NOT NULL, 
                        `userId` INTEGER, 
                        `isShared` INTEGER NOT NULL, 
                        `date` INTEGER NOT NULL, 
                        `recurrence` TEXT NOT NULL, 
                        `recurrenceInterval` INTEGER, 
                        `periodId` INTEGER NOT NULL, 
                        `installmentId` INTEGER, 
                        `isEmergency` INTEGER NOT NULL, 
                        `pendingAmount` TEXT, 
                        `pendingRecurrenceInterval` INTEGER, 
                        `isPendingDeletion` INTEGER NOT NULL,
                        FOREIGN KEY(`installmentId`) REFERENCES `installment_expenses`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL
                    )
                """.trimIndent())

                db.execSQL("""
                    INSERT INTO `expenses_new` (id, amount, concept, category, userId, isShared, date, recurrence, recurrenceInterval, periodId, installmentId, isEmergency, pendingAmount, pendingRecurrenceInterval, isPendingDeletion)
                    SELECT id, amount, concept, category, userId, isShared, date, recurrence, recurrenceInterval, periodId, installmentId, isEmergency, pendingAmount, pendingRecurrenceInterval, isPendingDeletion FROM `expenses`
                """.trimIndent())

                db.execSQL("DROP TABLE `expenses`")
                db.execSQL("ALTER TABLE `expenses_new` RENAME TO `expenses`")
            }
        }
    }
}
