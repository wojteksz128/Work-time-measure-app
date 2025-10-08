package net.wojteksz128.worktimemeasureapp.window.dialog.dayOff

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import dagger.hilt.android.AndroidEntryPoint
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.WorkTimeMeasureApp
import net.wojteksz128.worktimemeasureapp.model.fieldType.DayType
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import javax.inject.Inject

@AndroidEntryPoint
class TodayDayOffInformationDialogFragment(private val dayType: DayType) : DialogFragment() {

    @Inject
    lateinit var dateTimeUtils: DateTimeUtils

    @Inject
    lateinit var dateTimeProvider: DateTimeProvider

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext()).apply {
            setTitle(R.string.today_day_off_information_dialog_title)
            val formattedMessage = getMessage(dayType)
            setMessage(formattedMessage)
            setPositiveButton(R.string.today_day_off_information_dialog_action_yes) { _, _ ->
                val requireActivity = requireActivity()
                val workTimeMeasureApp = requireActivity.application as WorkTimeMeasureApp
                workTimeMeasureApp.closeApp(requireActivity)
            }
            setNegativeButton(R.string.today_day_off_information_dialog_action_no) { dialog, _ ->
                dialog.cancel()
            }
        }.create()
    }

    private fun getMessage(dayType: DayType) = if (dayType.dayOffInfo == null) getString(
        R.string.today_day_off_information_dialog_weekend_message, dateTimeUtils.formatDate(
            getString(R.string.today_day_off_information_dialog_message_date_format),
            dateTimeProvider.currentTime
        )
    )
    else getString(
        R.string.today_day_off_information_dialog_dayOff_message, dateTimeUtils.formatDate(
            getString(R.string.today_day_off_information_dialog_message_date_format),
            dateTimeProvider.currentTime
        ), dayType.dayOffInfo.name
    )

    override fun show(manager: FragmentManager, tag: String?) {
        openOnce { super.show(manager, tag) }
    }

    override fun show(transaction: FragmentTransaction, tag: String?): Int {
        return openOnce { super.show(transaction, tag) }
    }

    override fun showNow(manager: FragmentManager, tag: String?) {
        openOnce { super.showNow(manager, tag) }
    }

    companion object : ClassTagAware {
        private var dayOffDialogShowed: Boolean = false

        private fun <T : Any> openOnce(function: () -> T): T {
            if (dayOffDialogShowed) {
                Log.i(classTag, "Day off dialog was shown earlier")
                @Suppress("UNCHECKED_CAST")
                return Unit as T
            }
            dayOffDialogShowed = true
            return function()
        }
    }
}