package net.wojteksz128.worktimemeasureapp.window.settings

import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import androidx.compose.ui.test.isEnabled
import androidx.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.matcher.ViewMatchers.hasErrorText
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.settings.InitialSettingsPreparer
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.util.createTestImageUri
import net.wojteksz128.worktimemeasureapp.util.getInternalImageUri
import net.wojteksz128.worktimemeasureapp.util.launchFragmentInHiltContainer
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class ProfileFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var initialSettingsPreparer: InitialSettingsPreparer

    @Suppress("PropertyName")
    @Inject
    lateinit var Settings: Settings

    private lateinit var context: Context

    @Before
    fun setUp() {
        Intents.init()
        hiltRule.inject()
        context = ApplicationProvider.getApplicationContext()
        initialSettingsPreparer.initSettings()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun givenFragmentStarted_thenDisplaysProfilePreferences() {
        launchFragmentInHiltContainer<ProfileFragment>()

        onView(withText(R.string.image_view_preference_image_hint)).check(matches(isDisplayed()))
        onView(withText(R.string.settings_profile_username_title)).check(matches(isDisplayed()))
        onView(withText(R.string.settings_profile_email_title)).check(matches(isDisplayed()))
    }

    @Test
    fun whenProfileImageChanged_thenValueIsSaved() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val testImageUri = createTestImageUri(context)

        val resultData = Intent().setData(testImageUri)
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
        intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(result)

        launchFragmentInHiltContainer<ProfileFragment>()

        onView(withText(R.string.image_view_preference_image_hint)).perform(click())
        intended(hasAction(Intent.ACTION_GET_CONTENT))

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val expectedImagePath = getInternalImageUri(context, testImageUri)
        assertEquals(
            expectedImagePath,
            sharedPreferences.getString(context.getString(R.string.settings_key_profile_image), "")
        )
        assertEquals(expectedImagePath, Settings.Profile.ImagePath.valueNullable)
    }

    @Test
    fun whenUsernameChanged_thenValueIsSaved() {
        val newUsername = "John Doe"
        launchFragmentInHiltContainer<ProfileFragment>()

        // Click on username preference
        onView(withText(R.string.settings_profile_username_title)).perform(click())

        // Type new username and click OK
        onView(withId(android.R.id.edit)).perform(replaceText(newUsername))
        onView(withText("OK")).perform(click())

        // Verify that the preference was saved
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        assertEquals(
            newUsername,
            sharedPreferences.getString(
                context.getString(R.string.settings_key_profile_username),
                ""
            )
        )
        assertEquals(newUsername, Settings.Profile.Username.valueNullable)
    }

    @Test
    fun whenValidEmailChanged_thenValueIsSaved() {
        val newEmail = "test@example.com"
        launchFragmentInHiltContainer<ProfileFragment>()

        // Click on email preference
        onView(withText(R.string.settings_profile_email_title)).perform(click())

        // Type new email and click OK
        onView(withId(android.R.id.edit)).perform(replaceText(newEmail))
        onView(withText("OK")).perform(click())

        // Verify that the preference was saved
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        assertEquals(
            newEmail,
            sharedPreferences.getString(context.getString(R.string.settings_key_profile_email), "")
        )
        assertEquals(newEmail, Settings.Profile.Email.valueNullable)
    }

    @Test
    fun whenInvalidEmailChanged_thenErrorIsShownAndValueIsNotSaved() {
        val invalidEmail = "invalid-email"
        launchFragmentInHiltContainer<ProfileFragment>()

        // Click on email preference
        onView(withText(R.string.settings_profile_email_title)).perform(click())

        // Type invalid email
        onView(withId(android.R.id.edit)).perform(replaceText(invalidEmail))

        // Check if error is displayed and OK button is disabled
        onView(hasErrorText(context.getString(R.string.settings_profile_mail_error)))
            .check(matches(isDisplayed()))
        onView(withText("OK")).check(matches(not(isEnabled())))

        // Click cancel
        onView(withText("Cancel")).perform(click())

        // Verify that the preference was not saved
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        assertEquals(
            "",
            sharedPreferences.getString(context.getString(R.string.settings_key_profile_email), "")
        )
    }
}