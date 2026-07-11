package net.wojteksz128.worktimemeasureapp.service

import android.util.Log
import net.wojteksz128.worktimemeasureapp.api.HolidayProvider
import net.wojteksz128.worktimemeasureapp.model.DayOff
import net.wojteksz128.worktimemeasureapp.model.fieldType.DayType
import net.wojteksz128.worktimemeasureapp.repository.DayOffRepository
import net.wojteksz128.worktimemeasureapp.repository.api.ExternalHolidayRepositoriesFacade
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.ZonedDateTime

open class DayOffService(
    private val dayOffRepository: DayOffRepository,
    private val externalHolidayRepositoriesFacade: ExternalHolidayRepositoriesFacade,
    @Suppress("PrivatePropertyName") private val Settings: Settings,
) : ClassTagAware {

    open suspend fun getDayType(date: ZonedDateTime): DayType = getDayType(date.toLocalDate())

    open suspend fun getDayType(date: LocalDate): DayType {
        return getDayOffInDate(date)?.let {
            DayType.ofDayOff(it)
        } ?: if (isWorkingDay(date))
            DayType.WorkDay
        else
            DayType.Weekend
    }

    private suspend fun getDayOffInDate(date: LocalDate): DayOff? {
        return dayOffRepository.getDayOff(date)
    }

    private fun isWorkingDay(date: LocalDate): Boolean {
        val dayOfWeek = date.dayOfWeek
        val daysOfWorkingWeek =
            Settings.WorkTime.Week.DaysOfWorkingWeek.value.map { DayOfWeek.valueOf(it) }

        return daysOfWorkingWeek.any { it == dayOfWeek }
    }

    suspend fun syncHolidaysWith(holidayProvider: HolidayProvider) {
        val holidayRepository = externalHolidayRepositoriesFacade.forAPI(holidayProvider)
        holidayRepository.getHolidays()
            .forEach { newDayOff ->
                dayOffRepository.getSimilarDaysOff(newDayOff)
                    .filter { holidayRepository.isTheSameDayOffEntry(it, newDayOff) }
                    .forEach { dayOffRepository.delete(it) }
                dayOffRepository.save(newDayOff)
            }
        removeDuplicates()
    }

    private suspend fun removeDuplicates() {
        dayOffRepository.getAll()
            .groupBy { "${it.type}_${it.startDate}_${it.finishDate}" }
            .map {
                Log.d(
                    classTag,
                    "removeDuplicates: Found group ${it.key} with ${it.value.size} element${if (it.value.size > 1) "s" else ""}"
                )
                Pair(it.value.last(), it.value.dropLast(1))
            }
            .forEach { daysOffGroup ->
                daysOffGroup.second.forEach {
                    Log.d(classTag, "removeDuplicates: Remove element:\n $it")
                    dayOffRepository.delete(it)
                }
            }
    }
}