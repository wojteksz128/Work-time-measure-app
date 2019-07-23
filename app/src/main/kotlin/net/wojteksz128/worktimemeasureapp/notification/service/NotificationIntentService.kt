package net.wojteksz128.worktimemeasureapp.notification.service

import android.app.IntentService
import android.content.Intent
import net.wojteksz128.worktimemeasureapp.notification.action.Action

/**
 * Creates an IntentService. Invoked by your subclass's constructor.
 */
class NotificationIntentService : IntentService(NotificationIntentService::class.java.name) {

    override fun onHandleIntent(intent: Intent) {
        val action = intent.action
        action?.let { Action.valueOf(it)(this) }
    }
}
