package net.wojteksz128.worktimemeasureapp.notification.worktime

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import androidx.annotation.StringRes
import androidx.core.app.NotificationManagerCompat
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.notification.AppNotification
import net.wojteksz128.worktimemeasureapp.notification.Channel
import net.wojteksz128.worktimemeasureapp.notification.worktime.action.WorkTimeNotificationActionReceiver
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import net.wojteksz128.worktimemeasureapp.util.datetime.WorkTimeBalance
import net.wojteksz128.worktimemeasureapp.window.dashboard.DashboardActivity
import org.threeten.bp.ZonedDateTime

class WorkTimeInProgressNotification(
    context: Context,
    private val workDay: WorkDay,
    private val workTimeBalance: WorkTimeBalance,
    private val dateTimeUtils: DateTimeUtils,
) : AppNotification(Channel.WORK_TIME_IN_PROGRESS_CHANNEL, NOTIFICATION_ID, context) {

    override val actionReceiver: Class<out BroadcastReceiver>
        get() = WorkTimeNotificationActionReceiver::class.java

    companion object {
        const val NOTIFICATION_ID = 11

        fun cancel(context: Context) {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.cancel(NOTIFICATION_ID)
        }
    }

    override fun build(): Notification {
        val standardEndTime = workTimeBalance.getStandardEndTime(workDay)
        val balancedEndTime = workTimeBalance.getBalancedEndTime(workDay)

        val formattedStandardEnd = dateTimeUtils.formatDate(
            context.getString(getTimeFormatFor(standardEndTime)),
            standardEndTime
        )
        val formattedBalancedEnd = dateTimeUtils.formatDate(
            context.getString(getTimeFormatFor(balancedEndTime)),
            balancedEndTime
        )

        val maxProgress = workTimeBalance.requiredToday.seconds.toInt()
        val currentProgress = workTimeBalance.todayWorkTime.seconds.toInt()

        val contentText = context.getString(
            R.string.notification_work_in_progress_text,
            formattedStandardEnd,
            formattedBalancedEnd
        )
        return notificationBuilder.setContentTitle(context.getString(R.string.notification_work_in_progress_title))
            .setContentText(contentText)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setProgress(maxProgress, currentProgress, false)
            .setContentIntent(createNotificationIntent(context, DashboardActivity::class.java))
            .addAction(
                R.drawable.ic_baseline_work_off_24,
                context.getString(R.string.notification_action_stop_work),
                createActionIntent(
                    WorkTimeNotificationService.STOP_WORK_ACTION
                )
            )
            .build()
    }

    @StringRes
    private fun getTimeFormatFor(dateTime: ZonedDateTime): Int =
        if (dateTime.toLocalDate() == workDay.date) R.string.notification_time_short_format
        else R.string.notification_time_long_format
}
