package net.wojteksz128.worktimemeasureapp.window.dialog.comeevent

import android.text.format.DateFormat
import android.view.View
import android.widget.NumberPicker
import androidx.annotation.IdRes
import androidx.lifecycle.ViewModelProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.GeneralClickAction
import androidx.test.espresso.action.GeneralLocation
import androidx.test.espresso.action.GeneralSwipeAction
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Swipe
import androidx.test.espresso.action.Tap
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.util.datetime.AmPm
import net.wojteksz128.worktimemeasureapp.util.datetime.convert24To12HourFormat
import net.wojteksz128.worktimemeasureapp.util.launchFragmentInHiltContainer
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import kotlin.math.abs

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class EditComeEventDialogFragmentTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    private val testComeEvent = ComeEvent(
        id = 1,
        startDate = ZonedDateTime.of(
            LocalDateTime.of(2023, 10, 27, 8, 30, 0),
            ZoneId.systemDefault()
        ),
        endDate = ZonedDateTime.of(
            LocalDateTime.of(2023, 10, 27, 16, 45, 0),
            ZoneId.systemDefault()
        ),
        workDayId = 1
    )
    private val is24HourFormat by lazy {
        DateFormat.is24HourFormat(InstrumentationRegistry.getInstrumentation().targetContext)
    }

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun should_displayInitialEventData_when_dialogIsShown() {
        // Arrange
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val expectedStartTime = testComeEvent.startDate.format(timeFormatter)
        val expectedFinishTime = testComeEvent.endDate!!.format(timeFormatter)

        // Act
        launchFragmentInHiltContainer<EditComeEventDialogFragment>(
            preAction = { activity ->
                ViewModelProvider(activity)[SelectedComeEventViewModel::class.java].select(
                    testComeEvent
                )
            }
        )

        // Assert
        onView(
            allOf(
                withId(R.id.time_editor_value),
                isDescendantOfA(withId(R.id.component_time_editor_start))
            )
        )
            .inRoot(isDialog())
            .check(matches(withText(expectedStartTime)))

        onView(
            allOf(
                withId(R.id.time_editor_value),
                isDescendantOfA(withId(R.id.component_time_editor_finish))
            )
        )
            .inRoot(isDialog())
            .check(matches(withText(expectedFinishTime)))
    }

    @Test
    fun should_callListenerWithModifiedEvent_when_positiveButtonIsClicked() {
        // Arrange
        val listener = mock(EditComeEventDialogFragment.EditComeEventDialogListener::class.java)
        val captor = argumentCaptor<ComeEvent>()
        val newHour = 9
        val newMinute = 15

        launchFragmentInHiltContainer<EditComeEventDialogFragment>(
            preAction = { activity ->
                ViewModelProvider(activity)[SelectedComeEventViewModel::class.java].select(
                    testComeEvent
                )
            },
            action = { this.listener = listener }
        )

        // Act
        setTimeOnDateTimePicker(R.id.component_time_editor_start, newHour, newMinute)
        onView(withId(android.R.id.button1)).inRoot(isDialog()).perform(click())

        // Assert
        verify(listener).onAcceptModificationComeEventClick(any(), captor.capture())
        val modifiedEvent = captor.firstValue
        assertEquals(newHour, modifiedEvent.startDate.hour)
        assertEquals(newMinute, modifiedEvent.startDate.minute)
    }

    @Test
    fun should_disablePositiveButton_when_endTimeIsBeforeStartTime() {
        // Arrange
        launchFragmentInHiltContainer<EditComeEventDialogFragment>(
            preAction = { activity ->
                ViewModelProvider(activity)[SelectedComeEventViewModel::class.java].select(
                    testComeEvent
                )
            }
        )

        // Act
        setTimeOnDateTimePicker(R.id.component_time_editor_finish, 7, 0)

        // Assert
        onView(withId(android.R.id.button1)).inRoot(isDialog())
            .check(matches(not(isEnabled())))
    }

    @Test
    fun should_reEnablePositiveButton_when_invalidTimeIsCorrected() {
        // Arrange
        launchFragmentInHiltContainer<EditComeEventDialogFragment>(
            preAction = { activity ->
                ViewModelProvider(activity)[SelectedComeEventViewModel::class.java].select(
                    testComeEvent
                )
            }
        )

        // Act & Assert
        setTimeOnDateTimePicker(R.id.component_time_editor_finish, 7, 0)
        onView(withId(android.R.id.button1)).inRoot(isDialog()).check(matches(not(isEnabled())))

        setTimeOnDateTimePicker(R.id.component_time_editor_finish, 17, 0)
        onView(withId(android.R.id.button1)).inRoot(isDialog()).check(matches(isEnabled()))
    }

    @Test
    fun should_callListener_when_negativeButtonIsClicked() {
        // Arrange
        val listener = mock(EditComeEventDialogFragment.EditComeEventDialogListener::class.java)

        launchFragmentInHiltContainer<EditComeEventDialogFragment>(
            preAction = { activity ->
                ViewModelProvider(activity)[SelectedComeEventViewModel::class.java].select(
                    testComeEvent
                )
            },
            action = { this.listener = listener }
        )

        // Act
        onView(withId(android.R.id.button2)).inRoot(isDialog()).perform(click())

        // Assert
        verify(listener).onRejectModificationComeEventClick(any())
    }

    @Test
    fun should_revertTime_when_dismissingChangeInTimeEditor() {
        // Arrange
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val originalStartTime = testComeEvent.startDate.format(timeFormatter)

        launchFragmentInHiltContainer<EditComeEventDialogFragment>(
            preAction = { activity ->
                ViewModelProvider(activity)[SelectedComeEventViewModel::class.java].select(
                    testComeEvent
                )
            }
        )

        // Act
        onView(
            allOf(
                withId(R.id.time_editor_set_time),
                isDescendantOfA(withId(R.id.component_time_editor_start))
            )
        ).inRoot(isDialog()).perform(click())
        onView(
            allOf(
                withId(R.id.date_time_picker_hour),
                isDescendantOfA(withId(R.id.component_time_editor_start))
            )
        ).inRoot(isDialog()).perform(setNumberOnNumberPicker(10))
        onView(
            allOf(
                withId(R.id.time_editor_dismiss),
                isDescendantOfA(withId(R.id.component_time_editor_start))
            )
        ).inRoot(isDialog()).perform(click())

        // Assert
        onView(
            allOf(
                withId(R.id.time_editor_value),
                isDescendantOfA(withId(R.id.component_time_editor_start))
            )
        )
            .inRoot(isDialog()).check(matches(withText(originalStartTime)))
    }

    @Test
    fun should_returnNullEndDate_when_clearButtonIsClicked() {
        // Arrange
        val listener = mock(EditComeEventDialogFragment.EditComeEventDialogListener::class.java)
        val captor = argumentCaptor<ComeEvent>()

        launchFragmentInHiltContainer<EditComeEventDialogFragment>(
            preAction = { activity ->
                ViewModelProvider(activity)[SelectedComeEventViewModel::class.java].select(
                    testComeEvent
                )
            },
            action = { this.listener = listener }
        )

        // Act
        onView(
            allOf(
                withId(R.id.time_editor_clear_time),
                isDescendantOfA(withId(R.id.component_time_editor_finish))
            )
        ).inRoot(isDialog()).perform(click())
        onView(withId(android.R.id.button1)).inRoot(isDialog()).perform(click())

        // Assert
        verify(listener).onAcceptModificationComeEventClick(any(), captor.capture())
        assertNull(captor.firstValue.endDate)
    }

    @Test
    fun should_disableSaveButNotCancel_when_timeEditorIsInEditMode() {
        // Arrange
        launchFragmentInHiltContainer<EditComeEventDialogFragment>(
            preAction = { activity ->
                ViewModelProvider(activity)[SelectedComeEventViewModel::class.java].select(
                    testComeEvent
                )
            }
        )

        // Act & Assert: Wejdź w tryb edycji
        onView(
            allOf(
                withId(R.id.time_editor_set_time),
                isDescendantOfA(withId(R.id.component_time_editor_start))
            )
        )
            .inRoot(isDialog())
            .perform(click())

        // Przycisk zapisu jest nieaktywny, anulowania jest aktywny
        onView(withId(android.R.id.button1)).inRoot(isDialog()).check(matches(not(isEnabled())))
        onView(withId(android.R.id.button2)).inRoot(isDialog()).check(matches(isEnabled()))

        // Act & Assert: Wyjdź z trybu edycji przez akceptację w komponencie
        onView(
            allOf(
                withId(R.id.time_editor_accept),
                isDescendantOfA(withId(R.id.component_time_editor_start))
            )
        )
            .inRoot(isDialog())
            .perform(click())

        // Oba przyciski są ponownie aktywne
        onView(withId(android.R.id.button1)).inRoot(isDialog()).check(matches(isEnabled()))
        onView(withId(android.R.id.button2)).inRoot(isDialog()).check(matches(isEnabled()))
    }

    // ==============
    // Helper Methods
    // ==============

    private fun setNumberOnNumberPicker(value: Int): ViewAction {
        return object : ViewAction {
            val ROWS_PER_SWIPE = 5

            override fun perform(uiController: UiController, view: View) {
                val numberPicker = view as NumberPicker
                val numberPickerValueRangeSize = numberPicker.maxValue - numberPicker.minValue + 1

                while (numberPicker.value != value) {
                    val normalDelta = abs(value - numberPicker.value)
                    val reverseDelta = numberPickerValueRangeSize - normalDelta

                    val (delta, reverse) =
                        if (normalDelta <= reverseDelta) Pair(normalDelta, false)
                        else Pair(reverseDelta, true)

                    if (delta >= ROWS_PER_SWIPE) {
                        val (directionFrom, directionTo) =
                            if (value > numberPicker.value)
                                if (reverse)
                                    Pair(GeneralLocation.TOP_CENTER, GeneralLocation.BOTTOM_CENTER)
                                else
                                    Pair(GeneralLocation.BOTTOM_CENTER, GeneralLocation.TOP_CENTER)
                            else
                                if (reverse)
                                    Pair(GeneralLocation.BOTTOM_CENTER, GeneralLocation.TOP_CENTER)
                                else
                                    Pair(GeneralLocation.TOP_CENTER, GeneralLocation.BOTTOM_CENTER)

                        GeneralSwipeAction(Swipe.FAST, directionFrom, directionTo, Press.FINGER)
                            .perform(uiController, view)
                    } else {
                        val direction =
                            if (value > numberPicker.value)
                                if (reverse) GeneralLocation.TOP_CENTER else GeneralLocation.BOTTOM_CENTER
                            else
                                if (reverse) GeneralLocation.BOTTOM_CENTER else GeneralLocation.TOP_CENTER
                        GeneralClickAction(Tap.SINGLE, direction, Press.FINGER, 0, 0)
                            .perform(uiController, view)
                    }
                }
            }

            override fun getDescription(): String = "Set value on NumberPicker by tapping"

            override fun getConstraints(): Matcher<View> =
                isAssignableFrom(NumberPicker::class.java)
        }
    }

    private fun setTimeOnDateTimePicker(
        @IdRes editorId: Int,
        hour: Int,
        minute: Int,
        second: Int = 0,
    ) {
        onView(allOf(withId(R.id.time_editor_set_time), isDescendantOfA(withId(editorId))))
            .inRoot(isDialog())
            .perform(click())

        if (is24HourFormat)
            onView(allOf(withId(R.id.date_time_picker_hour), isDescendantOfA(withId(editorId))))
                .inRoot(isDialog())
                .perform(setNumberOnNumberPicker(hour))
        else {
            val (hour12Format, amPm) = convert24To12HourFormat(hour)
            val amPmId = if (amPm == AmPm.AM) R.id.date_time_picker_am else R.id.date_time_picker_pm
            onView(allOf(withId(R.id.date_time_picker_hour), isDescendantOfA(withId(editorId))))
                .inRoot(isDialog())
                .perform(setNumberOnNumberPicker(hour12Format))
            onView(allOf(withId(amPmId), isDescendantOfA(withId(editorId))))
                .inRoot(isDialog())
                .perform(click())
        }
        onView(allOf(withId(R.id.date_time_picker_minute), isDescendantOfA(withId(editorId))))
            .inRoot(isDialog())
            .perform(setNumberOnNumberPicker(minute))
        onView(allOf(withId(R.id.date_time_picker_second), isDescendantOfA(withId(editorId))))
            .inRoot(isDialog())
            .perform(setNumberOnNumberPicker(second))

        onView(allOf(withId(R.id.time_editor_accept), isDescendantOfA(withId(editorId))))
            .inRoot(isDialog())
            .perform(click())
    }
}
