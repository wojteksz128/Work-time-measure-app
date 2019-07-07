package net.wojteksz128.worktimemeasureapp.util

import android.arch.core.util.Function
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

    fun registerNewEvent(context: Context, preFunction: Function<Void, Void>, postFunction: Function<ComeEventType, Void>) {
        val comeEventDao = AppDatabase.getInstance(context).comeEventDao()
        val registerDate = Date()

        object : AsyncTask<Void, Void, ComeEventType>() {

            override fun onPreExecute() {
                preFunction.apply(null)
            }

            override fun doInBackground(vararg voids: Void): ComeEventType {
                val comeEventType: ComeEventType
                val workDay = getCurrentWorkDay(registerDate, context)

                if (isFirstWorkDayEvent(workDay)) {
                    comeEventType = createNewEvent(workDay, registerDate, comeEventDao)
                } else {
                    val comeEvent = workDay.events[0]
                    if (comeEvent.endDate != null) {
                        comeEventType = createNewEvent(workDay, registerDate, comeEventDao)
                    } else {
                        comeEventType = assignEndDateIntoCurrentEvent(comeEvent, registerDate, comeEventDao)
                    }
                }

                return comeEventType
            }

            override fun onPostExecute(comeEventType: ComeEventType) {
                postFunction.apply(comeEventType)
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

    private fun isFirstWorkDayEvent(workDay: WorkDayEvents): Boolean {
        return workDay.events == null || workDay.events.isEmpty()
    }

    private fun getCurrentWorkDay(registerDate: Date, context: Context): WorkDayEvents {
        val workDayDao = AppDatabase.getInstance(context).workDayDao()
        var workDay: WorkDayEvents? = workDayDao.findByIntervalContains(registerDate)

        if (workDay == null) {
            workDay = createNewWorkDay(registerDate, workDayDao)
        }
        return workDay
    }

    private fun createNewWorkDay(registerDate: Date, workDayDao: WorkDayDao): WorkDayEvents {
        val workDay = WorkDayEvents()

        workDay.workDay = WorkDay(registerDate)
        workDay.events = ArrayList()
        val insertedWorkdayId = workDayDao.insert(workDay.workDay)
        workDay.workDay = WorkDay(insertedWorkdayId!!.toInt(), workDay.workDay.date,
                workDay.workDay.beginSlot, workDay.workDay.endSlot,
                workDay.workDay.percentDeclaredTime)
        return workDay
    }
}
