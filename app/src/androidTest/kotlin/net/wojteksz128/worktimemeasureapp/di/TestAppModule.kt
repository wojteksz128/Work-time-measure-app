package net.wojteksz128.worktimemeasureapp.di

import android.content.Context
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.database.history.HistoryService
import net.wojteksz128.worktimemeasureapp.notification.worktime.WorkTimeNotificationFactory
import net.wojteksz128.worktimemeasureapp.notification.worktime.WorkTimeNotificationService
import net.wojteksz128.worktimemeasureapp.repository.ComeEventRepository
import net.wojteksz128.worktimemeasureapp.repository.DayOffRepository
import net.wojteksz128.worktimemeasureapp.repository.WorkDayRepository
import net.wojteksz128.worktimemeasureapp.repository.api.ExternalHolidayRepositoriesFacade
import net.wojteksz128.worktimemeasureapp.service.DayOffService
import net.wojteksz128.worktimemeasureapp.settings.InitialSettingsPreparer
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.settings.converter.ConfigurationConverterFactory
import net.wojteksz128.worktimemeasureapp.util.comeevent.ComeEventUtils
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import net.wojteksz128.worktimemeasureapp.window.history.formatters.HiddenFieldNameFormatter
import net.wojteksz128.worktimemeasureapp.window.history.formatters.HistoryFormatterProvider
import net.wojteksz128.worktimemeasureapp.window.history.formatters.TimeFieldNameFormatter
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import javax.inject.Named
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppUtilsModule::class]
)
object TestAppModule {

    @Singleton
    @Provides
    fun provideDateTimeUtils(
        @ApplicationContext context: Context,
    ) = DateTimeUtils(context)

    @Singleton
    @Provides
    fun provideDayOffService(
        dayOffRepository: DayOffRepository,
        externalHolidayRepositoriesFacade: ExternalHolidayRepositoriesFacade,
        @Suppress("LocalVariableName") Settings: Settings,
    ) = spy(DayOffService(dayOffRepository, externalHolidayRepositoriesFacade, Settings))

    @Singleton
    @Provides
    fun provideWorkTimeNotificationService(): WorkTimeNotificationService = mock()

    @Singleton
    @Provides
    fun provideWorkTimeNotificationFactory(): WorkTimeNotificationFactory = mock()

    @Singleton
    @Provides
    fun provideHistoryService(gson: Gson) = HistoryService(gson)

    @Singleton
    @Provides
    fun provideComeEventUtils(
        comeEventRepository: ComeEventRepository,
        workDayRepository: WorkDayRepository,
        dateTimeProvider: DateTimeProvider,
    ): ComeEventUtils =
        spy(ComeEventUtils(comeEventRepository, workDayRepository, dateTimeProvider))

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
        dateTimeUtils: DateTimeUtils,
        gson: Gson,
        @Named("entryHistoryDateTimeFormat") dateTimeFormat: String,
    ) = TimeFieldNameFormatter(dateTimeUtils, gson, dateTimeFormat)

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
