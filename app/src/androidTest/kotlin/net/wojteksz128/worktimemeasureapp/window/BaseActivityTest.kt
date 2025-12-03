package net.wojteksz128.worktimemeasureapp.window

import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import net.wojteksz128.worktimemeasureapp.model.fieldType.DayType
import net.wojteksz128.worktimemeasureapp.module.dayOff.DayOffService
import net.wojteksz128.worktimemeasureapp.settings.InitialSettingsPreparer
import net.wojteksz128.worktimemeasureapp.util.createTestImageUri
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.window.dashboard.DashboardActivity
import net.wojteksz128.worktimemeasureapp.window.dashboard.dashboard
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.stub
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class BaseActivityTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createEmptyComposeRule()

    @Inject
    lateinit var dayOffService: DayOffService

    @Inject
    lateinit var dateTimeProvider: DateTimeProvider

    @Inject
    lateinit var initialSettingsPreparer: InitialSettingsPreparer

    private lateinit var scenario: ActivityScenario<DashboardActivity>

    @Before
    fun setup() {
        Intents.init()
        hiltRule.inject()

        initialSettingsPreparer.initSettings()

        dayOffService.stub {
            onBlocking { getDayType(dateTimeProvider.currentTime) } doReturn DayType.WorkDay
            onBlocking { getDayType(dateTimeProvider.currentDate) } doReturn DayType.WorkDay
        }

        scenario = ActivityScenario.launch(DashboardActivity::class.java)
    }

    @After
    fun tearDown() {
        Intents.release()
        scenario.close()
    }

    @Test
    fun test_toolbarAndHamburgerMenuAreVisible() {
        dashboard {
            verifyToolbarAndHamburgerAreVisible()
        }
    }

    @Test
    fun test_hamburgerMenuOpensDrawer() {
        dashboard {
            openNavigationDrawer()
            verifyDrawerIsOpen()
        }
    }

    @Test
    fun test_navigationToHome() {
        dashboard {
            navigateToHome {
                this.verifyIsDisplayed()
            }
        }
    }

    @Test
    fun test_navigationToHistory() {
        dashboard {
            navigateToHistory {
                this.verifyIsDisplayed()
            }
        }
    }

    @Test
    fun test_navigationToDaysOffList() {
        dashboard {
            navigateToDaysOffList(composeTestRule) {
                verifyIsDisplayed()
            }
        }
    }

    @Test
    fun test_navigationToSettings() {
        dashboard {
            navigateToSettings {
                verifyIsDisplayed()
            }
        }
    }

    @Test
    fun test_aboutSnackbar() {
        dashboard {
            navigateToAbout()
            verifyAboutSnackbarIsDisplayed()
        }
    }

    @Test
    fun whenProfileDataChanged_thenDrawerHeaderIsUpdated() {
        val newUsername = "John Doe"
        val newEmail = "john.doe@example.com"

        dashboard {
            navigateToSettings {
                openProfileSettings {
                    editUsername {
                        enterText(newUsername)
                        clickOk()
                    }
                    editEmail {
                        enterText(newEmail)
                        clickOk()
                    }
                    goBack()
                }
                goBack()
            }

            openNavigationDrawer()
            verifyDrawerUsername(newUsername)
            verifyDrawerEmail(newEmail)
        }
    }

    @Test
    fun whenProfileImageChanged_thenDrawerHeaderIsUpdated() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        mockExampleImageWhenRequired(context)

        dashboard {
            navigateToSettings {
                openProfileSettings {
                    editProfileImage {
                        selectTestImage()
                    }
                    goBack()
                }
                goBack()
            }

            openNavigationDrawer()
            verifyDrawerProfileImageIsChanged(context)
        }
    }

    private fun mockExampleImageWhenRequired(context: Context) {
        val testImageUri = createTestImageUri(context)

        val resultData = Intent().setData(testImageUri)
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
        intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(result)
    }
}
