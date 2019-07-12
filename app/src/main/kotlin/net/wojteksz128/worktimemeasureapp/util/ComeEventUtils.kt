package net.wojteksz128.worktimemeasureapp.util

import android.content.Context
import android.os.AsyncTask
import net.wojteksz128.worktimemeasureapp.database.AppDatabase
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEvent
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventDao
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventType
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDay
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayDao
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayEvents
import java.util.*

object ComeEventUtils {

    // TODO: 07.07.2019 Move to separate action object.
    fun registerNewEvent(context: Context, preFunction: () -> Unit, postFunction: (ComeEventType) -> Unit) {
        val comeEventDao = AppDatabase.getInstance(context).comeEventDao()
        val registerDate = DateTimeProvider.currentTime

        object : AsyncTask<Unit, Unit, ComeEventType>() {

            override fun onPreExecute() {
                preFunction()
            }

            override fun doInBackground(vararg voids: Unit): ComeEventType {
                val workDay = getCurrentWorkDay(registerDate, context)
                val comeEvent = workDay.events.lastOrNull { !it.isEnded }

                return if (comeEvent != null) {
                    assignEndDateIntoCurrentEvent(comeEvent, registerDate, comeEventDao)
                } else {
                    createNewEvent(workDay, registerDate, comeEventDao)
                }
            }

            override fun onPostExecute(comeEventType: ComeEventType) {
                postFunction(comeEventType)
            }
        }.execute()
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
