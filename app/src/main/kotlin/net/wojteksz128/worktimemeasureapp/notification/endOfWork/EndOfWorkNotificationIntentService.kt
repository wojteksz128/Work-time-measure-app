package net.wojteksz128.worktimemeasureapp.notification.endOfWork

import android.app.IntentService
import android.content.Intent

/**
 * Creates an IntentService. Invoked by your subclass's constructor.
 */
class EndOfWorkNotificationIntentService : IntentService(EndOfWorkNotificationIntentService::class.java.name) {

    override fun onHandleIntent(intent: Intent) {
        val action = intent.action
        action?.let { EndOfWorkNotification.Action.valueOf(it).doAction(this) }
    }
}
