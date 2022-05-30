package net.wojteksz128.worktimemeasureapp.window.dialog.dayOff

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import dagger.hilt.android.AndroidEntryPoint
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.WorkTimeMeasureApp
import net.wojteksz128.worktimemeasureapp.model.fieldType.DayType
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class TodayDayOffInformationDialogFragment(private val dayType: DayType) : DialogFragment() {

    @Inject
    lateinit var dateTimeUtils: DateTimeUtils

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext()).apply {
            setTitle(R.string.today_day_off_information_dialog_title)
            val formattedMessage = getMessage(dayType)
            setMessage(formattedMessage)
            setPositiveButton(R.string.today_day_off_information_dialog_action_yes) { _, _ ->
                val workTimeMeasureApp = activity!!.application as WorkTimeMeasureApp
                workTimeMeasureApp.closeApp(activity!!)
            }
            setNegativeButton(R.string.today_day_off_information_dialog_action_no) { dialog, _ ->
                dialog.cancel()
            }
        }.create()
    }

    private fun getMessage(dayType: DayType) = if (dayType.dayOffInfo == null) getString(
        R.string.today_day_off_information_dialog_weekend_message, dateTimeUtils.formatDate(
            getString(R.string.today_day_off_information_dialog_message_date_format),
            Date()
        )
    )
    else getString(
        R.string.today_day_off_information_dialog_dayOff_message, dateTimeUtils.formatDate(
            getString(R.string.today_day_off_information_dialog_message_date_format),
            Date()
        ), dayType.dayOffInfo.name
    )
}