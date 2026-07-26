package net.wojteksz128.worktimemeasureapp.util.comeevent

import android.content.Context
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.model.ComeEventType
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.notification.worktime.WorkTimeTrackerService
import net.wojteksz128.worktimemeasureapp.repository.ComeEventRepository
import net.wojteksz128.worktimemeasureapp.repository.WorkDayRepository
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.model.extension.ComeEventExtensions.isEnded
import org.threeten.bp.ZonedDateTime

open class ComeEventUtils(
    @param:ApplicationContext private val context: Context,
    private val comeEventRepository: ComeEventRepository,
    private val workDayRepository: WorkDayRepository,
    private val dateTimeProvider: DateTimeProvider,
): ClassTagAware {

    // TODO: 07.07.2019 Move to separate action object or state action.
    open suspend fun registerNewEvent(): ComeEventType = withContext(Dispatchers.IO) {
        val registerTime = dateTimeProvider.currentTime
        val workDay =
            workDayRepository.getWorkDayByDate(registerTime.toLocalDate())
                ?: WorkDay(registerTime.toLocalDate()).let {
                workDayRepository.save(it)
                    workDayRepository.getWorkDayByDate(registerTime.toLocalDate())!!
            }
        val comeEvent = workDay.events.lastOrNull { !it.isEnded }

        val eventType = if (comeEvent != null) {
            assignEndDateIntoCurrentEvent(comeEvent, registerTime)
        } else {
            createNewEvent(workDay, registerTime)
        }

        when (eventType) {
            ComeEventType.COME_IN -> startTrackingService()
            ComeEventType.COME_OUT -> stopTrackingService()
        }

        return@withContext eventType
    }

    private suspend fun assignEndDateIntoCurrentEvent(
        comeEvent: ComeEvent,
        registerDate: ZonedDateTime,
    ): ComeEventType {
        comeEvent.endDate = registerDate
        comeEventRepository.save(comeEvent)
        return ComeEventType.COME_OUT
    }

    private suspend fun createNewEvent(
        workDay: WorkDay,
        registerDate: ZonedDateTime,
    ): ComeEventType {
        val comeEvent = ComeEvent(registerDate, workDay)
        comeEventRepository.save(comeEvent)
        return ComeEventType.COME_IN
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
