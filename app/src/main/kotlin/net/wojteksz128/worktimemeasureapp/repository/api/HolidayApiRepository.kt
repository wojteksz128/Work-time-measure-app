package net.wojteksz128.worktimemeasureapp.repository.api

import com.google.gson.Gson
import net.wojteksz128.worktimemeasureapp.api.holidayapi.HolidayApiErrorResponse
import net.wojteksz128.worktimemeasureapp.api.holidayapi.HolidayApiResponse
import net.wojteksz128.worktimemeasureapp.api.holidayapi.HolidayApiService
import net.wojteksz128.worktimemeasureapp.model.Country
import net.wojteksz128.worktimemeasureapp.model.DayOff
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.datetime.toLocalDate
import retrofit2.Response
import java.util.Locale

class HolidayApiRepository(
    private val holidayApiService: HolidayApiService,
    override val Settings: Settings,
    override val dateTimeProvider: DateTimeProvider,
    private val gson: Gson,
) : ExternalHolidayRepository(Settings, dateTimeProvider) {
    override suspend fun getAvailableCountries(): Collection<Country> {
        val getCountriesResponse = holidayApiService.getCountries()
        when (getCountriesResponse.isSuccessful) {
            true -> {
                val countries = getCountriesResponse.body()!!.countries
                return countries.sortedBy { it.name }
                    .map { prepareCountryDomainModel(it.code, it.name) }
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
                    prepareDayOffDomainModel(
                        it.name, toLocalDate(it.date), toLocalDate(it.date), it.uuid,
                    )
                }
            }

            else -> {
                throw throwApiErrorResponse(getHolidaysResponse)
            }
        }
    }

    private fun <T : HolidayApiResponse> throwApiErrorResponse(response: Response<T>): ApiErrorResponse {
        val bodyError = response.body()?.error
        val errorBodyString = response.errorBody()?.string()
        val errorBody =
            errorBodyString?.let { gson.fromJson(it, HolidayApiErrorResponse::class.java) }
        val errorMessage = bodyError ?: errorBody?.error ?: "Unexpected error"
        return ApiErrorResponse(errorMessage, errorMessage)
    }
}