package net.wojteksz128.worktimemeasureapp.repository.api

import android.content.Context
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.api.nagerDate.NagerDateApiV3Service
import net.wojteksz128.worktimemeasureapp.model.Country
import net.wojteksz128.worktimemeasureapp.model.DayOff
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.datetime.toLocalDate
import retrofit2.Response

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
                    prepareDayOffDomainModel(
                        it.localName ?: "",
                        toLocalDate(it.date),
                        toLocalDate(it.date)
                    )
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