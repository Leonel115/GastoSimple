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
import java.util.*

class NotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val dao: GastoSimpleDao by inject()

    override suspend fun doWork(): Result {
        val expenses = dao.getRecurringExpenses().first()
        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_MONTH)

        expenses.forEach { expense ->
            val expenseCal = Calendar.getInstance().apply { timeInMillis = expense.date }
            if (expenseCal.get(Calendar.DAY_OF_MONTH) == today) {
                showNotification(expense.concept, expense.amount)
            }
        }
        
        return Result.success()
    }

    private fun showNotification(concept: String, amount: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "expense_alerts"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Alertas de Gastos", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Recordatorio de Pago")
            .setContentText("Hoy vence: $concept ($$amount)")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}