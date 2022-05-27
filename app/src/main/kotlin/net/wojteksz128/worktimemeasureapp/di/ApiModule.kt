package net.wojteksz128.worktimemeasureapp.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.wojteksz128.worktimemeasureapp.api.holidayapi.HolidayApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    private const val HOLIDAY_API_BASE_URL = "https://holidayapi.com/v1/"

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
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(HOLIDAY_API_BASE_URL)
            .client(okHttpClient)
            .build()

    @Singleton
    @Provides
    fun provideHolidayApiService(retrofit: Retrofit): HolidayApiService =
        retrofit.create(HolidayApiService::class.java)
}