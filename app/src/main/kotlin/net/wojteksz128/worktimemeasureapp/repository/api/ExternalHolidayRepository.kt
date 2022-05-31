package net.wojteksz128.worktimemeasureapp.repository.api

import net.wojteksz128.worktimemeasureapp.model.Country
import net.wojteksz128.worktimemeasureapp.model.DayOff
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import java.util.*

interface ExternalHolidayRepository {
    @Suppress("PropertyName")
    val Settings: Settings

    val dateTimeProvider: DateTimeProvider

    suspend fun getAvailableCountries(): Collection<Country>

    suspend fun getHolidays(
        countryCode: String = Settings.DaysOff.Country.value,
        year: Int = dateTimeProvider.currentCalendar.get(Calendar.YEAR),
    ): Collection<DayOff>
}
