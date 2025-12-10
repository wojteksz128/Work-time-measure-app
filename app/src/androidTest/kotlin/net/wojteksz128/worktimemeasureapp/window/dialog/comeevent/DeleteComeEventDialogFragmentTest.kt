package net.wojteksz128.worktimemeasureapp.window.dialog.comeevent

import androidx.lifecycle.ViewModelProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import net.wojteksz128.worktimemeasureapp.util.launchFragmentInHiltContainer
import net.wojteksz128.worktimemeasureapp.window.dialog.comeevent.DeleteComeEventDialogFragment.DeleteComeEventDialogListener
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
        initDeleteComeEventDialogFragment()

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val dateFormat = context.getString(R.string.history_day_event_time_short_format)
        val formattedStartDate = dateTimeUtils.formatDate(dateFormat, testComeEvent.startDate)
        val formattedEndDate = dateTimeUtils.formatDate(dateFormat, testComeEvent.endDate)
        val expectedMessage = context.getString(
            R.string.delete_come_event_dialog_delete_message,
            formattedStartDate,
            formattedEndDate
        )

        deleteComeEventDialog {
            verifyIsDisplayed()
            verifyMessage(expectedMessage)
        }
    }

    @Test
    fun shouldCallOnAcceptDeletionComeEventClickWhenPositiveButtonIsClicked() {
        val listener = mock(DeleteComeEventDialogListener::class.java)
        initDeleteComeEventDialogFragment(listener)

        deleteComeEventDialog {
            clickDelete()
            verifyDeleteDialogIsDismissed()
        }

        verify(listener).onAcceptDeletionComeEventClick(any())
    }

    @Test
    fun shouldCallOnRejectDeletionComeEventClickWhenNegativeButtonIsClicked() {
        val listener = mock(DeleteComeEventDialogListener::class.java)
        initDeleteComeEventDialogFragment(listener)

        deleteComeEventDialog {
            clickCancel()
        }

        verify(listener).onRejectDeletionComeEventClick(any())
    }

    private fun initDeleteComeEventDialogFragment(listener: DeleteComeEventDialogListener? = null) {
        launchFragmentInHiltContainer<DeleteComeEventDialogFragment>(
            preAction = { activity ->
                val viewModel = ViewModelProvider(activity)[SelectedComeEventViewModel::class.java]
                viewModel.select(testComeEvent)
            },
            action = listener?.let { { this.listener = it } } ?: {}
        )
    }
}
