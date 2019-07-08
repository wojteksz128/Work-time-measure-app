package net.wojteksz128.worktimemeasureapp.database.workDay

import java.util.*

object WorkDayUtils {

    fun calculateBeginSlot(date: Date): Date {
        val calendar = Calendar.getInstance()

        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return calendar.time
    }

    fun calculateEndSlot(date: Date): Date {
        val calendar = Calendar.getInstance()

        calendar.time = calculateBeginSlot(date)
        calendar.add(Calendar.DATE, 1)
        calendar.add(Calendar.MILLISECOND, -1)

        return calendar.time
    }
}
