package net.wojteksz128.worktimemeasureapp.util

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingRootException
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.util.TreeIterables
import org.hamcrest.Matcher
import org.hamcrest.StringDescription

fun waitForView(viewMatcher: Matcher<View>, timeoutMs: Long = 5000L): ViewAction =
    object : ViewAction {
        override fun getConstraints() = isRoot()
        override fun getDescription() = "Wait for view $timeoutMs ms"

        override fun perform(uiController: UiController, view: View?) {
            uiController.loopMainThreadUntilIdle()
            val endTime = System.currentTimeMillis() + timeoutMs

            do {
                for (child in TreeIterables.breadthFirstViewTraversal(view)) {
                    if (viewMatcher.matches(child) && child.isShown) {
                        return
                    }
                }
                uiController.loopMainThreadForAtLeast(50)
            } while (System.currentTimeMillis() < endTime)

            val description = StringDescription()
            viewMatcher.describeTo(description)
            throw NoMatchingViewException.Builder()
                .withRootView(view)
                .withViewMatcher(viewMatcher)
                .build()
        }
    }

/**
 * Waits until a view appears in a dialog (a separate dialog window root).
 * Must be called from the instrumentation test thread — polling is done via [Thread.sleep]
 * between attempts on the test thread, where [onView]/[check] calls are permitted.
 */
fun waitForDialogView(viewMatcher: Matcher<View>, timeoutMs: Long = 5000L) {
    val endTime = System.currentTimeMillis() + timeoutMs

    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    var lastError: Throwable = AssertionError()

    do {
        lastError = try {
            onView(viewMatcher).inRoot(isDialog()).check(matches(isDisplayed()))
            return
        } catch (e: NoMatchingViewException) {
            e
        } catch (e: NoMatchingRootException) {
            e
        } catch (e: AssertionError) {
            e
        }
        Thread.sleep(50)
    } while (System.currentTimeMillis() < endTime)

    throw lastError
}

/**
 * Waits until the dialog window disappears — i.e. the dialog root no longer exists.
 * Must be called from the instrumentation test thread.
 */
fun waitForDialogToDisappear(timeoutMs: Long = 5000L) {
    val endTime = System.currentTimeMillis() + timeoutMs

    do {
        try {
            onView(isRoot()).inRoot(isDialog()).check(matches(isDisplayed()))
            // dialog still exists — keep waiting
        } catch (_: NoMatchingRootException) {
            return
        } catch (_: NoMatchingViewException) {
            return
        } catch (_: AssertionError) {
            // treat any other assertion error as dialog being gone
            return
        }
        Thread.sleep(50)
    } while (System.currentTimeMillis() < endTime)

    throw AssertionError("Timed out waiting for dialog to disappear")
}

/**
 * Waits until the dialog containing the given view disappears.
 * @see waitForDialogToDisappear
 */
fun waitForDialogToDisappear(viewMatcher: Matcher<View>, timeoutMs: Long = 5000L) {
    val endTime = System.currentTimeMillis() + timeoutMs

    do {
        try {
            onView(viewMatcher).inRoot(isDialog()).check(matches(isDisplayed()))
            // dialog still visible — keep waiting
        } catch (_: NoMatchingViewException) {
            return
        } catch (_: NoMatchingRootException) {
            return
        } catch (_: AssertionError) {
            return
        }
        Thread.sleep(50)
    } while (System.currentTimeMillis() < endTime)

    throw AssertionError("Timed out waiting for dialog to disappear: $viewMatcher")
}

/**
 * Waits until the given view no longer exists in the view hierarchy (not inside a dialog).
 * Must be called from the instrumentation test thread.
 */
fun waitForViewToDisappear(viewMatcher: Matcher<View>, timeoutMs: Long = 5000L) {
    val endTime = System.currentTimeMillis() + timeoutMs

    do {
        try {
            onView(viewMatcher).check(doesNotExist())
            return
        } catch (_: AssertionError) {
        }
        Thread.sleep(50)
    } while (System.currentTimeMillis() < endTime)

    throw AssertionError("Timed out waiting for view to disappear: $viewMatcher")
}

/**
 * Executes [UiController.loopMainThreadUntilIdle] on the main UI thread (Espresso synchronization).
 * Useful after operations that trigger animations or asynchronous view updates.
 */
fun idleMainThread(): ViewAction = object : ViewAction {
    override fun getConstraints() = isRoot()
    override fun getDescription() = "Idle main thread"
    override fun perform(uiController: UiController, view: View?) {
        uiController.loopMainThreadUntilIdle()
    }
}
