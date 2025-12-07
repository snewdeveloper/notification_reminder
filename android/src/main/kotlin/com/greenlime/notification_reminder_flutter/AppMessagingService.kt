package com.greenlime.notification_reminder_flutter

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.util.Log

import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.greenlime.notification_reminder_flutter.R
import com.greenlime.notification_reminder_flutter.FullScreenReminderActivity

class AppFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        Log.d("AppFirebaseMessagingService","--token--$token")
        super.onNewToken(token)
    }
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("AppFirebaseMessagingService","--remoteMessage--${remoteMessage}")
        val data = remoteMessage.data
        if (data.isEmpty()) return

        val type = data["type"] ?: return
        if (type != "reminder") return

        val title = data["title"] ?: "Reminder"
        val description = data["description"] ?: ""
        val audioUrl = data["audio_url"] ?: ""
        val reminderId = data["reminderId"] ?: ""

        showReminderNotification(title, description, audioUrl,reminderId)
        showReminderPopup(title, description, audioUrl,reminderId)
    }

      fun showReminderNotification(    title: String,
                                             description: String,
                                             audioUrl: String,
                                             reminderId: String) {

        val intent = Intent(this, FullScreenReminderActivity::class.java)
//            .apply {
//            Intent.setFlags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
//            Intent.putExtra("audio_url", audioUrl)
//            Intent.putExtra("title", title)
//            Intent.putExtra("description", description)
//            Intent.putExtra("reminder_id", reminderId)
//        }
          intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.putExtra("audio_url", audioUrl)
            intent.putExtra("title", title)
            intent.putExtra("description", description)
            intent.putExtra("reminder_id", reminderId)

        val pendingIntent = PendingIntent.getActivity(
            this,
            1001,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(this, "REMINDER_CHANNEL")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setAutoCancel(true)
            .setFullScreenIntent(pendingIntent, true)

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(2001, builder.build())
    }

     fun showReminderPopup( title: String,
                                   description: String,
                                   audioUrl: String,
                                   reminderId: String) {
        val popupIntent = Intent(this, FullScreenReminderActivity::class.java)
//            .apply {
//            Intent.setFlags = Intent.FLAG_ACTIVITY_NEW_TASK
//            Intent.putExtra("audio_url", audioUrl)
//            Intent.putExtra("title", title)
//            Intent.putExtra("description", description)
//            Intent.putExtra("reminder_id", reminderId)


//        }
         popupIntent.flags =  Intent.FLAG_ACTIVITY_NEW_TASK
         popupIntent.putExtra("audio_url", audioUrl)
         popupIntent.putExtra("title", title)
         popupIntent.putExtra("description", description)
         popupIntent.putExtra("reminder_id", reminderId)

        startActivity(popupIntent)
    }
}