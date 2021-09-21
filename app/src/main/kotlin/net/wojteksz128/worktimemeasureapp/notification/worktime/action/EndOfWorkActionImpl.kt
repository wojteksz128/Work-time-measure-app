package net.wojteksz128.worktimemeasureapp.notification.worktime.action

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.*

import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.notification.NotificationActionImpl
import net.wojteksz128.worktimemeasureapp.notification.NotificationUtils
import net.wojteksz128.worktimemeasureapp.notification.worktime.EndOfWorkTimeNotification
import net.wojteksz128.worktimemeasureapp.notification.worktime.WorkTimeInProgressNotification
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import net.wojteksz128.worktimemeasureapp.util.comeevent.ComeEventUtils

internal object EndOfWorkActionImpl : NotificationActionImpl, ClassTagAware {

    // TODO: 27.08.2021 change way of calling another scope
    private val job = Job()
    private val scopeForSaving = CoroutineScope(job + Dispatchers.Main)

    override operator fun invoke(context: Context) {
        // TODO: 21.09.2021 Czy nie potrzeba zdefiniować osobnej akcji kończenia dnia pracy jako klasy?
        scopeForSaving.launch {
            Log.d(classTag, "invoke: End Work Day action clicked")

            ComeEventUtils.registerNewEvent(context)
            NotificationManagerCompat.from(context).cancel(WorkTimeInProgressNotification.notificationId)
            NotificationManagerCompat.from(context).cancel(EndOfWorkTimeNotification.notificationId)

            Toast.makeText(context.applicationContext, context.getString(R.string.dashboard_snackbar_info_outcome_registered), Toast.LENGTH_LONG).show()
        }
    }
}
