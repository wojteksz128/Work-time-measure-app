package net.wojteksz128.worktimemeasureapp.window.settings

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.api.HolidayProvider
import net.wojteksz128.worktimemeasureapp.robot.base.BaseScreenRobot
import net.wojteksz128.worktimemeasureapp.util.setSwitchTo
import org.hamcrest.CoreMatchers.allOf

fun daysOffSettings(func: DaysOffSettingsRobot.() -> Unit) = DaysOffSettingsRobot().apply { func() }

class DaysOffSettingsRobot : BaseScreenRobot() {
    private val publicCategoryTitle = withText(R.string.settings_daysOff_public_category_title)
    private val syncWithApiTitle = withText(R.string.settings_daysOff_public_syncWithApi_title)
    private val syncWithApiClickable = allOf(isClickable(), hasDescendant(syncWithApiTitle))
    private val providerTitle = withText(R.string.settings_daysOff_public_provider_title)
    private val countryTitle = withText(R.string.settings_daysOff_public_country_title)
    private val syncNowTitle = withText(R.string.settings_daysOff_public_syncNow_title)
    private val snackbar = withId(com.google.android.material.R.id.snackbar_text)

    fun setSyncWithApi(enabled: Boolean) {
        onView(syncWithApiClickable).perform(setSwitchTo(enabled))
        Thread.sleep(500)
    }

    fun toggleSyncWithApi() {
        onView(syncWithApiTitle).perform(click())
        Thread.sleep(500)
    }

    fun changeProvider(provider: HolidayProvider) {
        onView(providerTitle).perform(click())
        Thread.sleep(500)
        onView(withText(provider.displayName)).perform(click())
        Thread.sleep(500)
    }

    fun changeCountry(countryName: String) {
        onView(countryTitle).perform(click())
        Thread.sleep(500)
        onView(withText(countryName)).perform(click())
        Thread.sleep(500)
    }

    fun syncNow() {
        onView(syncNowTitle).perform(click())
        Thread.sleep(500)
    }

    override fun verifyIsDisplayed() {
        onView(publicCategoryTitle).check(isDisplayed)
        onView(syncWithApiTitle).check(isDisplayed)
    }

    fun verifySyncDisabled() {
        onView(providerTitle).check(isDisplayedAndNotEnabled)
        onView(countryTitle).check(isDisplayedAndNotEnabled)
        onView(syncNowTitle).check(isDisplayedAndNotEnabled)
    }

    fun verifySyncEnabledButNotFulfilled() {
        onView(providerTitle).check(isDisplayedAndEnabled)
        onView(countryTitle).check(isDisplayedAndEnabled)
        onView(syncNowTitle).check(isDisplayedAndNotEnabled)
    }

    fun verifySyncEnabledAndFulfilled() {
        onView(providerTitle).check(isDisplayedAndEnabled)
        onView(countryTitle).check(isDisplayedAndEnabled)
        onView(syncNowTitle).check(isDisplayedAndEnabled)
    }

    fun verifySnackbarIsDisplayed() {
        onView(snackbar).check(isDisplayed)
    }
}
