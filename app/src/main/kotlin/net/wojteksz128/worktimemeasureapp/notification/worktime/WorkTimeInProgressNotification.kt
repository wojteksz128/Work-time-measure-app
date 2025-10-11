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
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import net.wojteksz128.worktimemeasureapp.util.datetime.WorkTimeBalance
import net.wojteksz128.worktimemeasureapp.window.dashboard.DashboardActivity
import org.threeten.bp.Duration
import org.threeten.bp.ZonedDateTime

class WorkTimeInProgressNotification(
    context: Context,
    private val workDay: WorkDay,
    private val workTimeBalance: WorkTimeBalance,
    private val dateTimeUtils: DateTimeUtils,
    private val dateTimeProvider: DateTimeProvider,
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
        val formattedStandardEnd = getFormattedTimeFor(WorkTimeBalance::getStandardEndTime)
        val formattedBalancedEnd = getFormattedTimeFor(WorkTimeBalance::getBalancedEndTime)

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
        val now = dateTimeProvider.currentTime

        val startTime = workDay.events.lastOrNull { !it.isEnded }?.startDate

        val standardEndTime = workTimeBalance.getStandardEndTime(workDay)
        val balancedEndTime = workTimeBalance.getBalancedEndTime(workDay)

        val targetEndTime = listOf(standardEndTime, balancedEndTime)
            .filter { it.isAfter(now) }
            .minOrNull()

        var maxProgress = workTimeBalance.requiredToday.seconds.toInt()
        var currentProgress = workTimeBalance.todayWorkTime.seconds.toInt()

        if (startTime != null && targetEndTime != null) {
            val totalDurationNeeded = Duration.between(startTime, targetEndTime).seconds.toInt()
            val timeElapsedSinceStart = Duration.between(startTime, now).seconds.toInt()

            if (totalDurationNeeded > 0) {
                maxProgress = totalDurationNeeded
                currentProgress = timeElapsedSinceStart
            }
        }

        return currentProgress to maxProgress
    }

    private fun getFormattedTimeFor(getter: WorkTimeBalance.(WorkDay) -> ZonedDateTime): String {
        val endTime = workTimeBalance.getter(workDay)
        return dateTimeUtils.formatDate(
            context.getString(getTimeFormatFor(endTime)),
            endTime
        )
    }

    @StringRes
    private fun getTimeFormatFor(dateTime: ZonedDateTime): Int =
        if (dateTime.toLocalDate() == workDay.date) R.string.notification_time_short_format
        else R.string.notification_time_long_format
}
