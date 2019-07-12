package net.wojteksz128.worktimemeasureapp.notification.endOfWork

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast

import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.util.notification.NotificationAction
import net.wojteksz128.worktimemeasureapp.util.notification.NotificationUtils

internal class EndOfWorkAction : NotificationAction {

    override fun invoke(context: Context) {
        // TODO: 03.11.2018 implement this
        NotificationUtils.clearAllNotifications(context)

        val handler = Handler(Looper.getMainLooper())
        handler.post { Toast.makeText(context.applicationContext, context.getString(R.string.dashboard_snackbar_info_outcome_registered), Toast.LENGTH_LONG).show() }
    }
}
