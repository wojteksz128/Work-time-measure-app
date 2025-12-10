package net.wojteksz128.worktimemeasureapp.window.settings

import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.util.launchFragmentInHiltContainer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class SyncFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Suppress("PropertyName")
    @Inject
    lateinit var Settings: Settings

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun givenFragmentStartedWithDisabledSync_thenDisplaysSyncPreferences() {
        launchFragmentInHiltContainer<SyncFragment>()

        syncSettings {
            setTimeSync(false)
            verifySyncDisabled()
        }
    }

    @Test
    fun givenFragmentStartedWithEnabledSync_thenDisplaysSyncPreferences() {
        launchFragmentInHiltContainer<SyncFragment>()

        syncSettings {
            setTimeSync(true)
            verifySyncEnabled()
        }
    }

    @Test
    fun whenTimeSyncSwitchIsToggled_thenValueIsSaved() {
        val initialValue = Settings.Sync.TimeSync.Enabled.valueNullable ?: false

        launchFragmentInHiltContainer<SyncFragment>()

        syncSettings {
            toggleTimeSync()
        }

        val newValue = Settings.Sync.TimeSync.Enabled.value
        assertNotEquals(initialValue, newValue)
        assertEquals(newValue, Settings.Sync.TimeSync.Enabled.value)
    }

    @Test
    fun whenTimeSyncServerIsChanged_thenValueIsSaved() {
        val initialValue =
            runBlocking { Settings.Sync.TimeSync.ServerAddress.getValueAsync() ?: "" }
        val newServerAddress = "time.google.com"

        launchFragmentInHiltContainer<SyncFragment>()

        syncSettings {
            setTimeSync(true)
            changeServer(newServerAddress)
        }

        val newValue = runBlocking { Settings.Sync.TimeSync.ServerAddress.getValueAsync() }
        assertEquals(newServerAddress, newValue?.hostName)
        assertNotEquals(initialValue, newValue)
        assertEquals(newValue, runBlocking { Settings.Sync.TimeSync.ServerAddress.getValueAsync() })
    }
}
