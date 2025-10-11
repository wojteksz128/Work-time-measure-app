package net.wojteksz128.worktimemeasureapp.notification.worktime

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.util.android.before
import net.wojteksz128.worktimemeasureapp.util.android.fromVersion
import net.wojteksz128.worktimemeasureapp.util.datetime.WorkTimeBalance
import javax.inject.Inject

@AndroidEntryPoint
class WorkTimeTrackerService : Service() {

    @Inject
    lateinit var notificationFactory: WorkTimeNotificationFactory

    private var currentWorkDay: WorkDay? = null
    private var currentWorkTimeBalance: WorkTimeBalance? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val (day, balance) = fromVersion(Build.VERSION_CODES.TIRAMISU) {
            Pair(
                intent?.getParcelableExtra(EXTRA_WORK_DAY, WorkDay::class.java),
                intent?.getParcelableExtra(EXTRA_WORK_TIME_BALANCE, WorkTimeBalance::class.java)
            )
        } before {
            @Suppress("DEPRECATION")
            Pair(
                intent?.getParcelableExtra(EXTRA_WORK_DAY),
                intent?.getParcelableExtra(EXTRA_WORK_TIME_BALANCE)
            )
        }
        currentWorkDay = day
        currentWorkTimeBalance = balance

        when (intent?.action) {
            ACTION_START -> {
                val notification = notificationFactory.createWorkInProgressNotification(
                    currentWorkDay!!,
                    currentWorkTimeBalance!!
                ).build()
                startForeground(WorkTimeInProgressNotification.NOTIFICATION_ID, notification)
            }

            ACTION_UPDATE -> {

            }

            ACTION_STOP -> {
                fromVersion(Build.VERSION_CODES.N) {
                    stopForeground(STOP_FOREGROUND_REMOVE)
                } before {
                    @Suppress("DEPRECATION")
                    stopForeground(true)
                }
                stopSelf()
            }
        }
        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? = null

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_UPDATE = "ACTION_UPDATE"
        const val ACTION_STOP = "ACTION_STOP"
        const val EXTRA_WORK_DAY = "EXTRA_WORK_DAY"
        const val EXTRA_WORK_TIME_BALANCE = "EXTRA_WORK_TIME_BALANCE"
    }
}
