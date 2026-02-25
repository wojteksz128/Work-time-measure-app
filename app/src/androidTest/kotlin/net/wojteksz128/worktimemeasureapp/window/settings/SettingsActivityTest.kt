package net.wojteksz128.worktimemeasureapp.window.settings

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import net.wojteksz128.worktimemeasureapp.settings.InitialSettingsPreparer
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class SettingsActivityTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var activityRule = ActivityScenarioRule(SettingsActivity::class.java)

    @Inject
    lateinit var initialSettingsPreparer: InitialSettingsPreparer

    @Before
    fun setup() {
        hiltRule.inject()
        initialSettingsPreparer.initSettings()
    }

    @Test
    fun givenActivityStarted_whenNoSavedState_thenDisplaysHeaderFragment() {
        settings {
            verifyHeaderFragmentIsDisplayed()
        }
    }

    @Test
    fun whenClickOnProfilePreference_thenDisplaysProfileFragment() {
        settings {
            openProfileSettings {
                verifyIsDisplayed()
            }
        }
    }

    @Test
    fun whenClickOnWorkTimePreference_thenDisplaysWorkTimeFragment() {
        settings {
            openWorkTimeSettings {
                verifyIsDisplayed()
            }
        }
    }

    @Test
    fun whenClickOnDaysOffPreference_thenDisplaysDaysOffFragment() {
        settings {
            openDaysOffSettings {
                verifyIsDisplayed()
            }
        }
    }

    @Test
    fun whenClickOnSyncPreference_thenDisplaysSyncFragment() {
        settings {
            openSyncSettings {
                verifyIsDisplayed()
            }
        }
    }

    @Test
    fun whenClickOnBackupPreference_thenDisplaysBackupFragment() {
        settings {
            openBackupSettings {
                verifyIsDisplayed()
            }
        }
    }
}
