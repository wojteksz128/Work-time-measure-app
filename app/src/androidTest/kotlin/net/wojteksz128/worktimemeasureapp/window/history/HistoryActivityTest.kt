package net.wojteksz128.worktimemeasureapp.window.history

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.MutableStateFlow
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class HistoryActivityTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var workDayFlow: MutableStateFlow<WorkDay?>

    @Inject
    lateinit var dateTimeProvider: DateTimeProvider

    private var activityScenario: ActivityScenario<HistoryActivity>? = null

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @After
    fun tearDown() {
        activityScenario?.close()
    }

    @Test
    fun whenActivityLaunched_shouldDisplayWorkDaysHistoryFragment() {
        workDayFlow.value = WorkDay(dateTimeProvider.currentDate).copy(id = 1L)
        activityScenario = ActivityScenario.launch(HistoryActivity::class.java)

        onView(withId(R.id.work_days_history_rv)).check(matches(isDisplayed()))
    }
}