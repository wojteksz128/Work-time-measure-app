package net.wojteksz128.worktimemeasureapp.window.history

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.paging.PagingSource
import androidx.paging.PagingState
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
import net.wojteksz128.worktimemeasureapp.repository.WorkDayRepository
import net.wojteksz128.worktimemeasureapp.util.clickChildViewWithId
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import net.wojteksz128.worktimemeasureapp.util.launchFragmentInHiltContainer
import net.wojteksz128.worktimemeasureapp.util.withItemCount
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.stub
import org.mockito.kotlin.verifyBlocking
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class WorkDaysHistoryFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var workDayRepository: WorkDayRepository

    @Inject
    lateinit var comeEventRepository: ComeEventRepository

    private val testDate = LocalDate.of(2024, 1, 1)
    private val workDayId = 1L
    private val comeEvent = ComeEvent(
        id = 1L,
        startDate = ZonedDateTime.of(testDate, LocalTime.of(8, 0), ZoneId.systemDefault()),
        endDate = null,
        workDayId = workDayId
    )
    private val workDay = WorkDay(
        id = workDayId,
        date = testDate,
        beginSlot = DateTimeUtils.getStartDayTime(testDate),
        endSlot = DateTimeUtils.getEndDayTime(testDate),
        events = mutableListOf(comeEvent)
    )

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun testFragmentDisplaysWithNoData() {
        workDayRepository.stub {
            on { getAllPaged() } doAnswer { { TestPagingSource(emptyList()) } }
        }

        launchFragmentInHiltContainer<WorkDaysHistoryFragment> {}

        onView(withId(R.id.work_days_history_rv)).check(matches(isDisplayed()))
        onView(withId(R.id.work_days_history_rv)).check(matches(withItemCount(0)))
    }

    @Test
    fun testFragmentDisplaysWithData() {
        workDayRepository.stub {
            on { getAllPaged() } doAnswer { { TestPagingSource(listOf(workDay)) } }
        }

        launchFragmentInHiltContainer<WorkDaysHistoryFragment> {}

        onView(withId(R.id.work_days_history_rv)).check(matches(isDisplayed()))
        onView(withId(R.id.work_days_history_rv)).check(matches(withItemCount(1)))
    }

    @Test
    fun testWorkDayClick_navigateToDetails() {
        val navController = mock(NavController::class.java)

        workDayRepository.stub {
            on { getAllPaged() } doAnswer { { TestPagingSource(listOf(workDay)) } }
        }

        launchFragmentInHiltContainer<WorkDaysHistoryFragment> {
            Navigation.setViewNavController(requireView(), navController)
        }

        onView(withId(R.id.work_days_history_rv))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<WorkDayAdapter.WorkDayViewHolder>(
                    0,
                    click()
                )
            )

        verify(navController).navigate(any<Int>(), any<Bundle>())
    }

    @Test
    fun testSwipeLeftOnComeEvent_showsEditDialog() {
        workDayRepository.stub {
            on { getAllPaged() } doAnswer { { TestPagingSource(listOf(workDay)) } }
        }

        launchFragmentInHiltContainer<WorkDaysHistoryFragment> {}

        onView(withId(R.id.work_days_history_rv)).perform(
            RecyclerViewActions.actionOnItemAtPosition<WorkDayAdapter.WorkDayViewHolder>(
                0,
                clickChildViewWithId(R.id.day_expand)
            )
        )

        onView(withId(R.id.day_events_list))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<ComeEventsAdapter.ComeEventViewHolder>(
                    0,
                    swipeLeft()
                )
            )

        onView(withText(R.string.edit_come_event_dialog_title)).check(matches(isDisplayed()))
    }

    @Test
    fun testSwipeRightOnComeEvent_showsDeleteDialog() {
        workDayRepository.stub {
            on { getAllPaged() } doAnswer { { TestPagingSource(listOf(workDay)) } }
        }

        launchFragmentInHiltContainer<WorkDaysHistoryFragment> {}

        onView(withId(R.id.work_days_history_rv)).perform(
            RecyclerViewActions.actionOnItemAtPosition<WorkDayAdapter.WorkDayViewHolder>(
                0,
                clickChildViewWithId(R.id.day_expand)
            )
        )

        onView(withId(R.id.day_events_list))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<ComeEventsAdapter.ComeEventViewHolder>(
                    0,
                    swipeRight()
                )
            )

        onView(withText(R.string.delete_come_event_dialog_title)).check(matches(isDisplayed()))
    }

    @Test
    fun testAcceptDeletion_deletesComeEventAndShowsSnackbar() {
        workDayRepository.stub {
            on { getAllPaged() } doAnswer { { TestPagingSource(listOf(workDay)) } }
        }

        launchFragmentInHiltContainer<WorkDaysHistoryFragment> {}

        onView(withId(R.id.work_days_history_rv)).perform(
            RecyclerViewActions.actionOnItemAtPosition<WorkDayAdapter.WorkDayViewHolder>(
                0,
                clickChildViewWithId(R.id.day_expand)
            )
        )

        onView(withId(R.id.day_events_list))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<ComeEventsAdapter.ComeEventViewHolder>(
                    0,
                    swipeRight()
                )
            )

        onView(withText(R.string.delete_come_event_dialog_action_delete)).perform(click())

        verifyBlocking(comeEventRepository) { delete(any()) }
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testCancelDeletion_dialogIsDismissed() {
        workDayRepository.stub {
            on { getAllPaged() } doAnswer { { TestPagingSource(listOf(workDay)) } }
        }

        launchFragmentInHiltContainer<WorkDaysHistoryFragment> {}

        onView(withId(R.id.work_days_history_rv)).perform(
            RecyclerViewActions.actionOnItemAtPosition<WorkDayAdapter.WorkDayViewHolder>(
                0,
                clickChildViewWithId(R.id.day_expand)
            )
        )

        onView(withId(R.id.day_events_list))
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
    fun testDisplaysMoreThan100Items() {
        val comeEvents = (1L..101L).map {
            ComeEvent(
                id = it,
                startDate = ZonedDateTime.of(testDate, LocalTime.of(8, 0), ZoneId.systemDefault()),
                endDate = null,
                workDayId = 1L
            )
        }

        val workDays = (1L..101L).map {
            WorkDay(
                id = it,
                date = testDate.plusDays(it - 1),
                beginSlot = DateTimeUtils.getStartDayTime(testDate.plusDays(it - 1)),
                endSlot = DateTimeUtils.getEndDayTime(testDate.plusDays(it - 1)),
                events = if (it == 1L) comeEvents.toMutableList() else mutableListOf()
            )
        }

        workDayRepository.stub {
            on { getAllPaged() } doAnswer { { TestPagingSource(workDays) } }
        }

        launchFragmentInHiltContainer<WorkDaysHistoryFragment> {}

        onView(withId(R.id.work_days_history_rv)).check(matches(isDisplayed()))
        onView(withId(R.id.work_days_history_rv)).check(matches(withItemCount(101)))

        onView(withId(R.id.work_days_history_rv)).perform(
            RecyclerViewActions.actionOnItemAtPosition<WorkDayAdapter.WorkDayViewHolder>(
                0,
                clickChildViewWithId(R.id.day_expand)
            )
        )

        onView(withId(R.id.day_events_list)).check(matches(isDisplayed()))
        onView(withId(R.id.day_events_list)).check(matches(withItemCount(101)))
    }
}

class TestPagingSource(private val data: List<WorkDay>) : PagingSource<Int, WorkDay>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, WorkDay> {
        return LoadResult.Page(data, null, null)
    }

    override fun getRefreshKey(state: PagingState<Int, WorkDay>): Int? {
        return null
    }
}