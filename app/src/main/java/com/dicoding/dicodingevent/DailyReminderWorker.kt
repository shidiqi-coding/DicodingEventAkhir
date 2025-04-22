package com.dicoding.dicodingevent.ui.setting

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dicoding.dicodingevent.R
import com.dicoding.dicodingevent.data.retrofit.ApiConfig

class DailyReminderWorker(
    context: Context ,
    workerParams: WorkerParameters
) : Worker(context , workerParams) {

    override fun doWork(): Result {
        val apiService = ApiConfig.getApiService()



        return try {
            val response = apiService.getNotificationEvents(active = 1).execute()
            if (response.isSuccessful) {
                val result = response.body()
                if (result != null && !result.error && result.listEvents.isNotEmpty()) {
                    val event = result.listEvents[0]
                    showNotification(event.name , event.summary)
                }
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }


    @SuppressLint("MissingPermission")
    private fun showNotification(title: String , message: String) {
        val channelId = "upcoming_event_channel"
        val channelName = "Upcoming Event Reminder"



        if (Build.VERSION.SDK_INT >= 33) {
            val channel = NotificationChannel(
                channelId ,
                channelName ,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }


        val builder = NotificationCompat.Builder(applicationContext , channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)


        with(NotificationManagerCompat.from(applicationContext)) {
            notify(1001 , builder.build())
        }
    }
}
