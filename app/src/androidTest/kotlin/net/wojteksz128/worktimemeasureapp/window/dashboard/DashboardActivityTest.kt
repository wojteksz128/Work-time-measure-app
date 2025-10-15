package net.wojteksz128.worktimemeasureapp.window.dashboard

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.NavigationViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.model.WorkState
import net.wojteksz128.worktimemeasureapp.model.fieldType.DayType
import net.wojteksz128.worktimemeasureapp.module.dayOff.DayOffService
import net.wojteksz128.worktimemeasureapp.notification.worktime.WorkTimeInProgressNotification
import net.wojteksz128.worktimemeasureapp.notification.worktime.WorkTimeNotificationFactory
import net.wojteksz128.worktimemeasureapp.notification.worktime.WorkTimeNotificationService
import net.wojteksz128.worktimemeasureapp.repository.WorkDayRepository
import net.wojteksz128.worktimemeasureapp.settings.InitialSettingsPreparer
import net.wojteksz128.worktimemeasureapp.util.awaitState
import net.wojteksz128.worktimemeasureapp.util.comeevent.ComeEventUtils
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.datetime.WorkTimeBalance
import net.wojteksz128.worktimemeasureapp.util.withItemCount
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.timeout
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyBlocking
import org.mockito.kotlin.whenever
import org.threeten.bp.LocalDate
import org.threeten.bp.ZonedDateTime
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class DashboardActivityTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var dayOffService: DayOffService

    @Inject
    lateinit var notificationService: WorkTimeNotificationService

    @Inject
    lateinit var notificationFactory: WorkTimeNotificationFactory

    @Inject
    lateinit var workDayRepository: WorkDayRepository

    @Inject
    lateinit var dateTimeProvider: DateTimeProvider

    @Inject
    lateinit var comeEventUtils: ComeEventUtils

    @Inject
    lateinit var initialSettingsPreparer: InitialSettingsPreparer

    @Inject
    lateinit var workStateFlow: StateFlow<WorkState?>

    private lateinit var scenario: ActivityScenario<DashboardActivity>

    @Before
    fun setup() {
        hiltRule.inject()

        // Initialize application settings first to ensure correct values are loaded
        initialSettingsPreparer.initSettings()

        runBlocking {
            doAnswer { DayType.WorkDay }.whenever(dayOffService).getDayType(any<ZonedDateTime>())
            doAnswer { DayType.WorkDay }.whenever(dayOffService).getDayType(any<LocalDate>())
        }

        // A real notification object is needed to avoid system-level NullPointerExceptions
        val context = ApplicationProvider.getApplicationContext<Context>()
        val channelId = "test_channel"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create a notification channel for Android O and above
        val channel =
            NotificationChannel(channelId, "Test Channel", NotificationManager.IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)

        // Build a real, minimal notification
        val realNotification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Test")
            .setContentText("Test Content")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // A placeholder icon is required
            .build()

        // Mocking creation of WorkTimeInProgressNotification
        val notification = mock<WorkTimeInProgressNotification>().apply {
            // Configure the mock to return the REAL notification object
            doAnswer { realNotification }.whenever(this).build()
        }
        doAnswer { notification }.whenever(notificationFactory)
            .createWorkInProgressNotification(any<WorkDay>(), any<WorkTimeBalance>())

        // Manually launching the activity AFTER the mocks are configured
        scenario = ActivityScenario.launch(DashboardActivity::class.java)
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    @Test
    fun test_activityInView() {
        // Checks if the dashboard layout is visible
        onView(withId(R.id.dashboard_content)).check(matches(isDisplayed()))
    }

    @Test
    fun test_fabIsDisplayedAndClickable() {
        // Checks if the FAB is visible
        onView(withId(R.id.dashboard_enter_fab)).check(matches(isDisplayed()))

        // Clicks the FAB
        onView(withId(R.id.dashboard_enter_fab)).perform(click())

        // Verify that onRegisterNewEvent() was called on the ViewModel by checking
        // if it triggered a call on its dependency, ComeEventUtils.
        verifyBlocking(comeEventUtils) { registerNewEvent() }
    }

    @Test
    fun test_initialStateIsCorrect() {
        // Verify initial visibility
        onView(withId(R.id.dashboard_current_day_empty_events_message)).check(matches(isDisplayed()))
        onView(withId(R.id.dashboard_current_day_events_list)).check(matches(not(isDisplayed())))

        // Verify initial timer values
        onView(withId(R.id.dashboard_remaining_day_time)).check(matches(hasDescendant(withText("8:00:00"))))
        onView(withId(R.id.dashboard_today_work_time)).check(matches(hasDescendant(withText("0:00:00"))))
    }

    @Test
    fun test_uiUpdatesCorrectlyAfterFabClick() {
        // Click the FAB to start work
        onView(withId(R.id.dashboard_enter_fab)).perform(click())

        // ----- AFTER -----
        // Wait for the work to be started
        awaitState(workStateFlow) { state ->
            state?.workDay?.isWorkFinished() == false
        }

        // After clicking, the empty message should disappear and the list should appear with one item
        onView(withId(R.id.dashboard_current_day_empty_events_message)).check(
            matches(
                not(
                    isDisplayed()
                )
            )
        )
        onView(withId(R.id.dashboard_current_day_events_list)).check(matches(isDisplayed()))
        onView(withId(R.id.dashboard_current_day_events_list)).check(matches(withItemCount(1)))

        // One second after clicking, values should change
        Thread.sleep(1000)

        // Verify that timers have started and their values have changed
        onView(withId(R.id.dashboard_remaining_day_time)).check(matches(not(hasDescendant(withText("8:00:00")))))
        onView(withId(R.id.dashboard_today_work_time)).check(matches(not(hasDescendant(withText("0:00:00")))))

        // Verify that the notification factory was called to create the notification.
        // This confirms that the WorkTimeTrackerService was started and is working correctly.
        verify(notificationFactory, timeout(1000).atLeastOnce()).createWorkInProgressNotification(
            any<WorkDay>(),
            any<WorkTimeBalance>()
        )

        // Verify that the end-of-work notification was scheduled by the ViewModel.
        verify(notificationService, timeout(1000)).scheduleEndOfWorkNotification(
            any<WorkDay>(),
            any<WorkTimeBalance>()
        )
    }

    @Test
    fun test_navigationDrawer() {
        // Opens the navigation drawer and checks if it's visible
        onView(withId(R.id.base_drawer_layout)).perform(DrawerActions.open())
        onView(withId(R.id.base_nav_view)).check(matches(isDisplayed()))
    }

    @Test
    fun test_navigationToHistory() {
        onView(withId(R.id.base_drawer_layout)).perform(DrawerActions.open())
        onView(withId(R.id.base_nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_history))
        // Verifies that the HistoryActivity layout is visible
        onView(withId(R.id.history_layout)).check(matches(isDisplayed()))
    }

    @Test
    fun test_navigationToSettings() {
        onView(withId(R.id.base_drawer_layout)).perform(DrawerActions.open())
        onView(withId(R.id.base_nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_settings))
        // Verifies that the SettingsActivity layout is visible
        onView(withId(R.id.settings)).check(matches(isDisplayed()))
    }
}
