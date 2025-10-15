package net.wojteksz128.worktimemeasureapp.util

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.matcher.BoundedMatcher
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.hamcrest.Description
import org.hamcrest.Matcher

/**
 * A custom matcher to assert the number of items in a RecyclerView.
 * Espresso does not provide this out-of-the-box, so creating a helper is a common practice.
 */
fun withItemCount(count: Int): Matcher<View> {
    return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
        override fun describeTo(description: Description) {
            description.appendText("has item count: $count")
        }

        override fun matchesSafely(item: RecyclerView?): Boolean {
            return item?.adapter?.itemCount == count
        }
    }
}

fun <T> awaitState(
    stateFlow: StateFlow<T>,
    timeoutMillis: Long = 5000L,
    predicate: (T) -> Boolean,
) =
    runBlocking {
        withTimeout(timeoutMillis) {
            stateFlow.first { predicate(it) }
        }
    }