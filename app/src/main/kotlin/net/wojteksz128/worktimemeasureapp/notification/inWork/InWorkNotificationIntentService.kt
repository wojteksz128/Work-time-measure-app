package net.wojteksz128.worktimemeasureapp.notification.inWork

import android.app.IntentService
import android.content.Intent

/**
 * Creates an IntentService. Invoked by your subclass's constructor.
 */
class InWorkNotificationIntentService : IntentService(InWorkNotificationIntentService::class.java.name) {

    override fun onHandleIntent(intent: Intent) {
        val action = intent.action
        action?.let { InWorkNotification.Action.valueOf(it).doAction(this) }
    }
}
