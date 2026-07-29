package net.wojteksz128.worktimemeasureapp.di

import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.flow.MutableStateFlow
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.model.Country
import net.wojteksz128.worktimemeasureapp.model.DayOff
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.repository.ComeEventRepository
import net.wojteksz128.worktimemeasureapp.repository.DayOffRepository
import net.wojteksz128.worktimemeasureapp.repository.EntityHistoryRepository
import net.wojteksz128.worktimemeasureapp.repository.WorkDayRepository
import net.wojteksz128.worktimemeasureapp.repository.api.ExternalHolidayRepositoriesFacade
import net.wojteksz128.worktimemeasureapp.repository.api.ExternalHolidayRepository
import net.wojteksz128.worktimemeasureapp.repository.api.HolidayApiRepository
import net.wojteksz128.worktimemeasureapp.repository.api.NagerDateApiV3Repository
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import org.mockito.kotlin.KStubbing
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class]
)
object TestRepositoryModule {

    const val DUMMY_ID = 1L
    val DUMMY_COUNTRIES = listOf(
        Country("US", "United States"),
        Country("PL", "Poland")
    )

    @Singleton
    @Provides
    fun provideMockWorkDayFlow(): MutableStateFlow<WorkDay?> = MutableStateFlow(null)

    @Singleton
    @Provides
    fun provideWorkDayRepository(workDayFlow: MutableStateFlow<WorkDay?>) =
        mock<WorkDayRepository>().stub {
            mockSingleWorkDayByDateMethods(workDayFlow)
            mockSingleWorkDayByIdMethods(DUMMY_ID, workDayFlow)
            mockMultipleWorkDays()
    }

    private fun KStubbing<WorkDayRepository>.mockSingleWorkDayByDateMethods(
        workDayFlow: MutableStateFlow<WorkDay?>,
    ) {
        on { getWorkDayByDateAsFlow(any()) } doAnswer { workDayFlow }
        onBlocking { getWorkDayByDate(any()) } doAnswer { workDayFlow.value }
        onBlocking { save(any()) } doAnswer { invocation ->
            val workDayToSave = invocation.getArgument<WorkDay>(0)
            workDayFlow.value = workDayToSave.copy(id = DUMMY_ID)
            return@doAnswer workDayFlow.value
        }
    }

    private fun KStubbing<WorkDayRepository>.mockSingleWorkDayByIdMethods(
        @Suppress("SameParameterValue") id: Long,
        workDayFlow: MutableStateFlow<WorkDay?>,
    ) {
        onBlocking { getWorkDayById(id) } doAnswer { workDayFlow.value }
        on { getWorkDayByIdInLiveData(any()) } doAnswer { workDayFlow.asLiveData() }
    }

    private fun KStubbing<WorkDayRepository>.mockMultipleWorkDays() {
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

    @Singleton
    @Provides
    fun provideComeEventRepository(workDayFlow: MutableStateFlow<WorkDay?>) =
        mock<ComeEventRepository>().stub {
            onBlocking { save(any()) }.doAnswer { invocation ->
                val eventToSave = invocation.getArgument<ComeEvent>(0)
                workDayFlow.value?.let { workDay ->
                    val events = workDay.events.toMutableList()
                    val existingIx =
                        if (eventToSave.id != null) events.indexOfFirst { it.id == eventToSave.id } else -1

                    val modifiedComeEvent = if (existingIx != -1) { // It's an update
                        events[existingIx] = eventToSave
                        eventToSave
                    } else { // It's a new event
                        val storedEvent = eventToSave.copy(id = DUMMY_ID)
                        events.add(storedEvent)
                        storedEvent
                    }
                    workDayFlow.value = workDay.copy(events = events)
                    modifiedComeEvent
                }
            }
            onBlocking { delete(any()) }.doAnswer { invocation ->
                val eventToDelete = invocation.getArgument<ComeEvent>(0)
                workDayFlow.value?.let { workDay ->
                    val events = workDay.events.toMutableList()
                    events.remove(eventToDelete)
                    workDayFlow.value = workDay.copy(events = events)
            }
        }
    }

    @Singleton
    @Provides
    fun provideDayOffRepository() = mock<DayOffRepository>().stub {
        onBlocking { getAll() } doAnswer { listOf() }
        on { getAllInLiveData() } doAnswer { liveData { listOf<DayOff>() } }
    }

    @Singleton
    @Provides
    fun provideExternalHolidayRepositoriesFacade(
        holidayApiRepository: HolidayApiRepository,
        nagerDateApiV3Repository: NagerDateApiV3Repository,
    ) = ExternalHolidayRepositoriesFacade(holidayApiRepository, nagerDateApiV3Repository)

    @Singleton
    @Provides
    fun provideHolidayApiRepository(
        @Suppress("LocalVariableName") Settings: Settings,
        dateTimeProvider: DateTimeProvider,
    ) = mock<HolidayApiRepository>().assignMocks(Settings, dateTimeProvider)

    @Singleton
    @Provides
    fun provideNagerDateApiV4Repository(
        @Suppress("LocalVariableName") Settings: Settings,
        dateTimeProvider: DateTimeProvider,
    ) = mock<NagerDateApiV3Repository>().assignMocks(Settings, dateTimeProvider)

    private fun <T : ExternalHolidayRepository> T.assignMocks(
        @Suppress("LocalVariableName") Settings: Settings,
        dateTimeProvider: DateTimeProvider,
    ) = stub {
        this.on { this@assignMocks.Settings } doAnswer { Settings }
        this.on { this@assignMocks.dateTimeProvider } doAnswer { dateTimeProvider }

        onBlocking { this@assignMocks.getAvailableCountries() } doAnswer { DUMMY_COUNTRIES }
        onBlocking {
            this@assignMocks.getHolidays(
                any<String>(),
                any<Int>()
            )
        } doAnswer { listOf<DayOff>() }
    }

    @Singleton
    @Provides
    fun provideEntityHistoryRepository(): EntityHistoryRepository = mock()
}
