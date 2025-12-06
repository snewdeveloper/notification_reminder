package com.greenlime.notification_reminder_flutter.utils

import android.content.Context
import androidx.core.content.edit

object TopicPref {
    private const val PREF_NAME = "reminder_prefs"
    private const val KEY_TOPIC_SUBSCRIBED = "is_topic_subscribed"

    fun isReminderSubscribed(context: Context): Boolean {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return pref.getBoolean(KEY_TOPIC_SUBSCRIBED, false)
    }

    fun setReminderSubscribed(context: Context, value: Boolean) {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        pref.edit { putBoolean(KEY_TOPIC_SUBSCRIBED, value) }
    }
}