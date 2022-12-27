package net.wojteksz128.worktimemeasureapp.repository.api

import android.content.Context
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.api.nagerDate.NagerDateApiV3Service
import net.wojteksz128.worktimemeasureapp.model.Country
import net.wojteksz128.worktimemeasureapp.model.DayOff
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import org.threeten.bp.Month
import retrofit2.Response
import java.util.*

class NagerDateApiV3Repository(
    private val nagerDateApiV3Service: NagerDateApiV3Service,
    override val Settings: Settings,
    override val dateTimeProvider: DateTimeProvider,
    private val context: Context,
) : ExternalHolidayRepository(Settings, dateTimeProvider) {
    override suspend fun getAvailableCountries(): Collection<Country> {
        val getAvailableCountriesResponse = nagerDateApiV3Service.getAvailableCountries()
        when (getAvailableCountriesResponse.isSuccessful) {
            true -> {
                val counties = getAvailableCountriesResponse.body()!!
                return counties.sortedBy { it.name }
                    .map { prepareCountryDomainModel(it.countryCode ?: "", it.name ?: "") }
            }
            else -> {
                throw throwApiErrorResponse(getAvailableCountriesResponse)
            }
        }
    }

    override suspend fun getHolidays(countryCode: String, year: Int): Collection<DayOff> {
        val getUpcomingPublicHolidaysResponse =
            nagerDateApiV3Service.getUpcomingPublicHolidays(countryCode)
        when (getUpcomingPublicHolidaysResponse.isSuccessful) {
            true -> {
                val upcomingPublicHolidays = getUpcomingPublicHolidaysResponse.body()!!
                return upcomingPublicHolidays.map {
                    val dayOffCalendar = Calendar.getInstance().apply { time = it.date }
                    val dayOffDay = dayOffCalendar.get(Calendar.DAY_OF_MONTH)
                    val dayOffMonth = Month.of(dayOffCalendar.get(Calendar.MONTH) + 1)
                    val dayOffYear = dayOffCalendar.get(Calendar.YEAR)
                    prepareDayOffDomainModel(it.localName ?: "", dayOffDay, dayOffMonth, dayOffYear)
                }
            }
            else -> {
                throw throwApiErrorResponse(getUpcomingPublicHolidaysResponse)
            }
        }
    }

    private fun throwApiErrorResponse(response: Response<*>): ApiErrorResponse {
        val errorMessage =
            context.getString(R.string.nager_date_error_template, response.code(), response.body())
        return ApiErrorResponse(errorMessage, errorMessage)
    }

}