package net.wojteksz128.worktimemeasureapp.window.history

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.action.ViewActions.swipeRight
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
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
import net.wojteksz128.worktimemeasureapp.util.withItemCount
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
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

        val workDayLiveData = MutableLiveData(workDay)
        workDayRepository.stub {
            on { getWorkDayByIdInLiveData(workDayId) } doReturn workDayLiveData
        }

        val historyLiveData = MutableLiveData<List<GroupedHistoryItem>>(emptyList())
        entityHistoryRepository.stub {
            on { getGroupedHistoryForWorkDay(workDayId) } doReturn historyLiveData
        }
    }

    @Test
    fun testDisplaysWorkDayDetails() {
        launchFragmentInHiltContainer<WorkDayDetailsFragment> {
            val selectedWorkDayViewModel by activityViewModels<SelectedWorkDayViewModel>()
            selectedWorkDayViewModel.select(workDay)
        }

        onView(withText("January 2024")).check(matches(isDisplayed()))
        onView(withText("22")).check(matches(isDisplayed()))
        onView(withText("Monday")).check(matches(isDisplayed()))
    }

    @Test
    fun testDisplaysComeEvents() {
        launchFragmentInHiltContainer<WorkDayDetailsFragment> {
            val selectedWorkDayViewModel by activityViewModels<SelectedWorkDayViewModel>()
            selectedWorkDayViewModel.select(workDay)
        }

        onView(withId(R.id.work_day_details_come_events)).check(matches(isDisplayed()))
    }

    @Test
    fun testShowsNoEventsMessageWhenListIsEmpty() {
        workDay.events.clear()
        launchFragmentInHiltContainer<WorkDayDetailsFragment> {
            val selectedWorkDayViewModel by activityViewModels<SelectedWorkDayViewModel>()
            selectedWorkDayViewModel.select(workDay)
        }

        onView(withId(R.id.work_day_details_come_event_list_no_events_message)).check(
            matches(
                isDisplayed()
            )
        )
    }

    @Test
    fun testShowsNoHistoryMessageWhenListIsEmpty() {
        launchFragmentInHiltContainer<WorkDayDetailsFragment> {
            val selectedWorkDayViewModel by activityViewModels<SelectedWorkDayViewModel>()
            selectedWorkDayViewModel.select(workDay)
        }

        onView(withId(R.id.work_day_details_history_no_events_message)).check(matches(isDisplayed()))
    }

    @Test
    fun testDisplaysHistoryItems() {
        val historyItem = GroupedHistoryItem(
            ZonedDateTime.now(),
            "ComeEventDto",
            "UPDATE",
            listOf(FieldChange("startDate", "2024-01-22 08:00:00", "2024-01-22 08:01:00"))
        )
        val historyLiveData = MutableLiveData(listOf(historyItem))
        entityHistoryRepository.stub {
            on { getGroupedHistoryForWorkDay(workDayId) } doReturn historyLiveData
        }

        launchFragmentInHiltContainer<WorkDayDetailsFragment> {
            val selectedWorkDayViewModel by activityViewModels<SelectedWorkDayViewModel>()
            selectedWorkDayViewModel.select(workDay)
        }

        onView(withId(R.id.work_day_details_history_entries)).check(matches(isDisplayed()))
        onView(withId(R.id.work_day_details_history_entries)).check(matches(withItemCount(1)))
        onView(withId(R.id.work_day_details_history_no_events_message)).check(
            matches(
                not(
                    isDisplayed()
                )
            )
        )
    }

    @Test
    fun testSwipeRightOnComeEvent_showsDeleteDialog() {
        launchFragmentInHiltContainer<WorkDayDetailsFragment> {
            val selectedWorkDayViewModel by activityViewModels<SelectedWorkDayViewModel>()
            selectedWorkDayViewModel.select(workDay)
        }

        onView(withId(R.id.work_day_details_come_events))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<ComeEventsAdapter.ComeEventViewHolder>(
                    0,
                    swipeRight()
                )
            )

        onView(withText(R.string.delete_come_event_dialog_title)).check(matches(isDisplayed()))
    }

    @Test
    fun testSwipeLeftOnComeEvent_showsEditDialog() {
        launchFragmentInHiltContainer<WorkDayDetailsFragment> {
            val selectedWorkDayViewModel by activityViewModels<SelectedWorkDayViewModel>()
            selectedWorkDayViewModel.select(workDay)
        }

        onView(withId(R.id.work_day_details_come_events))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<ComeEventsAdapter.ComeEventViewHolder>(
                    0,
                    swipeLeft()
                )
            )

        onView(withText(R.string.edit_come_event_dialog_title)).check(matches(isDisplayed()))
    }

    @Test
    fun testAcceptDeletion_deletesComeEventAndShowsSnackbar() {
        launchFragmentInHiltContainer<WorkDayDetailsFragment> {
            val selectedWorkDayViewModel by activityViewModels<SelectedWorkDayViewModel>()
            selectedWorkDayViewModel.select(workDay)
        }

        onView(withId(R.id.work_day_details_come_events))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<ComeEventsAdapter.ComeEventViewHolder>(
                    0,
                    swipeRight()
                )
            )

        onView(withText(R.string.delete_come_event_dialog_action_delete)).perform(click())

        verifyBlocking(comeEventRepository) { delete(any()) }
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText(R.string.work_day_details_come_events_deleted_message)))
    }

    @Test
    fun testCancelDeletion_dialogIsDismissed() {
        launchFragmentInHiltContainer<WorkDayDetailsFragment> {
            val selectedWorkDayViewModel by activityViewModels<SelectedWorkDayViewModel>()
            selectedWorkDayViewModel.select(workDay)
        }

        onView(withId(R.id.work_day_details_come_events))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<ComeEventsAdapter.ComeEventViewHolder>(
                    0,
                    swipeRight()
                )
            )

        onView(withText(R.string.delete_come_event_dialog_title)).check(matches(isDisplayed()))
        onView(withText(R.string.delete_come_event_dialog_action_cancel)).perform(click())
        onView(withText(R.string.delete_come_event_dialog_title)).check(doesNotExist())
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

        verifyBlocking(comeEventRepository) { save(modifiedComeEvent) }
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText(R.string.work_day_details_come_events_edited_message)))
    }
}
