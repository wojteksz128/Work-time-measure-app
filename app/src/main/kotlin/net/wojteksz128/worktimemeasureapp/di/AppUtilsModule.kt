package net.wojteksz128.worktimemeasureapp.di

import android.content.Context
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.database.history.HistoryService
import net.wojteksz128.worktimemeasureapp.repository.ComeEventRepository
import net.wojteksz128.worktimemeasureapp.repository.DayOffRepository
import net.wojteksz128.worktimemeasureapp.repository.WorkDayRepository
import net.wojteksz128.worktimemeasureapp.repository.api.ExternalHolidayRepositoriesFacade
import net.wojteksz128.worktimemeasureapp.service.DayOffService
import net.wojteksz128.worktimemeasureapp.settings.InitialSettingsPreparer
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.settings.converter.ConfigurationConverterFactory
import net.wojteksz128.worktimemeasureapp.util.comeevent.ToggleWorkStateUseCase
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.window.util.formatter.history.HiddenFieldNameFormatter
import net.wojteksz128.worktimemeasureapp.window.util.formatter.history.HistoryFormatterProvider
import net.wojteksz128.worktimemeasureapp.window.util.formatter.history.TimeFieldNameFormatter
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppUtilsModule {

    @Singleton
    @Provides
    fun provideDayOffService(
        dayOffRepository: DayOffRepository,
        externalHolidayRepositoriesFacade: ExternalHolidayRepositoriesFacade,
        @Suppress("LocalVariableName") Settings: Settings,
    ): DayOffService {
        return DayOffService(dayOffRepository, externalHolidayRepositoriesFacade, Settings)
    }

    @Singleton
    @Provides
    fun provideHistoryService(gson: Gson) = HistoryService(gson)

    @Singleton
    @Provides
    fun provideComeEventUtils(
        @ApplicationContext context: Context,
        comeEventRepository: ComeEventRepository,
        workDayRepository: WorkDayRepository,
        dateTimeProvider: DateTimeProvider,
    ): ToggleWorkStateUseCase {
        return ToggleWorkStateUseCase(
            context,
            comeEventRepository,
            workDayRepository,
            dateTimeProvider
        )
    }

    @Singleton
    @Provides
    fun provideInitialSettingsPreparer(
        @Suppress("LocalVariableName") Settings: Settings,
        @Named("settings_configuration_version") configurationVersion: String,
        configurationConverterFactory: ConfigurationConverterFactory,
    ): InitialSettingsPreparer =
        InitialSettingsPreparer(Settings, configurationVersion, configurationConverterFactory)

    @Singleton
    @Provides
    @Named("entryHistoryDateTimeFormat")
    fun provideEntryHistoryDateTimeFormat(@ApplicationContext context: Context) =
        context.getString(R.string.entry_history_date_time_format)

    @Singleton
    @Provides
    fun provideTimeFieldNameFormatter(
        gson: Gson,
        @Named("entryHistoryDateTimeFormat") dateTimeFormat: String,
    ) = TimeFieldNameFormatter(gson, dateTimeFormat)

    @Singleton
    @Provides
    fun provideHiddenFieldNameFormatter() = HiddenFieldNameFormatter()

    @Singleton
    @Provides
    fun provideHistoryFormatterProvider(
        timeFieldNameFormatter: TimeFieldNameFormatter,
        hiddenFieldNameFormatter: HiddenFieldNameFormatter,
    ) = HistoryFormatterProvider(timeFieldNameFormatter, hiddenFieldNameFormatter)

}