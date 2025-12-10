package net.wojteksz128.worktimemeasureapp.window.dialog.comeevent

import androidx.lifecycle.ViewModelProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.util.launchFragmentInHiltContainer
import net.wojteksz128.worktimemeasureapp.window.dialog.comeevent.EditComeEventDialogFragment.EditComeEventDialogListener
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

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun should_displayInitialEventData_when_dialogIsShown() {
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val expectedStartTime = testComeEvent.startDate.format(timeFormatter)
        val expectedFinishTime = testComeEvent.endDate!!.format(timeFormatter)

        initEditComeEventDialogFragment()

        editComeEventDialog {
            verifyStartTime(expectedStartTime)
            verifyFinishTime(expectedFinishTime)
        }
    }

    @Test
    fun should_callListenerWithModifiedEvent_when_positiveButtonIsClicked() {
        val listener = mock(EditComeEventDialogListener::class.java)
        val captor = argumentCaptor<ComeEvent>()
        val newHour = 9
        val newMinute = 15

        initEditComeEventDialogFragment(listener)

        editComeEventDialog {
            setStartTime(newHour, newMinute)
            clickOk()
        }

        verify(listener).onAcceptModificationComeEventClick(any(), captor.capture())
        val modifiedEvent = captor.firstValue
        assertEquals(newHour, modifiedEvent.startDate.hour)
        assertEquals(newMinute, modifiedEvent.startDate.minute)
    }

    @Test
    fun should_disablePositiveButton_when_endTimeIsBeforeStartTime() {
        initEditComeEventDialogFragment()

        editComeEventDialog {
            setFinishTime(7, 0)
            verifyOkButtonIsDisabled()
        }
    }

    @Test
    fun should_reEnablePositiveButton_when_invalidTimeIsCorrected() {
        initEditComeEventDialogFragment()

        editComeEventDialog {
            setFinishTime(7, 0)
            verifyOkButtonIsDisabled()
            setFinishTime(17, 0)
            verifyOkButtonIsEnabled()
        }
    }

    @Test
    fun should_callListener_when_negativeButtonIsClicked() {
        val listener = mock(EditComeEventDialogListener::class.java)

        initEditComeEventDialogFragment(listener)

        editComeEventDialog {
            clickCancel()
        }

        verify(listener).onRejectModificationComeEventClick(any())
    }

    @Test
    fun should_revertTime_when_dismissingChangeInTimeEditor() {
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val originalStartTime = testComeEvent.startDate.format(timeFormatter)

        initEditComeEventDialogFragment()

        editComeEventDialog {
            openStartTimeEditor {
                setHour(10)
                clickDismiss()
            }
            verifyStartTime(originalStartTime)
        }
    }

    @Test
    fun should_returnNullEndDate_when_clearButtonIsClicked() {
        val listener = mock(EditComeEventDialogListener::class.java)
        val captor = argumentCaptor<ComeEvent>()

        initEditComeEventDialogFragment(listener)

        editComeEventDialog {
            clearFinishTime()
            clickOk()
        }

        verify(listener).onAcceptModificationComeEventClick(any(), captor.capture())
        assertNull(captor.firstValue.endDate)
    }

    private fun initEditComeEventDialogFragment(listener: EditComeEventDialogListener? = null) {
        launchFragmentInHiltContainer<EditComeEventDialogFragment>(
            preAction = { activity ->
                ViewModelProvider(activity)[SelectedComeEventViewModel::class.java].select(
                    testComeEvent
                )
            },
            action = listener?.let { { this.listener = it } } ?: {}
        )
    }

}
