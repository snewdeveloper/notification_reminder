package com.greenlime.notification_reminder_flutter.receiver

import android.Manifest
import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission

class RingingPlayer(private val context: Context) {

    private var ringtone: Ringtone? = null

    @RequiresPermission(Manifest.permission.VIBRATE)
    @RequiresApi(Build.VERSION_CODES.P)
    fun startRinging() {
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        ringtone = RingtoneManager.getRingtone(context, uri)
        ringtone?.isLooping = true
        ringtone?.play()

        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        val pattern = longArrayOf(0, 500, 800) // vibrate, pause, vibrate...
        vibrator.vibrate(
            VibrationEffect.createWaveform(pattern, 0)
        )
    }

    fun stopRinging() {
        ringtone?.stop()
        ringtone = null

        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.cancel()
    }
}