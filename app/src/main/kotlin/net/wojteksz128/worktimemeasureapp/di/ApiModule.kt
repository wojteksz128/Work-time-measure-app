package net.wojteksz128.worktimemeasureapp.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.wojteksz128.worktimemeasureapp.api.holidayapi.HolidayApiService
import net.wojteksz128.worktimemeasureapp.api.nagerDate.NagerDateApiV3Service
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    private const val HOLIDAY_API_BASE_URL = "https://holidayapi.com/v1/"
    private const val NAGER_DATE_API_BASE_URL = "https://date.nager.at/api/"

    @Singleton
    @Provides
    fun providesHttpLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor()
            .apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

    @Singleton
    @Provides
    fun providesOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()

    @Singleton
    @Provides
    fun providesGson(): Gson =
        GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create()

    @Singleton
    @Provides
    @Named("holiday_api_retrofit")
    fun provideHolidayApiRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(HOLIDAY_API_BASE_URL)
            .client(okHttpClient)
            .build()

    @Singleton
    @Provides
    fun provideHolidayApiService(@Named("holiday_api_retrofit") retrofit: Retrofit): HolidayApiService =
        retrofit.create(HolidayApiService::class.java)

    @Singleton
    @Provides
    @Named("nager_date_api_retrofit")
    fun provideNagerDateApiRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(NAGER_DATE_API_BASE_URL)
            .client(okHttpClient)
            .build()

    @Singleton
    @Provides
    fun provideNagerDateApiV3Service(@Named("nager_date_api_retrofit") retrofit: Retrofit): NagerDateApiV3Service =
        retrofit.create(NagerDateApiV3Service::class.java)
}