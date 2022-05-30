package net.wojteksz128.worktimemeasureapp.api.holidayapi

import net.wojteksz128.worktimemeasureapp.BuildConfig
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*

interface HolidayApiService {

    @GET("holidays")
    suspend fun getHolidays(
        @Query("country") country: String,
        @Query("year") year: Int,
        @Query("month") month: Int? = null,
        @Query("day") day: Int? = null,
        @Query("public") public: Boolean? = null,
        @Query("subdivisions") subdivisions: Boolean? = null,
        @Query("search") search: String? = null,
        @Query("language") language: String? = null,
        @Query("previous") previous: Boolean? = null,
        @Query("upcoming") upcoming: Boolean? = null,
        @Query("format") format: String? = null,
        @Query("pretty") pretty: Boolean? = null,
        @Query("key") apiKey: String = BuildConfig.HOLIDAY_API_KEY
    ): Response<HolidayApiHolidaysResponse>

    @GET("countries")
    suspend fun getCountries(
        @Query("country") country: String? = null,
        @Query("search") search: String? = null,
        @Query("public") public: Boolean? = null,
        @Query("format") format: String? = null,
        @Query("pretty") pretty: Boolean? = null,
        @Query("key") apiKey: String = BuildConfig.HOLIDAY_API_KEY
    ): Response<HolidayApiCountriesResponse>

    @GET("languages")
    suspend fun getLanguages(
        @Query("language") language: String? = null,
        @Query("search") search: String? = null,
        @Query("format") format: String? = null,
        @Query("pretty") pretty: Boolean? = null,
        @Query("key") apiKey: String = BuildConfig.HOLIDAY_API_KEY
    ): Response<HolidayApiLanguageResponse>

    @GET("workday")
    suspend fun getWorkday(
        @Query("country") country: String,
        @Query("start") start: Date,
        @Query("days") days: Int,
        @Query("format") format: String? = null,
        @Query("pretty") pretty: Boolean? = null,
        @Query("key") apiKey: String = BuildConfig.HOLIDAY_API_KEY
    ): Response<HolidayApiWorkdayResponse>

    @GET("workdays")
    suspend fun getWorkdaysNoBetween(
        @Query("country") country: String,
        @Query("start") start: Date,
        @Query("end") end: Date,
        @Query("format") format: String? = null,
        @Query("pretty") pretty: Boolean? = null,
        @Query("key") apiKey: String = BuildConfig.HOLIDAY_API_KEY
    ): Response<HolidayApiWorkdaysResponse>
}