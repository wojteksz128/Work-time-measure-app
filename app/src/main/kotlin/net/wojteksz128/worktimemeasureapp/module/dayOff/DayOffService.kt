package net.wojteksz128.worktimemeasureapp.module.dayOff

import net.wojteksz128.worktimemeasureapp.api.HolidayProvider
import net.wojteksz128.worktimemeasureapp.model.DayOff
import net.wojteksz128.worktimemeasureapp.model.fieldType.DayType
import net.wojteksz128.worktimemeasureapp.repository.DayOffRepository
import net.wojteksz128.worktimemeasureapp.repository.api.ExternalHolidayRepositoriesFacade
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import java.util.Calendar
import java.util.Date

class DayOffService(
    private val dayOffRepository: DayOffRepository,
    private val externalHolidayRepositoriesFacade: ExternalHolidayRepositoriesFacade,
    private val Settings: Settings,
) : ClassTagAware {

    suspend fun getDayType(date: Date): DayType {
        return getDayOffInDate(date)?.let {
            DayType.ofDayOff(it)
        } ?: if (isWorkingDay(date))
            DayType.WorkDay
        else
            DayType.Weekend
    }

    private suspend fun getDayOffInDate(date: Date): DayOff? {
        return dayOffRepository.getDayOff(date)
    }

    private fun isWorkingDay(date: Date): Boolean {
        val dayOfWeekIndex = Calendar.getInstance().apply {
            time = date
        }[Calendar.DAY_OF_WEEK]
        val daysOfWorkingWeek = Settings.WorkTime.Week.DaysOfWorkingWeek.value

        return daysOfWorkingWeek.map { it.toInt() }.any { it == dayOfWeekIndex }
    }

    suspend fun syncHolidaysWith(holidayProvider: HolidayProvider) {
        externalHolidayRepositoriesFacade.forAPI(holidayProvider)
            .getHolidays().forEach {
                dayOffRepository.save(it)
            }
    }
}