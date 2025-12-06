package com.greenlime.notification_reminder_flutter.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.greenlime.notification_reminder_flutter.R
import com.greenlime.notification_reminder_flutter.ui.FullScreenReminderActivity

class ReminderReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "REMINDER_CHANNEL"
        const val NOTIFICATION_ID = 1001
    }

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(context, notificationManager)

        val fullScreenIntent = Intent(context, FullScreenReminderActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            fullScreenIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Reminder")
            .setContentText("It's time!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(pendingIntent, true)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)

        val launchIntent = Intent(context, FullScreenReminderActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        try{
            context.startActivity(launchIntent)
            Log.e("ReminderReceiver", "started Activity")

        }  catch (e: Exception) {
        Log.e("ReminderReceiver", "Failed to start activity: ${e.message}")
    }
    }

    private fun createNotificationChannel(context: Context, manager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Reminder Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Used for full-screen reminder alarms"
            manager.createNotificationChannel(channel)
        }
    }
}