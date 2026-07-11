package net.wojteksz128.worktimemeasureapp.window.dashboard

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.di.TestRepositoryModule.DUMMY_ID
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.model.WorkState
import net.wojteksz128.worktimemeasureapp.model.fieldType.DayType
import net.wojteksz128.worktimemeasureapp.notification.worktime.WorkTimeInProgressNotification
import net.wojteksz128.worktimemeasureapp.notification.worktime.WorkTimeNotificationFactory
import net.wojteksz128.worktimemeasureapp.notification.worktime.WorkTimeNotificationService
import net.wojteksz128.worktimemeasureapp.service.DayOffService
import net.wojteksz128.worktimemeasureapp.settings.InitialSettingsPreparer
import net.wojteksz128.worktimemeasureapp.util.FakeDateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.awaitState
import net.wojteksz128.worktimemeasureapp.util.comeevent.ComeEventUtils
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.fixtures.aComeEvent
import net.wojteksz128.worktimemeasureapp.util.fixtures.aWorkDay
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.timeout
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyBlocking
import org.threeten.bp.Duration
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
    lateinit var comeEventUtils: ComeEventUtils

    @Inject
    lateinit var initialSettingsPreparer: InitialSettingsPreparer

    @Inject
    lateinit var dateTimeProvider: DateTimeProvider

    @Inject
    lateinit var workStateFlow: StateFlow<@JvmSuppressWildcards WorkState>

    @Inject
    lateinit var workDayFlow: MutableStateFlow<WorkDay?>

    private lateinit var scenario: ActivityScenario<DashboardActivity>

    @Before
    fun setup() {
        hiltRule.inject()

        initialSettingsPreparer.initSettings()

        dayOffService.stub {
            onBlocking { getDayType(dateTimeProvider.currentTime) } doReturn DayType.WorkDay
            onBlocking { getDayType(dateTimeProvider.currentDate) } doReturn DayType.WorkDay
        }

        setupNotificationMocks()

        // Manually launching the activity AFTER the mocks are configured
        scenario = ActivityScenario.launch(DashboardActivity::class.java)
    }

    private fun setupNotificationMocks() {
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
        val notification = mock<WorkTimeInProgressNotification>().stub {
            // Configure the mock to return the REAL notification object
            on { build() } doReturn realNotification
        }
        notificationFactory.stub {
            on { createWorkInProgressNotification(any()) } doReturn notification
        }
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    @Test
    fun test_activityInView() {
        dashboard {
            this.verifyIsDisplayed()
        }
    }

    @Test
    fun test_fabIsDisplayedAndClickable() {
        dashboard {
            verifyFabIsDisplayed()
            clickFab()
        }

        verifyBlocking(comeEventUtils) { registerNewEvent() }
    }

    @Test
    fun test_initialStateIsCorrect() {
        dashboard {
            verifyInitialState()
        }
    }

    @Test
    fun test_uiUpdatesCorrectlyAfterFabClick() = runBlocking {
        val expectedMessage = getExpectedString(R.string.dashboard_snackbar_info_income_registered)

        startWorkDay()

        dashboard {
            verifyWorkStartedState()
            verifySnackbarIsShown(expectedMessage)
        }

        verify(notificationFactory, timeout(1000).atLeastOnce())
            .createWorkInProgressNotification(any<WorkState.InProgress>())

        verify(notificationService, timeout(1000))
            .scheduleEndOfWorkNotification(any<WorkState.InProgress>())

        (dateTimeProvider as FakeDateTimeProvider).advanceTimeBy(Duration.ofSeconds(5))
        Thread.sleep(1000)

        dashboard {
            verifyTimersHaveStarted()
        }

        return@runBlocking
    }

    private fun getExpectedString(@Suppress("SameParameterValue") stringId: Int): String {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val expectedMessage = context.getString(stringId)
        return expectedMessage
    }

    @Test
    fun test_swipeRightOnComeEvent_opensDeleteDialog() {
        startWorkDay()

        dashboard {
            eventsRecyclerView {
                swipeRightOnEvent(0) {
                    verifyIsDisplayed()
                }
            }
        }
    }

    @Test
    fun test_swipeLeftOnComeEvent_opensEditDialog() {
        startWorkDay()

        dashboard {
            eventsRecyclerView {
                swipeLeftOnEvent(0) {
                    verifyIsDisplayed()
                }
            }
        }
    }

    @Test
    fun test_largeNumberOfComeEvents_isDisplayedCorrectly() {
        fillWithEvents(100L)

        dashboard {
            verifyEventListCount(100)
            scrollToEvent(99)
        }
    }

    private fun fillWithEvents(@Suppress("SameParameterValue") numberOfEvents: Long) {
        workDayFlow.value = aWorkDay(id = DUMMY_ID) {
            date = dateTimeProvider.currentDate
            events(
                (1L..numberOfEvents).reversed().map { i ->
                    aComeEvent(id = i) {
                        workDayId = DUMMY_ID
                        startDate = dateTimeProvider.currentTime.minusSeconds(2 * i)
                        endDate = dateTimeProvider.currentTime.minusSeconds(2 * i + 1)
                    }
                }
            )
        }

        // Wait for the RecyclerView to update
        Thread.sleep(1000)
    }

    /**
     * Helper function to start a work day by clicking the FAB and waiting for the state to update.
     */
    private fun startWorkDay() {
        dashboard {
            clickFab()
        }

        // Wait for the work to be started
        awaitState(workStateFlow) { it is WorkState.InProgress }
    }
}
