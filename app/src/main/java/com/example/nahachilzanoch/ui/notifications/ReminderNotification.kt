package com.example.nahachilzanoch.ui.notifications

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

const val reminderNotificationChannelID = "reminders"
const val titleKey = "title"
const val messageKey = "message"
const val IDKey = "id"

class ReminderNotification : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notification = NotificationCompat.Builder(context, reminderNotificationChannelID)
            .setContentTitle(intent.getStringExtra(titleKey))
            .setContentText(intent.getStringExtra(messageKey))
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify( intent.getIntExtra(IDKey, 0) , notification)
    }

}