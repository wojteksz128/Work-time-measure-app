package net.wojteksz128.worktimemeasureapp.util.comeevent

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventDao
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventMapper
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.model.ComeEventType
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.repository.WorkDayRepository
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import java.util.*
import javax.inject.Inject

class ComeEventUtils @Inject constructor(
    private val comeEventMapper: ComeEventMapper,
    private val comeEventDao: ComeEventDao,
    private val workDayRepository: WorkDayRepository
): ClassTagAware {

    // TODO: 07.07.2019 Move to separate action object.
    suspend fun registerNewEvent(): ComeEventType = withContext(Dispatchers.IO) {
        val registerDate = DateTimeProvider.currentTime
        val workDay = workDayRepository.getCurrentWorkDay(registerDate)
        val comeEvent = workDay.events.lastOrNull { !it.isEnded }

        if (comeEvent != null) {
            assignEndDateIntoCurrentEvent(comeEvent, registerDate, comeEventDao)
        } else {
            createNewEvent(workDay, registerDate, comeEventDao)
        }
    }

    private fun assignEndDateIntoCurrentEvent(comeEvent: ComeEvent, registerDate: Date, comeEventDao: ComeEventDao): ComeEventType {
        comeEvent.endDate = registerDate
        comeEvent.durationMillis = DateTimeUtils.calculateDuration(comeEvent).toMillis()
        comeEventDao.update(comeEventMapper.mapFromDomainModel(comeEvent))
        return ComeEventType.COME_OUT
    }

    private fun createNewEvent(workDay: WorkDay, registerDate: Date, comeEventDao: ComeEventDao): ComeEventType {
        val comeEvent = ComeEvent(registerDate, workDay)
        comeEventDao.insert(comeEventMapper.mapFromDomainModel(comeEvent))
        return ComeEventType.COME_IN
    }
}
