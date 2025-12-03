package net.wojteksz128.worktimemeasureapp.robot.base

import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.isNotEnabled
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not

abstract class BaseScreenRobot {
    protected val isDisplayed: ViewAssertion = matches(isDisplayed())
    protected val isNotDisplayed: ViewAssertion = matches(not(isDisplayed()))
    protected val isDisplayedAndEnabled: ViewAssertion = matches(allOf(isDisplayed(), isEnabled()))
    protected val isDisplayedAndNotEnabled: ViewAssertion = matches(
        allOf(
            isDisplayed(),
            isNotEnabled()
        )
    )

    abstract fun verifyIsDisplayed()
}