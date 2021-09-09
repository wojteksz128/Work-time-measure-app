package net.wojteksz128.worktimemeasureapp.util.comeevent

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.wojteksz128.worktimemeasureapp.database.AppDatabase
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEvent
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventDao
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventType
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDay
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayDao
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayEvents
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import java.util.*

object ComeEventUtils : ClassTagAware {

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
        comeEvent.duration = DateTimeUtils.calculateDuration(comeEvent)
        comeEventDao.update(comeEvent)
        return ComeEventType.COME_OUT
    }

    private fun createNewEvent(workDay: WorkDayEvents, registerDate: Date, comeEventDao: ComeEventDao): ComeEventType {
        comeEventDao.insert(ComeEvent(registerDate, workDay.workDay))
        return ComeEventType.COME_IN
    }

    private fun getCurrentWorkDay(registerDate: Date, context: Context): WorkDayEvents {
        val workDayDao = AppDatabase.getInstance(context).workDayDao()
        var workDay: WorkDayEvents? = workDayDao.findByIntervalContains(registerDate)

        if (workDay == null) {
            createNewWorkDay(registerDate, workDayDao)
            workDay = workDayDao.findByIntervalContains(registerDate)
        }
        return workDay
    }

    private fun createNewWorkDay(registerDate: Date, workDayDao: WorkDayDao) {
        val workDay = WorkDay(registerDate)

        workDayDao.insert(workDay)
    }
}
