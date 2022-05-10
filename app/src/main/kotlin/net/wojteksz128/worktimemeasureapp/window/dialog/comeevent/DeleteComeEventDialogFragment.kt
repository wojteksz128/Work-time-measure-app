package net.wojteksz128.worktimemeasureapp.window.dialog.comeevent

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import javax.inject.Inject

@AndroidEntryPoint
class DeleteComeEventDialogFragment : DialogFragment() {
    private val selectedComeEventViewModel: SelectedComeEventViewModel by activityViewModels()

    @Inject
    lateinit var dateTimeUtils: DateTimeUtils

    internal lateinit var listener: DeleteComeEventDialogListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext()).apply {
            setTitle(R.string.delete_come_event_dialog_title)
            setMessage(prepareDeleteMessage())
            setPositiveButton(R.string.delete_come_event_dialog_action_delete) { _, _ ->
                listener.onAcceptDeletionComeEventClick(this@DeleteComeEventDialogFragment)
            }
            setNegativeButton(R.string.delete_come_event_dialog_action_cancel) { _, _ ->
                listener.onRejectDeletionComeEventClick(this@DeleteComeEventDialogFragment)
            }
        }.create()
    }

    private fun prepareDeleteMessage(): String {
        return selectedComeEventViewModel.selected.value?.let {
            getString(
                R.string.delete_come_event_dialog_delete_message,
                dateTimeUtils.formatDate(
                    getString(R.string.history_day_event_time_format),
                    it.startDate
                ),
                dateTimeUtils.formatDate(
                    getString(R.string.history_day_event_time_format),
                    it.endDate
                )
            )
        } ?: throw IllegalStateException("Come event is not selected")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = parentFragment as DeleteComeEventDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("${parentFragment.toString()} must implement DeleteComeEventDialogListener")
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        listener.onDeleteComeEventDialogDismiss(this@DeleteComeEventDialogFragment)
        super.onDismiss(dialog)
    }

    interface DeleteComeEventDialogListener {
        fun onAcceptDeletionComeEventClick(dialog: DialogFragment)
        fun onRejectDeletionComeEventClick(dialog: DialogFragment)
        fun onDeleteComeEventDialogDismiss(dialog: DialogFragment)
    }
}