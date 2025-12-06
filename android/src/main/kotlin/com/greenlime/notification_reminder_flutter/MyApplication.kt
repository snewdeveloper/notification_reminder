package com.greenlime.notification_reminder_flutter

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.annotation.RequiresApi

class App : Application() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()

        val channel = NotificationChannel(
            "REMINDER_CHANNEL",
            "Reminder Channel",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Shows incoming reminder call screen"
            setSound(null, null) // Using custom player instead
            enableVibration(true)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }

        val nm = getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(channel)
    }
}