package net.wojteksz128.worktimemeasureapp.api.nagerDate

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NagerDateApiV3Service {

    @GET("v3/CountryInfo/{countryCode}")
    suspend fun getCountryInfo(@Path("countryCode") countryCode: String): Response<NagerDateCountryInfoDto>

    @GET("v3/AvailableCountries")
    suspend fun getAvailableCountries(): Response<Set<NagerDateCountryV3Dto>>

    @GET("v3/LongWeekend/{year}/{countryCode}")
    suspend fun getLongWeekends(
        @Path("year") year: Int,
        @Path("countryCode") countryCode: String
    ): Response<Set<NagerDateLongWeekendV3Dto>>

    @GET("v3/PublicHolidays/{year}/{countryCode}")
    suspend fun getPublicHolidays(
        @Path("year") year: Int,
        @Path("countryCode") countryCode: String
    ): Response<Set<NagerDatePublicHolidayV3Dto>>

    @GET("v3/IsTodayPublicHoliday/{countryCode}")
    suspend fun isTodayPublicHolidayCode(
        @Path("countryCode") countryCode: String,
        @Query("countryCode") countryCodeQuery: String,
        @Query("offset") offset: Int?
    ): Response<*>

    @GET("v3/NextPublicHolidays/{countryCode}")
    suspend fun getUpcomingPublicHolidays(
        @Path("countryCode") countryCode: String
    ): Response<Set<NagerDatePublicHolidayV3Dto>>

    @GET("v3/NextPublicHolidaysWorldwide")
    suspend fun getUpcomingPublicHolidaysWorldwide(): Response<Set<NagerDatePublicHolidayV3Dto>>
}