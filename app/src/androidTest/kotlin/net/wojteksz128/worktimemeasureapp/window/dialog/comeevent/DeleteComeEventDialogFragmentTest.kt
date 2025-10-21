package net.wojteksz128.worktimemeasureapp.window.dialog.comeevent

import androidx.lifecycle.ViewModelProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import net.wojteksz128.worktimemeasureapp.util.launchFragmentInHiltContainer
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class DeleteComeEventDialogFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var dateTimeUtils: DateTimeUtils

    private val testComeEvent = ComeEvent(
        id = 1,
        startDate = ZonedDateTime.of(
            LocalDateTime.of(2023, 10, 27, 8, 0, 0),
            ZoneId.systemDefault()
        ),
        endDate = ZonedDateTime.of(
            LocalDateTime.of(2023, 10, 27, 16, 0, 0),
            ZoneId.systemDefault()
        ),
        workDayId = 1
    )

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun shouldDisplayCorrectTitleAndMessage() {
        launchFragmentInHiltContainer<DeleteComeEventDialogFragment>(
            preAction = { activity ->
                val viewModel = ViewModelProvider(activity)[SelectedComeEventViewModel::class.java]
                viewModel.select(testComeEvent)
            }
        )

        onView(withText(R.string.delete_come_event_dialog_title))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val dateFormat = context.getString(R.string.history_day_event_time_short_format)
        val formattedStartDate = dateTimeUtils.formatDate(dateFormat, testComeEvent.startDate)
        val formattedEndDate = dateTimeUtils.formatDate(dateFormat, testComeEvent.endDate)

        val expectedMessage = context.getString(
            R.string.delete_come_event_dialog_delete_message,
            formattedStartDate,
            formattedEndDate
        )
        onView(withText(expectedMessage))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
    }

    @Test
    fun shouldCallOnAcceptDeletionComeEventClickWhenPositiveButtonIsClicked() {
        val listener = mock(DeleteComeEventDialogFragment.DeleteComeEventDialogListener::class.java)

        launchFragmentInHiltContainer<DeleteComeEventDialogFragment>(
            preAction = { activity ->
                val viewModel = ViewModelProvider(activity)[SelectedComeEventViewModel::class.java]
                viewModel.select(testComeEvent)
            },
            action = {
                this.listener = listener
            }
        )

        onView(withText(R.string.delete_come_event_dialog_action_delete))
            .inRoot(isDialog())
            .perform(click())

        verify(listener).onAcceptDeletionComeEventClick(any())
        onView(withText(R.string.delete_come_event_dialog_title)).check(doesNotExist())
    }

    @Test
    fun shouldCallOnRejectDeletionComeEventClickWhenNegativeButtonIsClicked() {
        val listener = mock(DeleteComeEventDialogFragment.DeleteComeEventDialogListener::class.java)

        launchFragmentInHiltContainer<DeleteComeEventDialogFragment>(
            preAction = { activity ->
                val viewModel = ViewModelProvider(activity)[SelectedComeEventViewModel::class.java]
                viewModel.select(testComeEvent)
            },
            action = {
                this.listener = listener
            }
        )

        onView(withText(R.string.delete_come_event_dialog_action_cancel))
            .inRoot(isDialog())
            .perform(click())

        verify(listener).onRejectDeletionComeEventClick(any())
    }
}