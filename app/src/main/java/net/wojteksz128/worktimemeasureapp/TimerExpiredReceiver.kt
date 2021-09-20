package net.wojteksz128.worktimemeasureapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import net.wojteksz128.worktimemeasureapp.notification.EndOfWorkNotification

class TimerExpiredReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        EndOfWorkNotification(context).notifyUser()
    }
}