package net.wojteksz128.worktimemeasureapp.notification.action

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast

import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.util.notification.NotificationUtils

internal object EndOfWorkAction : NotificationAction {

    private val LOG = EndOfWorkAction::class.java.simpleName

    override operator fun invoke(context: Context) {
        Log.d(LOG, "invoke: Ignore notification clicked")
        // TODO: 03.11.2018 implement this
        NotificationUtils.clearAllNotifications(context)

        val handler = Handler(Looper.getMainLooper())
        handler.post { Toast.makeText(context.applicationContext, context.getString(R.string.dashboard_snackbar_info_outcome_registered), Toast.LENGTH_LONG).show() }
    }
}
