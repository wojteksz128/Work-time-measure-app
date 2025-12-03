package net.wojteksz128.worktimemeasureapp.robot.base

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.matcher.RootMatchers.isDialog
import org.hamcrest.Matcher

abstract class BaseDialogRobot : BaseScreenRobot() {

    protected fun onViewInDialog(viewMatcher: Matcher<View>): ViewInteraction {
        return onView(viewMatcher).inRoot(isDialog())
    }
}