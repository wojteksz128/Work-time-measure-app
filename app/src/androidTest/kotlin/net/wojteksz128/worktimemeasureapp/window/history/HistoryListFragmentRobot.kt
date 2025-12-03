package net.wojteksz128.worktimemeasureapp.window.history

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.withId
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.robot.base.BaseScreenRobot
import net.wojteksz128.worktimemeasureapp.util.clickChildViewWithId
import net.wojteksz128.worktimemeasureapp.util.withItemCount
import net.wojteksz128.worktimemeasureapp.window.history.WorkDayAdapter.WorkDayViewHolder

fun historyListFragment(func: HistoryListFragmentRobot.() -> Unit) =
    HistoryListFragmentRobot().apply { func() }

class HistoryListFragmentRobot : BaseScreenRobot() {

    private val comeEventsListRobot = ComeEventsListRobot(R.id.day_events_list)
    private val historyList = withId(R.id.work_days_history_rv)
    private val snackbar = withId(com.google.android.material.R.id.snackbar_text)

    override fun verifyIsDisplayed() {
        onView(historyList).check(isDisplayed)
    }

    fun verifyItemCount(count: Int) {
        onView(historyList).check(itemCount(count))
    }

    fun clickWorkDayAt(position: Int) {
        onView(historyList).perform(clickAt(position))
    }

    fun expandWorkDayAt(position: Int) {
        onView(historyList).perform(clickEventsExpandingAt(position))
    }

    fun comeEditRecyclerView(func: ComeEventsListRobot.() -> Unit) {
        comeEventsListRobot.apply { func() }
    }

    fun verifySnackbarIsDisplayed() {
        onView(snackbar).check(isDisplayed)
    }

    fun verifyComeEventsListIsDisplayed() {
        onView(withId(R.id.day_events_list)).check(isDisplayed)
    }

    fun verifyComeEventsCount(count: Int) {
        onView(withId(R.id.day_events_list)).check(itemCount(count))
    }

    private fun clickAt(position: Int) =
        actionOnItemAtPosition<WorkDayViewHolder>(position, click())

    private fun clickEventsExpandingAt(position: Int) =
        actionOnItemAtPosition<WorkDayViewHolder>(position, clickChildViewWithId(R.id.day_expand))

    private fun itemCount(count: Int): ViewAssertion? = matches(withItemCount(count))
}
