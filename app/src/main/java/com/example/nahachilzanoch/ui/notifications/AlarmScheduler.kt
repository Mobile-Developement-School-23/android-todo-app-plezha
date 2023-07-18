package com.example.nahachilzanoch.ui.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import javax.inject.Inject

class AlarmScheduler @Inject constructor(
    private val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleNotification(time: Long, title: String, message: String, id: Int) {
        val intent = Intent(context, ReminderNotification::class.java)
        intent.putExtra(titleKey, title)
        intent.putExtra(messageKey, message)
        intent.putExtra(IDKey, id)
        val pendingIntent =  PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        Log.d("Alarm", "Scheduled an alarm $title: $message for $time with id=$id")
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
    }

    fun cancelNotification(id: Int) {
        Log.d("Alarm", "Canceled id=$id")

        val intent = Intent(context, ReminderNotification::class.java)
        val pendingIntent =  PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.cancel(pendingIntent)
    }
}