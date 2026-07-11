package net.wojteksz128.worktimemeasureapp.notification.worktime

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import androidx.annotation.StringRes
import androidx.core.app.NotificationManagerCompat
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.model.WorkState
import net.wojteksz128.worktimemeasureapp.notification.AppNotification
import net.wojteksz128.worktimemeasureapp.notification.Channel
import net.wojteksz128.worktimemeasureapp.notification.worktime.action.WorkTimeNotificationActionReceiver
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import net.wojteksz128.worktimemeasureapp.util.datetime.min
import net.wojteksz128.worktimemeasureapp.window.dashboard.DashboardActivity
import org.threeten.bp.ZonedDateTime

open class WorkTimeInProgressNotification(
    context: Context,
    private val inProgressState: WorkState.InProgress,
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
        val formattedStandardEnd = getFormattedTimeFor(inProgressState.standardWorkTime.endTime)
        val formattedBalancedEnd = getFormattedTimeFor(inProgressState.balancedWorkTime.endTime)

        val (currentProgress, maxProgress) = calculateCurrentProgress()

        val contentText = context.getString(
            R.string.notification_work_in_progress_text,
            formattedStandardEnd,
            formattedBalancedEnd
        )
        return notificationBuilder.setContentTitle(context.getString(R.string.notification_work_in_progress_title))
            .setContentText(contentText)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setProgress(maxProgress, currentProgress.coerceAtMost(maxProgress), false)
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

    private fun calculateCurrentProgress(): Pair<Int, Int> {
        val maxProgress = min(
            inProgressState.standardWorkTime.requiredTime,
            inProgressState.balancedWorkTime.requiredTime
        ).seconds.toInt()
        val currentProgress = inProgressState.todayWorkTime.seconds.toInt()

        return currentProgress to maxProgress
    }

    private fun getFormattedTimeFor(dateTime: ZonedDateTime): String {
        val timeFormat = context.getString(getTimeFormatFor(dateTime))
        return dateTimeUtils.formatDate(timeFormat, dateTime)
    }

    @StringRes
    private fun getTimeFormatFor(dateTime: ZonedDateTime): Int =
        if (dateTime.toLocalDate() == inProgressState.workDay.date) R.string.notification_time_short_format
        else R.string.notification_time_long_format
}
