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
import org.threeten.bp.ZonedDateTime
import javax.inject.Inject

@AndroidEntryPoint
class WorkTimeNotificationActionReceiver : HiltBroadcastReceiver() {

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

        when (intent.action) {
            WorkTimeNotificationService.SNOOZE_ACTION -> {
                val nextReminder = dateTimeProvider.currentTime.plusMinutes(10)
                notificationService.scheduleEndOfWorkNotification(nextReminder)
                notificationService.hideEndOfWorkNotification()
            }

            WorkTimeNotificationService.STOP_WORK_ACTION -> {
                GlobalScope.launch {
                    comeEventUtils.registerNewEvent()
                }
            }

            WorkTimeNotificationService.SNOOZE_TO_NEXT_ACTION -> {
                val nextTime =
                    intent.getStringExtra(NEXT_NOTIFICATION_TIME)?.let { ZonedDateTime.parse(it) }
                val standardEndTime =
                    intent.getStringExtra(STANDARD_END_TIME)?.let { ZonedDateTime.parse(it) }
                val balancedEndTime =
                    intent.getStringExtra(BALANCED_END_TIME)?.let { ZonedDateTime.parse(it) }

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
    }
}