package net.wojteksz128.worktimemeasureapp.util.comeevent

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.model.ComeEventType
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.repository.ComeEventRepository
import net.wojteksz128.worktimemeasureapp.repository.WorkDayRepository
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import java.util.*

class ComeEventUtils(
    private val comeEventRepository: ComeEventRepository,
    private val workDayRepository: WorkDayRepository,
    private val dateTimeUtils: DateTimeUtils,
    private val dateTimeProvider: DateTimeProvider
): ClassTagAware {

    // TODO: 07.07.2019 Move to separate action object.
    suspend fun registerNewEvent(): ComeEventType = withContext(Dispatchers.IO) {
        val registerDate = dateTimeProvider.currentTime
        val workDay = workDayRepository.getCurrentWorkDay(registerDate)
        val comeEvent = workDay.events.lastOrNull { !it.isEnded }

        if (comeEvent != null) {
            assignEndDateIntoCurrentEvent(comeEvent, registerDate)
        } else {
            createNewEvent(workDay, registerDate)
        }
    }

    private fun assignEndDateIntoCurrentEvent(comeEvent: ComeEvent, registerDate: Date): ComeEventType {
        comeEvent.endDate = registerDate
        comeEvent.durationMillis = dateTimeUtils.calculateDuration(comeEvent).toMillis()
        comeEventRepository.save(comeEvent)
        return ComeEventType.COME_OUT
    }

    private fun createNewEvent(workDay: WorkDay, registerDate: Date): ComeEventType {
        val comeEvent = ComeEvent(registerDate, workDay)
        comeEventRepository.save(comeEvent)
        return ComeEventType.COME_IN
    }
}
