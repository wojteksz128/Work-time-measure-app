package net.wojteksz128.worktimemeasureapp.window.settings

import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import androidx.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.settings.InitialSettingsPreparer
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.util.createTestImageUri
import net.wojteksz128.worktimemeasureapp.util.getInternalImageUri
import net.wojteksz128.worktimemeasureapp.util.launchFragmentInHiltContainer
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
        launchFragmentInHiltContainer<ProfileFragment>()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun givenFragmentStarted_thenDisplaysProfilePreferences() {
        profileSettings {
            verifyIsDisplayed()
        }
    }

    @Test
    fun whenProfileImageChanged_thenValueIsSaved() {
        val testImageUri = createTestImageUri(context)
        val resultData = Intent().setData(testImageUri)
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
        intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(result)

        profileSettings {
            editProfileImage {
                selectTestImage()
            }
        }

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

        profileSettings {
            editUsername {
                enterText(newUsername)
                clickOk()
            }
        }

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

        profileSettings {
            editEmail {
                enterText(newEmail)
                clickOk()
            }
        }

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        assertEquals(
            newEmail,
            sharedPreferences.getString(context.getString(R.string.settings_key_profile_email), "")
        )
        assertEquals(newEmail, Settings.Profile.Email.valueNullable)
    }

    @Test
    fun whenInvalidEmailChanged_thenErrorIsShownAndValueIsNotSaved() {
        val oldEmail = Settings.Profile.Email.valueNullable
        val invalidEmail = "invalid-email"

        profileSettings {
            editEmail {
                enterText(invalidEmail)
                verifyErrorIsDisplayed(context.getString(R.string.settings_profile_mail_error))
                verifyOkButtonIsNotEnabled()
                clickCancel()
            }
        }

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        assertEquals(
            oldEmail,
            sharedPreferences.getString(
                context.getString(R.string.settings_key_profile_email),
                null
            )
        )
    }
}
