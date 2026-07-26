package net.wojteksz128.worktimemeasureapp.notification.worktime

import android.app.NotificationManager
import android.content.Intent
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import net.wojteksz128.worktimemeasureapp.model.WorkState
import javax.inject.Inject

@AndroidEntryPoint
class WorkTimeTrackerService : LifecycleService() {

    @Inject
    lateinit var notificationFactory: WorkTimeNotificationFactory

    @Inject
    lateinit var workStateFlow: StateFlow<@JvmSuppressWildcards WorkState>

    @Inject
    lateinit var notificationService: WorkTimeNotificationService

    private var serviceJob: Job? = null
    private var isServiceRunning = false

    override fun onCreate() {
        super.onCreate()

        lifecycleScope.launch {
            workStateFlow.collect { workState ->
                handleNotificationsForState(workState)
            }
        }
    }

    private fun handleNotificationsForState(workState: WorkState) {
        when (workState) {
            is WorkState.InProgress -> {
                updateInProgressNotification(workState)

                notificationService.scheduleEndOfWorkNotification(workState)
            }

            is WorkState.Finished, is WorkState.NotStarted -> {
                notificationService.cancelEndOfWorkNotification()
                stopSelf()
            }

            is WorkState.Loading -> Unit
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        when (intent?.action) {
            ACTION_START -> if (serviceJob?.isActive != true)
                serviceJob = CoroutineScope(Dispatchers.Main).launch {
                    workStateFlow.collect { workState ->
                        handleNotificationsForState(workState)
                    }
                }

            ACTION_STOP -> {
                notificationService.cancelEndOfWorkNotification()
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    private fun updateInProgressNotification(inProgressState: WorkState.InProgress) {
        val notification = notificationFactory.createWorkInProgressNotification(
            inProgressState
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
        notificationService.cancelEndOfWorkNotification()
        serviceJob?.cancel()
        super.onDestroy()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
}
