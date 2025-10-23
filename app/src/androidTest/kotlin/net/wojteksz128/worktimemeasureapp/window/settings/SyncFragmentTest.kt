package net.wojteksz128.worktimemeasureapp.window.settings

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.util.launchFragmentInHiltContainer
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class SyncFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Test
    fun givenFragmentStarted_thenDisplaysSyncPreferences() {
        launchFragmentInHiltContainer<SyncFragment>()

        onView(withText(R.string.settings_sync_timeSync_category_title)).check(matches(isDisplayed()))
        onView(withText(R.string.settings_sync_timeSync_enable_title)).check(matches(isDisplayed()))
    }
}