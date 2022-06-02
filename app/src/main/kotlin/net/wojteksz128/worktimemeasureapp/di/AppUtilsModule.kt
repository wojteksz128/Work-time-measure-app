package net.wojteksz128.worktimemeasureapp.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.wojteksz128.worktimemeasureapp.module.dayOff.DayOffService
import net.wojteksz128.worktimemeasureapp.notification.NotificationUtils
import net.wojteksz128.worktimemeasureapp.repository.ComeEventRepository
import net.wojteksz128.worktimemeasureapp.repository.DayOffRepository
import net.wojteksz128.worktimemeasureapp.repository.WorkDayRepository
import net.wojteksz128.worktimemeasureapp.settings.InitialSettingsPreparer
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.util.TimerManager
import net.wojteksz128.worktimemeasureapp.util.comeevent.ComeEventUtils
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppUtilsModule {

    @Singleton
    @Provides
    fun provideTimerManager(
        @ApplicationContext context: Context,
        settings: Settings,
    ): TimerManager {
        return TimerManager(context, settings)
    }

    @Singleton
    @Provides
    fun provideDateTimeUtils(
        @ApplicationContext context: Context,
        dateTimeProvider: DateTimeProvider,
    ): DateTimeUtils {
        return DateTimeUtils(context, dateTimeProvider)
    }

    @Singleton
    @Provides
    fun provideDayOffService(
        dayOffRepository: DayOffRepository,
        Settings: Settings,
    ): DayOffService {
        return DayOffService(dayOffRepository, Settings)
    }

    @Singleton
    @Provides
    fun provideComeEventUtils(
        comeEventRepository: ComeEventRepository,
        workDayRepository: WorkDayRepository,
        dateTimeUtils: DateTimeUtils,
        dateTimeProvider: DateTimeProvider,
    ): ComeEventUtils {
        return ComeEventUtils(
            comeEventRepository,
            workDayRepository,
            dateTimeUtils,
            dateTimeProvider)
    }

    @Singleton
    @Provides
    fun provideNotificationUtils(
        @ApplicationContext context: Context,
        dateTimeProvider: DateTimeProvider,
        timerManager: TimerManager,
        dateTimeUtils: DateTimeUtils,
    ): NotificationUtils {
        return NotificationUtils(context, dateTimeProvider, timerManager, dateTimeUtils)
    }

    @Singleton
    @Provides
    fun provideInitialSettingsPreparer(
        Settings: Settings,
    ): InitialSettingsPreparer = InitialSettingsPreparer(Settings)
}