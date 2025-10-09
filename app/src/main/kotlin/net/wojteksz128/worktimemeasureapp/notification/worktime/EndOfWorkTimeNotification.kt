package net.wojteksz128.worktimemeasureapp.notification.worktime

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.notification.AppNotification
import net.wojteksz128.worktimemeasureapp.notification.Channel
import net.wojteksz128.worktimemeasureapp.notification.worktime.action.WorkTimeNotificationActionReceiver
import net.wojteksz128.worktimemeasureapp.window.dashboard.DashboardActivity

class EndOfWorkTimeNotification(
    private val context: Context,
) : AppNotification(Channel.END_WORK_TIME_CHANNEL, NOTIFICATION_ID, context) {

    companion object {
        const val NOTIFICATION_ID = 251
    }

    override fun build(): Notification {
        return notificationBuilder.setContentTitle(context.getString(R.string.notification_end_of_work_title))
            .setContentText(context.getString(R.string.notification_end_of_work_text))
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(context.getString(R.string.notification_end_of_work_text)))
            .setDefaults(Notification.DEFAULT_ALL)
            .setPriority(channel.importance)
            .setContentIntent(getPendingIntentWithStack(context, DashboardActivity::class.java))
            .addAction(
                R.drawable.ic_baseline_snooze_24,
                context.getString(R.string.notification_action_snooze),
                createActionIntent(WorkTimeNotificationService.SNOOZE_ACTION)
            )
            .addAction(
                R.drawable.ic_baseline_work_off_24,
                context.getString(R.string.notification_action_stop_work),
                createActionIntent(WorkTimeNotificationService.STOP_WORK_ACTION)
            )
            .addAction(
                R.drawable.ic_baseline_timelapse_24,
                context.getString(R.string.notification_action_extend),
                createActionIntent(WorkTimeNotificationService.EXTEND)
            )
            .build()
    }

    private fun createActionIntent(action: String): PendingIntent {
        val intent = Intent(context, WorkTimeNotificationActionReceiver::class.java).apply {
            this.action = action
        }
        return PendingIntent.getBroadcast(
            context,
            action.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}
