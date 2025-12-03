package net.wojteksz128.worktimemeasureapp.window.history

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.withId
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.robot.base.BaseScreenRobot

fun history(func: HistoryRobot.() -> Unit) = HistoryRobot().apply { func() }

class HistoryRobot : BaseScreenRobot() {

    private val historyList = withId(R.id.work_days_history_rv)

    override fun verifyIsDisplayed() {
        onView(historyList).check(isDisplayed)
    }
}
