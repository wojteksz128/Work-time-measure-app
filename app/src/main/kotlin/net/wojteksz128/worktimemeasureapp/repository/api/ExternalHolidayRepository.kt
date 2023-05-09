package net.wojteksz128.worktimemeasureapp.repository.api

import net.wojteksz128.worktimemeasureapp.model.Country
import net.wojteksz128.worktimemeasureapp.model.DayOff
import net.wojteksz128.worktimemeasureapp.model.fieldType.DayOffSource
import net.wojteksz128.worktimemeasureapp.model.fieldType.DayOffType.PublicHoliday
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import org.threeten.bp.LocalDate
import java.util.Calendar

abstract class ExternalHolidayRepository(
    open val Settings: Settings,
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
        startDate: LocalDate,
        finishDate: LocalDate,
        uuid: String? = null,
    ) = DayOff(
        null,
        uuid,
        PublicHoliday,
        name,
        startDate,
        finishDate,
        DayOffSource.ExternalAPI
    )

    open fun isTheSameDayOffEntry(originalDayOff: DayOff, otherDayOff: DayOff): Boolean =
        originalDayOff.type == PublicHoliday && otherDayOff.type == PublicHoliday
                && originalDayOff.startDate == otherDayOff.startDate
                && originalDayOff.finishDate == otherDayOff.finishDate
}
