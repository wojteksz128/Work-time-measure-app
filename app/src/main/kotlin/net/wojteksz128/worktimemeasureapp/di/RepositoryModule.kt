package net.wojteksz128.worktimemeasureapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.wojteksz128.worktimemeasureapp.api.holidayapi.HolidayApiService
import net.wojteksz128.worktimemeasureapp.api.nagerDate.NagerDateApiV3Service
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventDao
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventMapper
import net.wojteksz128.worktimemeasureapp.database.dayOff.DayOffDao
import net.wojteksz128.worktimemeasureapp.database.dayOff.DayOffMapper
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayDao
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayMapper
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayWithEventsMapper
import net.wojteksz128.worktimemeasureapp.repository.ComeEventRepository
import net.wojteksz128.worktimemeasureapp.repository.DayOffRepository
import net.wojteksz128.worktimemeasureapp.repository.WorkDayRepository
import net.wojteksz128.worktimemeasureapp.repository.api.ExternalHolidayRepositoriesFacade
import net.wojteksz128.worktimemeasureapp.repository.api.HolidayApiRepository
import net.wojteksz128.worktimemeasureapp.repository.api.NagerDateApiV3Repository
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideWorkDayRepository(
        workDayDao: WorkDayDao,
        workDayMapper: WorkDayMapper,
        workDayWithEventsMapper: WorkDayWithEventsMapper,
    ): WorkDayRepository {
        return WorkDayRepository(workDayDao, workDayMapper, workDayWithEventsMapper)
    }

    @Singleton
    @Provides
    fun provideComeEventRepository(
        comeEventDao: ComeEventDao,
        comeEventMapper: ComeEventMapper
    ): ComeEventRepository {
        return ComeEventRepository(comeEventDao, comeEventMapper)
    }

    @Singleton
    @Provides
    fun provideDayOffRepository(
        dayOffDao: DayOffDao,
        dayOffMapper: DayOffMapper,
    ): DayOffRepository {
        return DayOffRepository(dayOffDao, dayOffMapper)
    }

    @Singleton
    @Provides
    fun provideExternalHolidayRepositoriesFacade(
        holidayApiRepository: HolidayApiRepository,
        nagerDateApiV3Repository: NagerDateApiV3Repository,
    ): ExternalHolidayRepositoriesFacade =
        ExternalHolidayRepositoriesFacade(holidayApiRepository, nagerDateApiV3Repository)

    @Singleton
    @Provides
    fun provideHolidayApiRepository(
        holidayApiService: HolidayApiService,
        Settings: Settings,
        dateTimeProvider: DateTimeProvider,
    ): HolidayApiRepository =
        HolidayApiRepository(holidayApiService, Settings, dateTimeProvider)

    @Singleton
    @Provides
    fun provideNagerDateApiV4Repository(
        nagerDateApiV3Service: NagerDateApiV3Service,
        Settings: Settings,
        dateTimeProvider: DateTimeProvider,
    ): NagerDateApiV3Repository =
        NagerDateApiV3Repository(nagerDateApiV3Service, Settings, dateTimeProvider)
}
