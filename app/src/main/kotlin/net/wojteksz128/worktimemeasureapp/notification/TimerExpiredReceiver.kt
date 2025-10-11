package net.wojteksz128.worktimemeasureapp.notification

import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import net.wojteksz128.worktimemeasureapp.notification.worktime.WorkTimeNotificationService
import javax.inject.Inject

@AndroidEntryPoint
class TimerExpiredReceiver : HiltBroadcastReceiver() {
    @Inject
    lateinit var notificationService: WorkTimeNotificationService

    companion object {
        const val REQUEST_CODE = 12345
    }

    // TODO: 21.09.2021 Register expired recipients
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        notificationService.showEndOfWorkNotification()
    }
}