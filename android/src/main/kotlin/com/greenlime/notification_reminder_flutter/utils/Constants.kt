
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.widget.TextView
import androidx.annotation.DrawableRes
import com.greenlime.notification_reminder_flutter.R

object Constants {
    var sharedPrefFlutterKey: String = "current_user_uid"
    var IVR_HIGH_IMPORTANCE_CHANNEL: String = "IVR_HIGH_IMPORTANCE_CHANNEL"
    var KEY_BASE_URL: String = "baseURL"
    var KEY_IVR_ENDPOINT: String = "checkIVREndPoint"

    var KEY_LEAD_INFO_ENDPOINT: String = "leadInfoEndPoint"
    var KEY_IVR_MODE : String = "isIVR"
    fun normalizeIndianNumber(rawNumber: String?): String {
        if (rawNumber.isNullOrBlank()) return ""
        // Remove all non-digit characters (spaces, hyphens, +, etc.)
        var number = rawNumber.replace("\\D".toRegex(), "")
        // Remove leading 91 or 0 if present
        number = when {
            number.startsWith("91") && number.length > 10 -> number.substring(number.length - 10)
            number.startsWith("0") && number.length > 10 -> number.substring(number.length - 10)
            number.length > 10 -> number.substring(number.length - 10)
            else -> number
        }
        return number
    }

    /**
     * Returns the drawable resource ID corresponding to the lead status.
     *
     * @param context Context used to access resources.
     * @param leadStatus Status string from API (lowerCamelCase or any case).
     * @return Drawable resource ID.
     */
    @DrawableRes
    fun getLeadStatusDrawable(context: Context, leadStatus: String?): Int {
        if (leadStatus.isNullOrBlank()) return R.drawable.fresh
        // Normalize string: lowercase, trim, replace spaces & hyphens with underscores
        val normalized = leadStatus
            .lowercase()
            .trim()
            .replace(" ", "_")
            .replace("-", "_")

        return when (normalized) {
            "call_back", "callback" -> R.drawable.call_back
            "deal_lost", "deallost" -> R.drawable.deal_lost
            "dealing" -> R.drawable.dealing
            "follow_up", "followup", "post_visit_follow_up" -> R.drawable.follow_up
            "fresh" -> R.drawable.fresh
            "junk" -> R.drawable.junk
            "not_interested", "notinterested" -> R.drawable.not_interested
            "registered_client", "registeredclient" -> R.drawable.registered_client
            "response" -> R.drawable.response
            "retry_suitable", "retrysuitable" -> R.drawable.retry_suitable
            "unanswered" -> R.drawable.unanswered
            else -> R.drawable.fresh
        }
    }

    /**
     * Returns the drawable resource ID corresponding to the lead character type.
     *
     * @param context Context used to access resources.
     * @param leadCharacter Character type string from API (any case).
     * @return Drawable resource ID.
     */
    @DrawableRes
    fun getLeadCharacterDrawable(context: Context, leadCharacter: String?): Int {
        if (leadCharacter.isNullOrBlank()) return R.drawable.not_applicable
        return when (leadCharacter.lowercase().trim()) {
            "confused" -> R.drawable.confused
            "excited" -> R.drawable.excited
            "neutral" -> R.drawable.neutral
            "not_applicable", "notapplicable" -> R.drawable.not_applicable
            "thinking" -> R.drawable.thinking
            else -> R.drawable.not_applicable
        }
    }

    fun TextView.setLimitedText(text: String, maxChars: Int = 25) {
        if (text.length > maxChars) {
            this.text = text.take(maxChars) + "..."
            this.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 12f)
        } else {
            this.text = text
        }
    }

    fun toggleFirebaseService(context: Context, enable: Boolean) {
        val component = ComponentName(context, "com.greenlime.notification_reminder_flutter.MyFirebaseMessagingService")
        val pm = context.packageManager
        pm.setComponentEnabledSetting(
            component,
            if (enable)
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            else
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
    }
}