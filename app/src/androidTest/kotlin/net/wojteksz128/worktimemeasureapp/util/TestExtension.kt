package net.wojteksz128.worktimemeasureapp.util

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Checkable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.hamcrest.CoreMatchers.allOf
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

fun clickChildViewWithId(id: Int): ViewAction {
    return object : ViewAction {
        override fun getConstraints(): Matcher<View>? {
            return null
        }

        override fun getDescription(): String {
            return "Click on a child view with specified id."
        }

        override fun perform(uiController: UiController, view: View) {
            val v = view.findViewById<View>(id)
            v.performClick()
        }
    }
}

fun setSwitchTo(targetValue: Boolean): ViewAction {
    return object : ViewAction {
        override fun getConstraints(): Matcher<View> {
            return allOf(isDisplayed(), isClickable())
        }

        override fun getDescription(): String {
            return "Set switch to $targetValue"
        }

        override fun perform(
            uiController: UiController,
            view: View,
        ) {
            val switchView =
                findClickableChild(view) ?: throw IllegalStateException("Switch view not found")

            if (switchView.isChecked != targetValue) {
                switchView.toggle()
            }
        }

        private fun findClickableChild(view: View): Checkable? {
            if (view is Checkable) {
                return view
            }
            if (view is ViewGroup) {
                for (i in 0 until view.childCount) {
                    val child = view.getChildAt(i)
                    val clickableChild = findClickableChild(child)
                    if (clickableChild != null) {
                        return clickableChild
                    }
                }
            }
            return null
        }
    }
}

fun printViewHierarchy(): ViewAction {
    data class Depth(val childrenNo: Int, var currentChild: Int = 0, var isListed: Boolean = true) {
        val hasMoreElements: Boolean
            get() = currentChild < childrenNo - 1

        fun print(isCurrentDepth: Boolean): String {
            val firstChar = if (!isListed) ' '
            else if (!isCurrentDepth) '│'
            else if (hasMoreElements) '├'
            else '└'
            val secondChar = if (isCurrentDepth) '-' else ' '
            return "$firstChar$secondChar"
        }
    }

    return object : ViewAction {
        val LOG_TAG = "ViewHierarchy"

        override fun getConstraints() = isRoot()
        override fun getDescription() = "print view hierarchy"

        override fun perform(uiController: UiController, view: View) {
            Log.d(LOG_TAG, "--- Start of view hierarchy ---")
            dumpViewHierarchy(view, listOf())
            Log.d(LOG_TAG, "--- End of view hierarchy ---")
        }

        private fun dumpViewHierarchy(view: View, depthList: List<Depth>) {
            val indent =
                depthList.mapIndexed { index, depth -> depth.print(index == depthList.lastIndex) }
                    .joinToString("")

            val id = if (view.id != View.NO_ID)
                try {
                    view.resources.getResourceName(view.id)
                } catch (_: Exception) {
                    "no-id-name"
                } else "no-id"

            val text = if (view is TextView) " text='${view.text}'" else ""

            val desc =
                if (view.contentDescription != null) "desc='${view.contentDescription}'" else ""

            val clickable = if (view.isClickable) " CLICKABLE" else ""
            val enabled = if (!view.isEnabled) " DISABLED" else ""
            val checked = if (view is Checkable && view.isChecked) " CHECKED" else ""

            val logLine =
                "${view.javaClass.simpleName} (id=$id, visibility=${getVisibilityString(view.visibility)}$clickable$enabled$checked$text$desc)"
            Log.d("ViewHierarchy", "$indent$logLine")

            if (view is ViewGroup) {
                val currentDepth = depthList.lastOrNull() ?: Depth(0)
                currentDepth.isListed = currentDepth.hasMoreElements

                val depth = Depth(view.childCount)
                val newDepthList = depthList + depth

                for (i in 0 until view.childCount) {
                    depth.currentChild = i
                    dumpViewHierarchy(view.getChildAt(i), newDepthList)
                }
            }
        }

        private fun getVisibilityString(visibility: Int): String {
            return when (visibility) {
                View.VISIBLE -> "VISIBLE"
                View.INVISIBLE -> "INVISIBLE"
                View.GONE -> "GONE"
                else -> visibility.toString()
            }
        }
    }
}