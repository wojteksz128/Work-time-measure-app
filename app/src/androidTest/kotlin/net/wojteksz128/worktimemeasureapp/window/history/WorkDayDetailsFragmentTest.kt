package net.wojteksz128.worktimemeasureapp.window.history

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.repository.ComeEventRepository
import net.wojteksz128.worktimemeasureapp.repository.EntityHistoryRepository
import net.wojteksz128.worktimemeasureapp.repository.WorkDayRepository
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import net.wojteksz128.worktimemeasureapp.util.launchFragmentInHiltContainer
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import org.mockito.kotlin.timeout
import org.mockito.kotlin.verifyBlocking
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class WorkDayDetailsFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var workDayRepository: WorkDayRepository

    @Inject
    lateinit var comeEventRepository: ComeEventRepository

    @Inject
    lateinit var entityHistoryRepository: EntityHistoryRepository

    private val testDate = LocalDate.of(2024, 1, 22)
    private val workDayId = 1L

    private lateinit var workDay: WorkDay
    private lateinit var comeEvent: ComeEvent

    @Before
    fun setup() {
        hiltRule.inject()

        workDayRepository.stub {
            on { getWorkDayByIdInLiveData(workDayId) } doReturn prepareMockWorkDayLiveData()
        }

        val historyLiveData = prepareHistoryItemsLiveData(emptyList())
        entityHistoryRepository.stub {
            on { getGroupedHistoryForWorkDay(workDayId) } doReturn historyLiveData
        }
    }

    @Test
    fun testDisplaysWorkDayDetails() {
        selectExampleWorkDay()

        workDayDetails {
            verifyDetailsAreDisplayed("January 2024", "22", "Monday")
        }
    }

    @Test
    fun testShowsNoEventsMessageWhenListIsEmpty() {
        workDay.events.clear()
        selectExampleWorkDay()

        workDayDetails {
            verifyNoEventsMessageIsDisplayed()
        }
    }

    @Test
    fun testShowsNoHistoryMessageWhenListIsEmpty() {
        selectExampleWorkDay()

        workDayDetails {
            verifyNoHistoryMessageIsDisplayed()
        }
    }

    @Test
    fun testDisplaysHistoryItems() {
        val historyLiveData = prepareHistoryItemsLiveData(
            listOf(
                GroupedHistoryItem(
                    ZonedDateTime.now(),
                    "ComeEventDto",
                    "UPDATE",
                    listOf(FieldChange("startDate", "2024-01-22 08:00:00", "2024-01-22 08:01:00"))
                )
            )
        )
        entityHistoryRepository.stub {
            on { getGroupedHistoryForWorkDay(workDayId) } doReturn historyLiveData
        }

        selectExampleWorkDay()

        workDayDetails {
            verifyHistoryListIsDisplayed()
            verifyHistoryItemCount(1)
            verifyNoHistoryMessageIsNotDisplayed()
        }
    }

    @Test
    fun testSwipeRightOnComeEvent_showsDeleteDialog() {
        selectExampleWorkDay()

        workDayDetails {
            eventsRecyclerView {
                swipeRightOnEvent(0) {
                    verifyIsDisplayed()
                }
            }
        }
    }

    @Test
    fun testSwipeLeftOnComeEvent_showsEditDialog() {
        selectExampleWorkDay()

        workDayDetails {
            eventsRecyclerView {
                swipeLeftOnEvent(0) {
                    verifyIsDisplayed()
                }
            }
        }
    }

    @Test
    fun testAcceptDeletion_deletesComeEventAndShowsSnackbar() {
        selectExampleWorkDay()

        workDayDetails {
            eventsRecyclerView {
                swipeRightOnEvent(0) {
                    clickDelete()
                }
            }
        }

        verifyBlocking(comeEventRepository) { delete(any()) }
        workDayDetails {
            verifySnackbarIsShown(R.string.work_day_details_come_events_deleted_message)
        }
    }

    @Test
    fun testCancelDeletion_dialogIsDismissed() {
        selectExampleWorkDay()

        workDayDetails {
            eventsRecyclerView {
                swipeRightOnEvent(0) {
                    verifyIsDisplayed()
                    clickCancel()
                    verifyDeleteDialogIsDismissed()
                }
            }
        }
    }

    @Test
    fun testAcceptModification_savesComeEventAndShowsSnackbar() {
        val modifiedComeEvent = comeEvent.copy(endDate = comeEvent.endDate!!.plusHours(1))
        val dialogFragment = mock<DialogFragment>()

        launchFragmentInHiltContainer<WorkDayDetailsFragment> {
            val selectedWorkDayViewModel by activityViewModels<SelectedWorkDayViewModel>()
            selectedWorkDayViewModel.select(workDay)

            // Simulate dialog callback
            onAcceptModificationComeEventClick(dialogFragment, modifiedComeEvent)
        }

        verifyBlocking(comeEventRepository, timeout(1000)) { save(modifiedComeEvent) }
        Thread.sleep(1000)
        workDayDetails {
            verifySnackbarIsShown(R.string.work_day_details_come_events_edited_message)
        }
    }

    private fun prepareMockWorkDayLiveData(): MutableLiveData<WorkDay?> {
        comeEvent = ComeEvent(
            id = 1L,
            startDate = ZonedDateTime.of(testDate, LocalTime.of(8, 0), ZoneId.systemDefault()),
            endDate = ZonedDateTime.of(testDate, LocalTime.of(16, 0), ZoneId.systemDefault()),
            workDayId = workDayId
        )

        workDay = WorkDay(
            id = workDayId,
            date = testDate,
            beginSlot = DateTimeUtils.getStartDayTime(testDate),
            endSlot = DateTimeUtils.getEndDayTime(testDate),
            events = mutableListOf(comeEvent)
        )

        return MutableLiveData(workDay)
    }

    private fun prepareHistoryItemsLiveData(items: List<GroupedHistoryItem>) =
        MutableLiveData(items)

    private fun selectExampleWorkDay() {
        launchFragmentInHiltContainer<WorkDayDetailsFragment> {
            val selectedWorkDayViewModel by activityViewModels<SelectedWorkDayViewModel>()
            selectedWorkDayViewModel.select(workDay)
        }
    }
}
