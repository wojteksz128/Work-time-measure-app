package net.wojteksz128.worktimemeasureapp.util.comeevent

import android.content.Context
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.model.ComeEventType
import net.wojteksz128.worktimemeasureapp.model.WorkState
import net.wojteksz128.worktimemeasureapp.notification.worktime.WorkTimeTrackerService
import net.wojteksz128.worktimemeasureapp.repository.ComeEventRepository
import net.wojteksz128.worktimemeasureapp.repository.WorkDayRepository
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider

open class ToggleWorkStateUseCase(
    @param:ApplicationContext private val context: Context,
    private val comeEventRepository: ComeEventRepository,
    private val workDayRepository: WorkDayRepository,
    private val dateTimeProvider: DateTimeProvider,
): ClassTagAware {

    open suspend operator fun invoke(currentState: WorkState.Loaded): ComeEventType =
        withContext(Dispatchers.IO) {
            val registerTime = dateTimeProvider.currentTime

            return@withContext when (currentState) {
                is WorkState.InProgress -> {
                    val currentEvent = currentState.nonFinishedEvent
                    currentEvent.endDate = registerTime
                    comeEventRepository.save(currentEvent)

                    stopTrackingService()
                    ComeEventType.COME_OUT
                }

                is WorkState.NotStarted, is WorkState.Finished -> {
                    val savedWorkDay =
                        if (currentState.workDay.id == null) workDayRepository.save(currentState.workDay) else currentState.workDay
                    workDayRepository.save(currentState.workDay)
                    val newEvent = ComeEvent(registerTime, savedWorkDay)
                    comeEventRepository.save(newEvent)

                    startTrackingService()
                    ComeEventType.COME_IN
                }
            }
    }

    private fun startTrackingService() {
        doOnTrackingService(WorkTimeTrackerService.ACTION_START)
    }

    private fun stopTrackingService() {
        doOnTrackingService(WorkTimeTrackerService.ACTION_STOP)
    }

    private fun doOnTrackingService(action: String) {
        val intent = Intent(context, WorkTimeTrackerService::class.java).apply {
            this.action = action
        }
        context.startService(intent)
    }
}
