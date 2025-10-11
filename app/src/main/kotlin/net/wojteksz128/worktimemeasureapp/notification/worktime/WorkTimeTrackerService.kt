package net.wojteksz128.worktimemeasureapp.notification.worktime

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import net.wojteksz128.worktimemeasureapp.model.WorkState
import javax.inject.Inject

@AndroidEntryPoint
class WorkTimeTrackerService : Service() {

    @Inject
    lateinit var notificationFactory: WorkTimeNotificationFactory

    @Inject
    lateinit var workStateFlow: StateFlow<WorkState?>

    private var serviceJob: Job? = null
    private var isServiceRunning = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> if (serviceJob?.isActive != true)
                serviceJob = CoroutineScope(Dispatchers.Main).launch {
                    workStateFlow.collect { workState ->
                        workState?.let {
                            if (!it.workDay.isWorkFinished()) updateNotification(it)
                            else stopSelf()
                        }
                    }
                }

            ACTION_STOP -> stopSelf()
        }
        return START_NOT_STICKY
    }

    private fun updateNotification(workState: WorkState) {
        val notification = notificationFactory.createWorkInProgressNotification(
            workState.workDay,
            workState.workTimeBalance
        ).build()

        if (!isServiceRunning) {
            startForeground(WorkTimeInProgressNotification.NOTIFICATION_ID, notification)
            isServiceRunning = true
        } else {
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(WorkTimeInProgressNotification.NOTIFICATION_ID, notification)
        }
    }

    override fun onDestroy() {
        serviceJob?.cancel()
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? = null

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
}
