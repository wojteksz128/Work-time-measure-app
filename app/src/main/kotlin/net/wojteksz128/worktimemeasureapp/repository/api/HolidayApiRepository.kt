package net.wojteksz128.worktimemeasureapp.repository.api

import net.wojteksz128.worktimemeasureapp.api.holidayapi.HolidayApiResponse
import net.wojteksz128.worktimemeasureapp.api.holidayapi.HolidayApiService
import net.wojteksz128.worktimemeasureapp.model.Country
import net.wojteksz128.worktimemeasureapp.model.DayOff
import net.wojteksz128.worktimemeasureapp.model.fieldType.DayOffSource
import net.wojteksz128.worktimemeasureapp.model.fieldType.DayOffType
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import org.threeten.bp.Month
import retrofit2.Response
import java.util.*

class HolidayApiRepository(
    private val holidayApiService: HolidayApiService,
    override val Settings: Settings,
    override val dateTimeProvider: DateTimeProvider,
) : ExternalHolidayRepository {
    override suspend fun getAvailableCountries(): Collection<Country> {
        val getCountriesResponse = holidayApiService.getCountries()
        when (getCountriesResponse.isSuccessful) {
            true -> {
                val countries = getCountriesResponse.body()!!.countries
                return countries.sortedBy { it.name }.map { Country(it.code, it.name) }
            }
            else -> {
                throw throwApiErrorResponse(getCountriesResponse)
            }
        }
    }

    override suspend fun getHolidays(countryCode: String, year: Int): Collection<DayOff> {
        val getHolidaysResponse = holidayApiService.getHolidays(
            countryCode,
            year,
            public = true,
            language = Locale.getDefault().language
        )
        when (getHolidaysResponse.isSuccessful) {
            true -> {
                val holidays = getHolidaysResponse.body()!!.holidays
                return holidays.map {
                    val dayOffCalendar = Calendar.getInstance().apply { time = it.date }
                    val dayOffDay = dayOffCalendar.get(Calendar.DAY_OF_MONTH)
                    val dayOffMonth = Month.of(dayOffCalendar.get(Calendar.MONTH) + 1)
                    val dayOffYear = dayOffCalendar.get(Calendar.YEAR)
                    DayOff(
                        null,
                        it.uuid,
                        DayOffType.PublicHoliday,
                        it.name,
                        dayOffDay,
                        dayOffMonth,
                        dayOffYear,
                        dayOffDay,
                        dayOffMonth,
                        dayOffYear,
                        DayOffSource.ExternalAPI
                    )
                }
            }
            else -> {
                throw throwApiErrorResponse(getHolidaysResponse)
            }
        }
    }

    private fun <T : HolidayApiResponse> throwApiErrorResponse(getCountriesResponse: Response<T>): ApiErrorResponse {
        val errorMessage =
            getCountriesResponse.body()?.error ?: getCountriesResponse.errorBody()?.string()
            ?: "Unexpected error"
        return ApiErrorResponse(errorMessage)
    }
}