package net.wojteksz128.worktimemeasureapp.notification.action

import android.app.IntentService
import android.content.Intent

/**
 * Creates an IntentService. Invoked by your subclass's constructor.
 */
class NotificationIntentService : IntentService(NotificationIntentService::class.java.name) {

    override fun onHandleIntent(intent: Intent?) {
        val action = intent?.action
        action?.let { Action.valueOf(it)(this) }
    }
}
