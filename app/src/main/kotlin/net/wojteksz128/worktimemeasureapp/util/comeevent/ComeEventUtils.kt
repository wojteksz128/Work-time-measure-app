package net.wojteksz128.worktimemeasureapp.util.comeevent

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.wojteksz128.worktimemeasureapp.database.AppDatabase
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventDao
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventMapper
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayDao
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayMapper
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayWithEventsMapper
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.model.ComeEventType
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import java.util.*
import javax.inject.Inject

class ComeEventUtils @Inject constructor(
    private val workDayMapper: WorkDayMapper,
    private val workDayWithEventsMapper: WorkDayWithEventsMapper,
    private val comeEventMapper: ComeEventMapper
): ClassTagAware {

    // TODO: 07.07.2019 Move to separate action object.
    suspend fun registerNewEvent(context: Context): ComeEventType = withContext(Dispatchers.IO) {
        val comeEventDao = AppDatabase.getInstance(context).comeEventDao()
        val registerDate = DateTimeProvider.currentTime
        val workDay = getCurrentWorkDay(registerDate, context)
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

    // TODO: 05.10.2021 move to repository
    private fun getCurrentWorkDay(registerDate: Date, context: Context): WorkDay {
        val workDayDao = AppDatabase.getInstance(context).workDayDao()
        val entity = workDayDao.findByIntervalContains(registerDate)
        var workDay: WorkDay? = entity?.let { workDayWithEventsMapper.mapToDomainModel(it) }

        if (workDay == null) {
            createNewWorkDay(registerDate, workDayDao)
            workDay = workDayWithEventsMapper.mapToDomainModel(workDayDao.findByIntervalContains(registerDate)!!)
        }
        return workDay
    }

    // TODO: 05.10.2021 move to repository
    private fun createNewWorkDay(registerDate: Date, workDayDao: WorkDayDao) {
        val workDay = WorkDay(registerDate)

        workDayDao.insert(workDayMapper.mapFromDomainModel(workDay))
    }
}
