package net.wojteksz128.worktimemeasureapp.module.dayOff

import net.wojteksz128.worktimemeasureapp.model.fieldType.DayType
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import java.util.*

class DayOffService(private val Settings: Settings) : ClassTagAware {

    fun getDayType(date: Date): DayType {
        return if (isWorkingDay(date))
            DayType.WorkDay
        else
            DayType.Weekend
    }

    private fun isWorkingDay(date: Date): Boolean {
        val dayOfWeekIndex = Calendar.getInstance().apply {
            time = date
        }[Calendar.DAY_OF_WEEK]
        val daysOfWorkingWeek = Settings.WorkTime.Week.DaysOfWorkingWeek.value

        return daysOfWorkingWeek.map { it.toInt() }.any { it == dayOfWeekIndex }
    }
}