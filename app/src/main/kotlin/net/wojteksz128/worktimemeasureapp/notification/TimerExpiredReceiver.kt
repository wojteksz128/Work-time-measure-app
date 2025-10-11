package net.wojteksz128.worktimemeasureapp.notification

import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import net.wojteksz128.worktimemeasureapp.notification.worktime.WorkTimeNotificationService
import org.threeten.bp.ZonedDateTime
import javax.inject.Inject

@AndroidEntryPoint
class TimerExpiredReceiver : HiltBroadcastReceiver() {
    @Inject
    lateinit var notificationService: WorkTimeNotificationService

    companion object {
        const val REQUEST_CODE = 12345
        const val EXTRA_STANDARD_END_TIME = "standardEndTime"
        const val EXTRA_BALANCED_END_TIME = "balancedEndTime"
    }

    // TODO: 21.09.2021 Register expired recipients
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val standardEndTimeStr = intent.getStringExtra(EXTRA_STANDARD_END_TIME)
        val balancedEndTimeStr = intent.getStringExtra(EXTRA_BALANCED_END_TIME)
        if (standardEndTimeStr != null || balancedEndTimeStr != null) {
            val standardEndTime = ZonedDateTime.parse(standardEndTimeStr)
            val balancedEndTime = ZonedDateTime.parse(balancedEndTimeStr)
            notificationService.showEndOfWorkNotification(standardEndTime, balancedEndTime)
        } else notificationService.showEndOfWorkNotification()

    }
}