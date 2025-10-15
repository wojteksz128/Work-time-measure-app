package net.wojteksz128.worktimemeasureapp.di

import android.content.Context
import androidx.lifecycle.asLiveData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.flow.MutableStateFlow
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.api.holidayapi.HolidayApiService
import net.wojteksz128.worktimemeasureapp.api.nagerDate.NagerDateApiV3Service
import net.wojteksz128.worktimemeasureapp.database.dayOff.DayOffDao
import net.wojteksz128.worktimemeasureapp.database.dayOff.DayOffMapper
import net.wojteksz128.worktimemeasureapp.database.history.EntityHistoryDao
import net.wojteksz128.worktimemeasureapp.database.history.HistoryService
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.module.dayOff.DayOffService
import net.wojteksz128.worktimemeasureapp.notification.worktime.WorkTimeNotificationFactory
import net.wojteksz128.worktimemeasureapp.notification.worktime.WorkTimeNotificationService
import net.wojteksz128.worktimemeasureapp.repository.ComeEventRepository
import net.wojteksz128.worktimemeasureapp.repository.DayOffRepository
import net.wojteksz128.worktimemeasureapp.repository.EntityHistoryRepository
import net.wojteksz128.worktimemeasureapp.repository.WorkDayRepository
import net.wojteksz128.worktimemeasureapp.repository.api.ExternalHolidayRepositoriesFacade
import net.wojteksz128.worktimemeasureapp.repository.api.HolidayApiRepository
import net.wojteksz128.worktimemeasureapp.repository.api.NagerDateApiV3Repository
import net.wojteksz128.worktimemeasureapp.settings.InitialSettingsPreparer
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.settings.converter.ConfigurationConverterFactory
import net.wojteksz128.worktimemeasureapp.util.comeevent.ComeEventUtils
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import net.wojteksz128.worktimemeasureapp.window.history.formatters.HiddenFieldNameFormatter
import net.wojteksz128.worktimemeasureapp.window.history.formatters.HistoryFormatterProvider
import net.wojteksz128.worktimemeasureapp.window.history.formatters.TimeFieldNameFormatter
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.stub
import javax.inject.Named
import javax.inject.Singleton

const val DUMMY_ID = 1L

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppUtilsModule::class, RepositoryModule::class]
)
object TestAppModule {

    // This shared, single StateFlow is the source of truth for our reactive test data.
    // Both repository mocks will interact with this single instance.
    @Singleton
    @Provides
    fun provideMockWorkDayFlow(): MutableStateFlow<WorkDay?> = MutableStateFlow(null)

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
    fun provideDayOffService(): DayOffService = mock()

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

    // Replacing RepositoryModule beans

    @Singleton
    @Provides
    fun provideWorkDayRepository(workDayFlow: MutableStateFlow<WorkDay?>): WorkDayRepository {
        val workDayRepository = mock<WorkDayRepository>()

        mockSingleWorkDayByDateMethods(workDayFlow, workDayRepository)
        mockSingleWorkDayByIdMethods(DUMMY_ID, workDayFlow, workDayRepository)
        mockMultipleWorkDays(workDayRepository)

        return workDayRepository
    }

    private fun mockSingleWorkDayByDateMethods(
        workDayFlow: MutableStateFlow<WorkDay?>,
        workDayRepository: WorkDayRepository,
    ) {
        workDayRepository.stub {
            on { getWorkDayByDateAsFlow(any()) } doAnswer { workDayFlow }
            onBlocking { getWorkDayByDate(any()) } doAnswer { workDayFlow.value }
            onBlocking { save(any()) } doAnswer { invocation ->
                val workDayToSave = invocation.getArgument<WorkDay>(0)
                workDayFlow.value = workDayToSave.copy(id = DUMMY_ID)
                return@doAnswer
            }
        }
    }

    private fun mockSingleWorkDayByIdMethods(
        id: Long,
        workDayFlow: MutableStateFlow<WorkDay?>,
        workDayRepository: WorkDayRepository,
    ) {
        workDayRepository.stub {
            onBlocking { getWorkDayById(id) } doAnswer { workDayFlow.value }
            on { getWorkDayByIdInLiveData(any()) } doAnswer { workDayFlow.asLiveData() }
        }
    }

    private fun mockMultipleWorkDays(workDayRepository: WorkDayRepository) {
        workDayRepository.stub {
            onBlocking { getWorkDaysForRange(any()) } doAnswer { listOf() }
            on { getAllPaged() } doAnswer {
                {
                    object : PagingSource<Int, WorkDay>() {
                        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, WorkDay> =
                            LoadResult.Page(listOf(), null, null)

                        override fun getRefreshKey(state: PagingState<Int, WorkDay>): Int? = null
                    }
                }
            }
        }
    }

    @Singleton
    @Provides
    fun provideComeEventRepository(workDayFlow: MutableStateFlow<WorkDay?>): ComeEventRepository {
        val comeEventRepository = mock<ComeEventRepository>().stub {
            onBlocking { save(any()) } doAnswer { invocation ->
                val comeEvent = invocation.getArgument<ComeEvent>(0)
                val currentWorkDay = workDayFlow.value

                currentWorkDay?.let { day ->
                    val updatedEvents = day.events + comeEvent.copy(id = DUMMY_ID)
                    val updatedWorkDay = day.copy(events = updatedEvents.toMutableList())
                    workDayFlow.value = updatedWorkDay
                }
                return@doAnswer
            }
        }

        return comeEventRepository
    }

    @Singleton
    @Provides
    fun provideDayOffRepository(
        dayOffDao: DayOffDao,
        dayOffMapper: DayOffMapper,
        historyService: HistoryService,
        historyDao: EntityHistoryDao,
    ): DayOffRepository = DayOffRepository(dayOffDao, dayOffMapper, historyService, historyDao)

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
        @Suppress("LocalVariableName") Settings: Settings,
        dateTimeProvider: DateTimeProvider,
        gson: Gson,
    ): HolidayApiRepository =
        HolidayApiRepository(holidayApiService, Settings, dateTimeProvider, gson)

    @Singleton
    @Provides
    fun provideNagerDateApiV4Repository(
        nagerDateApiV3Service: NagerDateApiV3Service,
        @Suppress("LocalVariableName") Settings: Settings,
        dateTimeProvider: DateTimeProvider,
        @ApplicationContext context: Context,
    ): NagerDateApiV3Repository =
        NagerDateApiV3Repository(nagerDateApiV3Service, Settings, dateTimeProvider, context)

    @Singleton
    @Provides
    fun provideEntityHistoryRepository(
        entityHistoryDao: EntityHistoryDao,
        gson: Gson,
        formatterProvider: HistoryFormatterProvider,
    ): EntityHistoryRepository = EntityHistoryRepository(entityHistoryDao, gson, formatterProvider)
}
