package com.greenlime.notification_reminder_flutter.utils

fun formatTime(millis: Long): String {
    if (millis <= 0) return "00:00"
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}


object Constants{
    const val CHANNEL_ID = "reminder_channel"
    const val FIREBASE_TOPIC_EMPLOYEES_NOTIFICATION_REMINDER = "reminder_notifications"
}