package net.wojteksz128.worktimemeasureapp.window.settings

import android.content.Context
import androidx.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.settings.InitialSettingsPreparer
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.util.launchFragmentInHiltContainer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.DayOfWeek
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class WeekWorkTimeFragmentTest {

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
        hiltRule.inject()
        context = ApplicationProvider.getApplicationContext()
        initialSettingsPreparer.initSettings()
        launchFragmentInHiltContainer<WeekWorkTimeFragment>()
    }

    @Test
    fun givenFragmentStarted_thenDisplaysWeekWorkTimePreferences() {
        weekWorkTimeSettings {
            verifyIsDisplayed()
        }
    }

    @Test
    fun whenFirstDayOfWeekIsChanged_thenValueIsSaved() {
        val key = context.getString(R.string.settings_key_workTime_firstWeekDay)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val initialValue = sharedPreferences.getString(key, null)

        weekWorkTimeSettings {
            changeFirstDayOfWeek(R.string.settings_workTime_week_firstWeekDay_sundayEntry)
        }

        val newValue = sharedPreferences.getString(key, null)
        assertNotEquals(initialValue, newValue)
        assertEquals(DayOfWeek.SUNDAY.name, newValue)
        assertEquals(newValue, Settings.WorkTime.Week.FirstWeekDay.value)
    }

    @Test
    fun whenDaysOfWorkingWeekIsClicked_thenValueIsSaved() {
        val key = context.getString(R.string.settings_key_workTime_week_daysOfWorkingWeek)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val initialValue = sharedPreferences.getStringSet(key, emptySet())

        weekWorkTimeSettings {
            changeDaysOfWorkingWeek(
                R.string.settings_workTime_week_firstWeekDay_mondayEntry,
                R.string.settings_workTime_week_firstWeekDay_wednesdayEntry
            )
        }

        val newValue = sharedPreferences.getStringSet(key, emptySet())
        assertNotEquals(initialValue, newValue)
        newValue!!.let {
            assertFalse(it.contains(DayOfWeek.MONDAY.name))
            assertFalse(it.contains(DayOfWeek.WEDNESDAY.name))
        }
        assertEquals(newValue, Settings.WorkTime.Week.DaysOfWorkingWeek.value)
    }

    @Test
    fun whenWorkTimeInDayIsChanged_thenValueIsSaved() {
        val key = context.getString(R.string.settings_key_workTime_duration)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val initialValue = sharedPreferences.getInt(key, 0)

        weekWorkTimeSettings {
            changeWorkTimeInDay(10, 15)
        }

        val newValue = sharedPreferences.getInt(key, 0)

        assertNotEquals(initialValue, newValue)
        assertEquals(10 * 60 + 15, newValue)
        assertEquals(newValue.toLong(), Settings.WorkTime.Week.Duration.value.toMinutes())
    }
}
