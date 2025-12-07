
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
    var REMINDER_CHANNEL: String = "REMINDER_CHANNEL"


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


}