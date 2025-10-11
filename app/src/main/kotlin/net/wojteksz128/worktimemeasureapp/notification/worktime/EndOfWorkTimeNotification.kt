package net.wojteksz128.worktimemeasureapp.notification.worktime

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.notification.AppNotification
import net.wojteksz128.worktimemeasureapp.notification.Channel
import net.wojteksz128.worktimemeasureapp.notification.worktime.action.WorkTimeNotificationActionReceiver
import net.wojteksz128.worktimemeasureapp.notification.worktime.action.WorkTimeNotificationActionReceiver.Companion.BALANCED_END_TIME
import net.wojteksz128.worktimemeasureapp.notification.worktime.action.WorkTimeNotificationActionReceiver.Companion.NEXT_NOTIFICATION_TIME
import net.wojteksz128.worktimemeasureapp.notification.worktime.action.WorkTimeNotificationActionReceiver.Companion.STANDARD_END_TIME
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.window.dashboard.DashboardActivity
import org.threeten.bp.ZonedDateTime

class EndOfWorkTimeNotification(
    context: Context,
    private val dateTimeProvider: DateTimeProvider,
    private val standardEndTime: ZonedDateTime? = null,
    private val balancedEndTime: ZonedDateTime? = null,
) : AppNotification(Channel.END_WORK_TIME_CHANNEL, NOTIFICATION_ID, context) {

    override val actionReceiver: Class<out BroadcastReceiver>
        get() = WorkTimeNotificationActionReceiver::class.java

    companion object {
        const val NOTIFICATION_ID = 251

        fun cancel(context: Context) {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.cancel(NOTIFICATION_ID)
        }
    }

    override fun build(): Notification {
        notificationBuilder.setContentTitle(context.getString(R.string.notification_end_of_work_title))
            .setContentText(context.getString(R.string.notification_end_of_work_text))
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(context.getString(R.string.notification_end_of_work_text)))
            .setDefaults(Notification.DEFAULT_ALL)
            .setPriority(channel.importance)
            .setContentIntent(createNotificationIntent(context, DashboardActivity::class.java))
            .addAction(
                R.drawable.ic_baseline_work_off_24,
                context.getString(R.string.notification_action_stop_work),
                createActionIntent(WorkTimeNotificationService.STOP_WORK_ACTION)
            )

        if (standardEndTime != null && balancedEndTime != null) {
            val triggeredTime = dateTimeProvider.currentTime
            val nextTime = listOf(standardEndTime, balancedEndTime).sorted()
                .find { it.isAfter(triggeredTime) }

            nextTime?.let {

                notificationBuilder.addAction(
                    R.drawable.ic_baseline_timelapse_24,
                    context.getString(R.string.notification_action_snooze_to_next),
                    createActionIntent(WorkTimeNotificationService.SNOOZE_TO_NEXT_ACTION) {
                        this.putExtra(NEXT_NOTIFICATION_TIME, nextTime.toString())
                        this.putExtra(STANDARD_END_TIME, standardEndTime.toString())
                        this.putExtra(BALANCED_END_TIME, balancedEndTime.toString())
                    }
                )
            }
        }


        notificationBuilder.addAction(
            R.drawable.ic_baseline_snooze_24,
            context.getString(R.string.notification_action_snooze),
            createActionIntent(WorkTimeNotificationService.SNOOZE_ACTION)
        )

        return notificationBuilder.build()
    }

//    private fun createActionIntent(
//        action: String,
//        intentConfig: (Intent.() -> Unit)? = null,
//    ): PendingIntent {
//        val intent = Intent(context, WorkTimeNotificationActionReceiver::class.java).apply {
//            this.action = action
//        }
//        intentConfig?.let { intent.it() }
//        return PendingIntent.getBroadcast(
//            context,
//            action.hashCode(),
//            intent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//    }
}
