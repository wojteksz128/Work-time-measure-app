package net.wojteksz128.worktimemeasureapp.window.history

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.repository.ComeEventRepository
import net.wojteksz128.worktimemeasureapp.repository.WorkDayRepository
import net.wojteksz128.worktimemeasureapp.util.TestPagingSource
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import net.wojteksz128.worktimemeasureapp.util.launchFragmentInHiltContainer
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
        mockReturningPagedWorkDays(emptyList())

        launchFragmentInHiltContainer<WorkDaysHistoryFragment> {}

        historyListFragment {
            verifyIsDisplayed()
            verifyItemCount(0)
        }
    }

    @Test
    fun testFragmentDisplaysWithData() {
        mockReturningPagedWorkDays(listOf(workDay))

        launchFragmentInHiltContainer<WorkDaysHistoryFragment> {}

        historyListFragment {
            verifyIsDisplayed()
            verifyItemCount(1)
        }
    }

    @Test
    fun testWorkDayClick_navigateToDetails() {
        val navController = mock(NavController::class.java)

        mockReturningPagedWorkDays(listOf(workDay))

        launchFragmentInHiltContainer<WorkDaysHistoryFragment> {
            Navigation.setViewNavController(requireView(), navController)
        }

        historyListFragment {
            clickWorkDayAt(0)
        }

        verify(navController).navigate(any<Int>(), any<Bundle>())
    }

    @Test
    fun testSwipeLeftOnComeEvent_showsEditDialog() {
        mockReturningPagedWorkDays(listOf(workDay))

        launchFragmentInHiltContainer<WorkDaysHistoryFragment> {}

        historyListFragment {
            expandWorkDayAt(0)
            comeEditRecyclerView {
                swipeLeftOnEvent(0) {
                    verifyIsDisplayed()
                }
            }
        }
    }

    @Test
    fun testSwipeRightOnComeEvent_showsDeleteDialog() {
        mockReturningPagedWorkDays(listOf(workDay))

        launchFragmentInHiltContainer<WorkDaysHistoryFragment> {}

        historyListFragment {
            expandWorkDayAt(0)
            comeEditRecyclerView {
                swipeRightOnEvent(0) {
                    verifyIsDisplayed()
                }
            }
        }
    }

    @Test
    fun testAcceptDeletion_deletesComeEventAndShowsSnackbar() {
        mockReturningPagedWorkDays(listOf(workDay))

        launchFragmentInHiltContainer<WorkDaysHistoryFragment> {}

        historyListFragment {
            expandWorkDayAt(0)
            comeEditRecyclerView {
                swipeRightOnEvent(0) {
                    clickDelete()
                }
            }
        }

        verifyBlocking(comeEventRepository) { delete(any()) }

        historyListFragment {
            verifySnackbarIsDisplayed()
        }
    }

    @Test
    fun testCancelDeletion_dialogIsDismissed() {
        mockReturningPagedWorkDays(listOf(workDay))

        launchFragmentInHiltContainer<WorkDaysHistoryFragment> {}

        historyListFragment {
            expandWorkDayAt(0)
            comeEditRecyclerView {
                swipeRightOnEvent(0) {
                    verifyIsDisplayed()
                    clickCancel()
                    verifyDeleteDialogIsDismissed()
                }
            }
        }
    }

    @Test
    fun testDisplaysMoreThan100Items() {
        val numberOfComeEvents = 101L
        val numberOfWorkDays = 101L

        mockReturningPagedWorkDays(assembleTestWorkDays(numberOfComeEvents, numberOfWorkDays))

        launchFragmentInHiltContainer<WorkDaysHistoryFragment> {}

        historyListFragment {
            verifyIsDisplayed()
            verifyItemCount(numberOfWorkDays.toInt())
            expandWorkDayAt(0)
            verifyComeEventsListIsDisplayed()
            verifyComeEventsCount(numberOfComeEvents.toInt())
        }
    }

    private fun assembleTestWorkDays(
        @Suppress("SameParameterValue") numberOfComeEvents: Long,
        @Suppress("SameParameterValue") numberOfWorkDays: Long,
    ): List<WorkDay> {
        val comeEvents = (1L..numberOfComeEvents).map {
            ComeEvent(
                id = it,
                startDate = ZonedDateTime.of(testDate, LocalTime.of(8, 0), ZoneId.systemDefault()),
                endDate = null,
                workDayId = 1L
            )
        }

        return (1L..numberOfWorkDays).map {
            WorkDay(
                id = it,
                date = testDate.plusDays(it - 1),
                beginSlot = DateTimeUtils.getStartDayTime(testDate.plusDays(it - 1)),
                endSlot = DateTimeUtils.getEndDayTime(testDate.plusDays(it - 1)),
                events = if (it == 1L) comeEvents.toMutableList() else mutableListOf()
            )
        }
    }

    private fun mockReturningPagedWorkDays(workDayList: List<WorkDay>) {
        workDayRepository.stub {
            on { getAllPaged() } doAnswer { { TestPagingSource(workDayList) } }
        }
    }
}
