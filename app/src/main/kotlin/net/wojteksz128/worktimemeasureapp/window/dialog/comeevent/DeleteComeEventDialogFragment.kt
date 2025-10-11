package net.wojteksz128.worktimemeasureapp.window.dialog.comeevent

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import net.wojteksz128.worktimemeasureapp.util.datetime.isTheSameDay
import net.wojteksz128.worktimemeasureapp.window.dialog.DialogFragmentWithListener
import net.wojteksz128.worktimemeasureapp.window.dialog.comeevent.DeleteComeEventDialogFragment.DeleteComeEventDialogListener
import javax.inject.Inject

@AndroidEntryPoint
class DeleteComeEventDialogFragment : DialogFragmentWithListener<DeleteComeEventDialogListener>() {
    private val selectedComeEventViewModel: SelectedComeEventViewModel by activityViewModels()

    @Inject
    lateinit var dateTimeUtils: DateTimeUtils

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext()).apply {
            setTitle(R.string.delete_come_event_dialog_title)
            setMessage(prepareDeleteMessage())
            setPositiveButton(R.string.delete_come_event_dialog_action_delete) { dialog, _ ->
                listener?.onAcceptDeletionComeEventClick(this@DeleteComeEventDialogFragment)
                dialog.dismiss()
            }
            setNegativeButton(R.string.delete_come_event_dialog_action_cancel) { dialog, _ ->
                listener?.onRejectDeletionComeEventClick(this@DeleteComeEventDialogFragment)
                dialog.dismiss()
            }
        }.create()
    }

    private fun prepareDeleteMessage(): String {
        return selectedComeEventViewModel.selected.value?.let {
            val dateFormat =
                if (it.startDate.isTheSameDay(it.endDate))
                    R.string.history_day_event_time_short_format
                else
                    R.string.history_day_event_time_long_format
            getString(
                R.string.delete_come_event_dialog_delete_message,
                dateTimeUtils.formatDate(
                    getString(dateFormat),
                    it.startDate
                ),
                dateTimeUtils.formatDate(
                    getString(dateFormat),
                    it.endDate
                )
            )
        } ?: throw IllegalStateException("Come event is not selected")
    }

    override fun onDismiss(dialog: DialogInterface) {
        listener?.onDeleteComeEventDialogDismiss(this@DeleteComeEventDialogFragment)
        super.onDismiss(dialog)
    }

    interface DeleteComeEventDialogListener {
        fun onAcceptDeletionComeEventClick(dialog: DialogFragment) {}
        fun onRejectDeletionComeEventClick(dialog: DialogFragment) {}
        fun onDeleteComeEventDialogDismiss(dialog: DialogFragment) {}
    }
}