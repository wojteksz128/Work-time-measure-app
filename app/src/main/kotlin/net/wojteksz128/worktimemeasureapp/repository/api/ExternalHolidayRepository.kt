package net.wojteksz128.worktimemeasureapp.repository.api

import net.wojteksz128.worktimemeasureapp.model.Country
import net.wojteksz128.worktimemeasureapp.model.DayOff
import net.wojteksz128.worktimemeasureapp.model.fieldType.DayOffSource
import net.wojteksz128.worktimemeasureapp.model.fieldType.DayOffType
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import org.threeten.bp.Month
import java.util.*

abstract class ExternalHolidayRepository(
    @Suppress("PropertyName") open val Settings: Settings,
    open val dateTimeProvider: DateTimeProvider,
) {

    abstract suspend fun getAvailableCountries(): Collection<Country>

    abstract suspend fun getHolidays(
        countryCode: String = Settings.DaysOff.Country.value,
        year: Int = dateTimeProvider.currentCalendar.get(Calendar.YEAR),
    ): Collection<DayOff>

    protected fun prepareCountryDomainModel(code: String, name: String) = Country(code, name)

    protected fun prepareDayOffDomainModel(
        name: String,
        dayOffDay: Int,
        dayOffMonth: Month,
        dayOffYear: Int,
        uuid: String? = null,
    ) = DayOff(
        null,
        uuid,
        DayOffType.PublicHoliday,
        name,
        dayOffDay,
        dayOffMonth,
        dayOffYear,
        dayOffDay,
        dayOffMonth,
        dayOffYear,
        DayOffSource.ExternalAPI
    )
}
