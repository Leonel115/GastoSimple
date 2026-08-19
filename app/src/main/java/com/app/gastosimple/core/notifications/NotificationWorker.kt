package com.app.gastosimple.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.app.gastosimple.R
import com.app.gastosimple.core.data.local.GastoSimpleDao
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.math.BigDecimal
import java.util.*

class NotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val dao: GastoSimpleDao by inject()

    override suspend fun doWork(): Result {
        val expenses = dao.getRecurringExpenses().first()
        val calendar = Calendar.getInstance()
        val todayMillis = calendar.timeInMillis

        expenses.forEach { expense ->
            val expenseCal = Calendar.getInstance().apply { timeInMillis = expense.date }
            val interval = expense.recurrenceInterval ?: 0
            
            if (interval > 0) {
                val diffMillis = todayMillis - expenseCal.timeInMillis
                val diffDays = (diffMillis / (1000 * 60 * 60 * 24)).toInt()
                
                // Notify on the day of recurrence
                if (diffDays >= 0 && diffDays % interval == 0) {
                    showNotification(expense.concept, expense.amount)
                }
            } else if (expenseCal.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)) {
                // Legacy monthly logic if interval not set
                showNotification(expense.concept, expense.amount)
            }
        }
        
        return Result.success()
    }

    private fun showNotification(concept: String, amount: BigDecimal) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "expense_alerts"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Alertas de Gastos", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val formattedAmount = amount.toPlainString()
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Recordatorio de Pago")
            .setContentText("Hoy vence: $concept ($$formattedAmount)")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}