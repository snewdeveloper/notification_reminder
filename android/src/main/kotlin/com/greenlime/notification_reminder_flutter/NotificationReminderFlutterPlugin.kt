package com.greenlime.notification_reminder_flutter

import Constants.REMINDER_CHANNEL
import android.Manifest
import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import com.greenlime.notification_reminder_flutter.utils.TopicPref
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.net.toUri
import com.google.firebase.messaging.FirebaseMessaging
import com.greenlime.notification_reminder_flutter.managers.KillerManager
import com.greenlime.notification_reminder_flutter.utils.BatteryOptimizationUtil
import com.greenlime.notification_reminder_flutter.utils.Constants.CHANNEL_ID
import com.greenlime.notification_reminder_flutter.utils.Constants.FIREBASE_TOPIC_EMPLOYEES_NOTIFICATION_REMINDER
import com.greenlime.notification_reminder_flutter.utils.PrefKeys
import com.greenlime.notification_reminder_flutter.utils.PrefUtils
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import kotlinx.coroutines.channels.Channel
import java.lang.Exception

/** NotificationReminderFlutterPlugin */
class NotificationReminderFlutterPlugin :
    FlutterPlugin,
    ActivityAware,
    MethodCallHandler {
    // The MethodChannel that will the communication between Flutter and native Android
    //
    // This local reference serves to register the plugin with the Flutter Engine and unregister it
    // when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel
    private lateinit var context: Context
    private var activity: Activity? = null

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel =
            MethodChannel(flutterPluginBinding.binaryMessenger, "notification_reminder_flutter")
//        activity = flutterPluginBinding.activity
//        context = activity!!.applicationContext
//        createNotificationChannel(flutterPluginBinding.applicationContext)

    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
        context = activity!!.applicationContext
        channel.setMethodCallHandler(this)
    }


    override fun onDetachedFromActivityForConfigChanges() {
        activity = null
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        activity = binding.activity
    }

    override fun onDetachedFromActivity() {
        activity = null
    }

    override fun onMethodCall(
        call: MethodCall,
        result: Result
    ) {
//        if (call.method == "getPlatformVersion") {
//            result.success("Android ${android.os.Build.VERSION.RELEASE}")
//        } else {
//            result.notImplemented()
//        }
        when (call.method) {
            "getPlatformVersion" -> {
                result.success("Android ${android.os.Build.VERSION.RELEASE}")
            }
            // ✅ Battery Optimization Plugin Methods (Merged)
            "showEnableAutoStart" -> {
                val args = call.arguments as List<*>
                val title = args[0].toString()
                val msg = args[1].toString()
                showAutoStart(title, msg)
                result.success(true)
            }

            "showDisableManBatteryOptimization" -> {
                val args = call.arguments as List<*>
                showManBatteryDialog(args[0].toString(), args[1].toString(), false)
                result.success(true)
            }

            "showDisableBatteryOptimization" -> {
                showIgnoreBatteryPermissions()
                result.success(true)
            }

            "disableAllOptimizations" -> {
                val args = call.arguments as List<*>
                handleIgnoreAll(
                    args[0].toString(),
                    args[1].toString(),
                    args[2].toString(),
                    args[3].toString()
                )
                result.success(true)
            }
             "subscribeUserToTopicOnce" -> {
                 createNotificationChannel(context)
                 subscribeUserToTopicOnce()
                result.success(true)
            }
            "requestBasicPermissions" -> {
                requestBasicPermissions(context)
                result.success(true)
            }
            "isAutoStartEnabled" -> result.success(getManAutoStart())
            "isBatteryOptimizationDisabled" -> result.success(
                BatteryOptimizationUtil.isIgnoringBatteryOptimizations(
                    context
                )
            )

            "isManBatteryOptimizationDisabled" -> result.success(getManBatteryOptimization())
            "isAllOptimizationsDisabled" ->
                result.success(
                    getManAutoStart() &&
                            BatteryOptimizationUtil.isIgnoringBatteryOptimizations(context) &&
                            getManBatteryOptimization()
                )

            else -> {
                result.notImplemented()
            }
        }

    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

     fun createNotificationChannel(context: Context) {
        Log.d("NotificationReminderFlutterPlugin", "Notification channel entering creation block")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                REMINDER_CHANNEL,
                "Reminder Channel",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Shows incoming reminder call screen"
                setSound(null, null)
                enableVibration(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            val nm = context.getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(channel)
            Log.d("NotificationReminderFlutterPlugin", "Notification channel created")
        }
    }

    fun requestBasicPermissions(context: Context) {
       activity?.let {
           if (Build.VERSION.SDK_INT >= 33) {
               requestPermissions(
                   activity!!,
                   arrayOf(
                       Manifest.permission.POST_NOTIFICATIONS,
                       Manifest.permission.USE_EXACT_ALARM,
                       Manifest.permission.SCHEDULE_EXACT_ALARM,
                   ),
                   101
               )
           }
           if (Build.VERSION.SDK_INT >= 34) {
               val intent = Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT).apply {
                   data = "package:${context.packageName}".toUri()
                   flags = Intent.FLAG_ACTIVITY_NEW_TASK
               }
               context.startActivity(intent)
           }
       }
    }
    fun subscribeUserToTopicOnce() {
        // If already subscribed, do nothing
        try {
            if (TopicPref.isReminderSubscribed(context)) {
                Log.d(
                    "FCM",
                    "Already subscribed to topic: $FIREBASE_TOPIC_EMPLOYEES_NOTIFICATION_REMINDER"
                )
                return
            }

            FirebaseMessaging.getInstance()
                .subscribeToTopic(FIREBASE_TOPIC_EMPLOYEES_NOTIFICATION_REMINDER)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(
                            "FCM",
                            "Topic subscription SUCCESS: $FIREBASE_TOPIC_EMPLOYEES_NOTIFICATION_REMINDER"
                        )
                        TopicPref.setReminderSubscribed(context, true)   // mark as subscribed
                    } else {
                        Log.e("FCM", "Topic subscription FAILED: ${task.exception?.message}")
                    }
                }
        } catch (e: Exception) {
            Log.e("FCM", "Exception: ${e.message}")
        }
    }

    // ✅ Battery Optimization Functions (Merged)

    private fun showAutoStart(title: String, msg: String) {
        BatteryOptimizationUtil.showBatteryOptimizationDialog(
            activity,
            KillerManager.Actions.ACTION_AUTOSTART,
            title,
            msg,
            { setManAutoStart(true) },
            { setManAutoStart(false) }
        )
    }

    private fun showManBatteryDialog(title: String, msg: String, chain: Boolean) {
        BatteryOptimizationUtil.showBatteryOptimizationDialog(
            activity,
            KillerManager.Actions.ACTION_POWERSAVING,
            title,
            msg,
            {
                setManBatteryOptimization(true)
                if (chain) showIgnoreBatteryPermissions()
            },
            {
                if (chain) showIgnoreBatteryPermissions()
            }
        )
    }

    private fun showIgnoreBatteryPermissions() {
        if (!BatteryOptimizationUtil.isIgnoringBatteryOptimizations(context)) {
            BatteryOptimizationUtil.getIgnoreBatteryOptimizationsIntent(context)?.let {
                context.startActivity(it)
            }
        }
    }

    private fun handleIgnoreAll(autoTitle: String, autoMsg: String, manTitle: String, manMsg: String) {
        if (!getManAutoStart()) {
            showAutoStart(autoTitle, autoMsg)
        } else if (!getManBatteryOptimization()) {
            showManBatteryDialog(manTitle, manMsg, true)
        } else {
            showIgnoreBatteryPermissions()
        }
    }

    private fun setManBatteryOptimization(v: Boolean) =
        PrefUtils.saveToPrefs(context, PrefKeys.IS_MAN_BATTERY_OPTIMIZATION_ACCEPTED, v)

    private fun getManBatteryOptimization(): Boolean {
        return if (PrefUtils.hasKey(context, PrefKeys.IS_MAN_BATTERY_OPTIMIZATION_ACCEPTED))
            PrefUtils.getFromPrefs(context, PrefKeys.IS_MAN_BATTERY_OPTIMIZATION_ACCEPTED, false) as Boolean
        else {
            val available = KillerManager.isActionAvailable(context, KillerManager.Actions.ACTION_POWERSAVING)
            PrefUtils.saveToPrefs(context, PrefKeys.IS_MAN_BATTERY_OPTIMIZATION_ACCEPTED, !available)
            !available
        }
    }

    private fun setManAutoStart(v: Boolean) =
        PrefUtils.saveToPrefs(context, PrefKeys.IS_MAN_AUTO_START_ACCEPTED, v)

    private fun getManAutoStart(): Boolean {
        return if (PrefUtils.hasKey(context, PrefKeys.IS_MAN_AUTO_START_ACCEPTED))
            PrefUtils.getFromPrefs(context, PrefKeys.IS_MAN_AUTO_START_ACCEPTED, false) as Boolean
        else {
            val available = KillerManager.isActionAvailable(context, KillerManager.Actions.ACTION_AUTOSTART)
            PrefUtils.saveToPrefs(context, PrefKeys.IS_MAN_AUTO_START_ACCEPTED, !available)
            !available
        }
    }

}
