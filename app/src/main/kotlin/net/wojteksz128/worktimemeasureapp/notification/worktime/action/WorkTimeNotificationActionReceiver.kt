package net.wojteksz128.worktimemeasureapp.notification.worktime.action

import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.wojteksz128.worktimemeasureapp.notification.HiltBroadcastReceiver
import net.wojteksz128.worktimemeasureapp.notification.worktime.WorkTimeNotificationService
import net.wojteksz128.worktimemeasureapp.util.comeevent.ComeEventUtils
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import javax.inject.Inject

@AndroidEntryPoint
class WorkTimeNotificationActionReceiver : HiltBroadcastReceiver() {

    @Inject
    lateinit var dateTimeProvider: DateTimeProvider

    @Inject
    lateinit var notificationService: WorkTimeNotificationService

    @Inject
    lateinit var comeEventUtils: ComeEventUtils


    @DelicateCoroutinesApi
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        when (intent.action) {
            WorkTimeNotificationService.SNOOZE_ACTION -> {
                val nextReminder = dateTimeProvider.currentTime.plusMinutes(10)
                notificationService.scheduleEndOfWorkNotification(nextReminder)
            }

            WorkTimeNotificationService.STOP_WORK_ACTION -> {
                GlobalScope.launch {
                    comeEventUtils.registerNewEvent()
                }
            }

            WorkTimeNotificationService.EXTEND -> {
                // TODO implement this
            }
        }
    }
}