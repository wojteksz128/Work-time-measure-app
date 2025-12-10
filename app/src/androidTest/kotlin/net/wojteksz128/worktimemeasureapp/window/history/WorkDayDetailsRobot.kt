package net.wojteksz128.worktimemeasureapp.window.history

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.robot.base.BaseScreenRobot
import net.wojteksz128.worktimemeasureapp.util.withItemCount

fun workDayDetails(func: WorkDayDetailsRobot.() -> Unit) = WorkDayDetailsRobot().apply { func() }

class WorkDayDetailsRobot : BaseScreenRobot() {

    private val comeEventsListRobot =
        ComeEventsListRobot(R.id.work_day_details_come_events)

    private val noComeEventsMessage =
        withId(R.id.work_day_details_come_event_list_no_events_message)
    private val noHistoryEventsMessage = withId(R.id.work_day_details_history_no_events_message)
    private val historyEventsList = withId(R.id.work_day_details_history_entries)

    override fun verifyIsDisplayed() {
        onView(historyEventsList).check(isDisplayed)
    }

    fun verifyDetailsAreDisplayed(monthAndYear: String, day: String, dayOfWeek: String) {
        onView(withText(monthAndYear)).check(isDisplayed)
        onView(withText(day)).check(isDisplayed)
        onView(withText(dayOfWeek)).check(isDisplayed)
    }

    fun eventsRecyclerView(func: ComeEventsListRobot.() -> Unit) {
        comeEventsListRobot.apply { func() }
    }

    fun verifyNoEventsMessageIsDisplayed() {
        onView(noComeEventsMessage).check(isDisplayed)
    }

    fun verifyNoHistoryMessageIsDisplayed() {
        onView(noHistoryEventsMessage).check(isDisplayed)
    }

    fun verifyHistoryListIsDisplayed() {
        onView(historyEventsList).check(isDisplayed)
    }

    fun verifyHistoryItemCount(count: Int) {
        onView(historyEventsList).check(matches(withItemCount(count)))
    }

    fun verifyNoHistoryMessageIsNotDisplayed() {
        onView(noHistoryEventsMessage).check(isNotDisplayed)
    }

    fun verifySnackbarIsShown(stringId: Int) {
        onView(withText(stringId)).check(isDisplayed)
    }
}
