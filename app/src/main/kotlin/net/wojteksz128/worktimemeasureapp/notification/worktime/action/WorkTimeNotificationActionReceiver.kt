package net.wojteksz128.worktimemeasureapp.notification.worktime.action

import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.wojteksz128.worktimemeasureapp.notification.HiltBroadcastReceiver
import net.wojteksz128.worktimemeasureapp.notification.worktime.WorkTimeNotificationService
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import net.wojteksz128.worktimemeasureapp.util.comeevent.ComeEventUtils
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import org.threeten.bp.ZonedDateTime
import javax.inject.Inject

@AndroidEntryPoint
class WorkTimeNotificationActionReceiver : HiltBroadcastReceiver(), ClassTagAware {

    companion object {
        const val NEXT_NOTIFICATION_TIME = "nextNotificationTime"
        const val STANDARD_END_TIME = "standardEndTime"
        const val BALANCED_END_TIME = "balancedEndTime"
    }

    @Inject
    lateinit var dateTimeProvider: DateTimeProvider

    @Inject
    lateinit var notificationService: WorkTimeNotificationService

    @Inject
    lateinit var comeEventUtils: ComeEventUtils


    @DelicateCoroutinesApi
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        Log.d(
            classTag,
            "onReceive: receive action - ${intent.action}"
        )

        when (intent.action) {
            WorkTimeNotificationService.SNOOZE_ACTION -> {
                performSnoozeEndOfWorkToNextTenMinutes()
            }

            WorkTimeNotificationService.STOP_WORK_ACTION -> {
                performStopWork()
            }

            WorkTimeNotificationService.SNOOZE_TO_NEXT_ACTION -> {
                performSnoozeEndOfWorkNotificationToNextNotificationTime(intent)
            }
        }
    }

    private fun performSnoozeEndOfWorkToNextTenMinutes() {
        Log.d(
            classTag,
            "onReceive: perform action - snooze end of work notification to next 10 minutes"
        )
        val nextReminder = dateTimeProvider.currentTime.plusMinutes(10)
        Log.d(classTag, "onReceive: next notification time: $nextReminder")

        notificationService.scheduleEndOfWorkNotification(nextReminder)
        notificationService.hideEndOfWorkNotification()
    }

    @DelicateCoroutinesApi
    private fun performStopWork() {
        Log.d(classTag, "onReceive: perform action - stop work")
        GlobalScope.launch {
            comeEventUtils.registerNewEvent()
        }
    }

    private fun performSnoozeEndOfWorkNotificationToNextNotificationTime(intent: Intent) {
        Log.d(
            classTag,
            "onReceive: perform action - snooze end of work notification to next notification time"
        )
        val nextTime =
            intent.getStringExtra(NEXT_NOTIFICATION_TIME)?.let { ZonedDateTime.parse(it) }
        val standardEndTime =
            intent.getStringExtra(STANDARD_END_TIME)?.let { ZonedDateTime.parse(it) }
        val balancedEndTime =
            intent.getStringExtra(BALANCED_END_TIME)?.let { ZonedDateTime.parse(it) }
        Log.d(classTag, "onReceive: next notification time: $nextTime")

        nextTime?.let {
            notificationService.scheduleEndOfWorkNotification(
                it,
                standardEndTime,
                balancedEndTime
            )
            notificationService.hideEndOfWorkNotification()
        }
    }
}