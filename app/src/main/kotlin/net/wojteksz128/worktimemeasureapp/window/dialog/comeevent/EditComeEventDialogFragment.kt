package net.wojteksz128.worktimemeasureapp.window.dialog.comeevent

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.databinding.DialogComeEventEditBinding
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import javax.inject.Inject

@AndroidEntryPoint
class EditComeEventDialogFragment : DialogFragment() {
    private val editDialogViewModel: EditComeEventDialogViewModel by viewModels()
    private val selectedComeEventViewModel: SelectedComeEventViewModel by activityViewModels()

    @Inject
    lateinit var dateTimeUtils: DateTimeUtils

    internal lateinit var listener: EditComeEventDialogListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext()).apply {
            val dialogBinding = DialogComeEventEditBinding.inflate(layoutInflater, null, false)
                .apply {
                    this.lifecycleOwner = this@EditComeEventDialogFragment
                    this.dateTimeUtils = this@EditComeEventDialogFragment.dateTimeUtils
                    this.viewModel = this@EditComeEventDialogFragment.editDialogViewModel
                }
            setView(dialogBinding.root)
            setTitle(R.string.edit_come_event_dialog_title)
            setPositiveButton(R.string.edit_come_event_dialog_action_edit) { _, _ ->
                listener.onModifyComeEventClick(
                    this@EditComeEventDialogFragment,
                    editDialogViewModel.prepareModified()
                )
            }
            setNegativeButton(R.string.edit_come_event_dialog_action_cancel) { _, _ ->
                listener.onRejectModificationComeEventClick(this@EditComeEventDialogFragment)
            }
        }.create().apply {
            editDialogViewModel.positiveButtonEnabled.observe(this@EditComeEventDialogFragment) { buttonEnabled ->
                getButton(AlertDialog.BUTTON_POSITIVE)?.let {
                    it.isEnabled = buttonEnabled
                }
            }
            selectedComeEventViewModel.selected.observe(this@EditComeEventDialogFragment) { comeEvent ->
                editDialogViewModel.fill(comeEvent)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = parentFragment as EditComeEventDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("${parentFragment.toString()} must implement EditComeEventDialogListener")
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        listener.onEditComeEventDialogDismiss(this@EditComeEventDialogFragment)
        super.onDismiss(dialog)
    }

    interface EditComeEventDialogListener {
        fun onModifyComeEventClick(dialog: DialogFragment, modifiedComeEvent: ComeEvent)
        fun onRejectModificationComeEventClick(dialog: DialogFragment)
        fun onEditComeEventDialogDismiss(dialog: DialogFragment)
    }
}