package net.wojteksz128.worktimemeasureapp.window.settings

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.settings.InitialSettingsPreparer
import net.wojteksz128.worktimemeasureapp.util.launchFragmentInHiltContainer
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class DaysOffFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var initialSettingsPreparer: InitialSettingsPreparer

    @Before
    fun setup() {
        hiltRule.inject()
        initialSettingsPreparer.initSettings()
    }

    @Test
    fun givenFragmentStarted_thenDisplaysDaysOffPreferences() {
        launchFragmentInHiltContainer<DaysOffFragment>()

        onView(withText(R.string.settings_daysOff_public_category_title)).check(matches(isDisplayed()))
        onView(withText(R.string.settings_daysOff_public_syncWithApi_title)).check(
            matches(
                isDisplayed()
            )
        )
    }
}