package net.wojteksz128.worktimemeasureapp.window.dialog.backup

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.backup.BackupService
import net.wojteksz128.worktimemeasureapp.window.dialog.DialogFragmentWithListener
import net.wojteksz128.worktimemeasureapp.window.dialog.backup.ImportStrategyDialogFragment.ImportStrategyDialogListener

class ImportStrategyDialogFragment : DialogFragmentWithListener<ImportStrategyDialogListener>() {

    private var selectedIndex = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val strategies = arrayOf(
            getString(R.string.settings_backup_import_strategy_merge),
            getString(R.string.settings_backup_import_strategy_replace),
            getString(R.string.settings_backup_import_strategy_skip),
        )
        return AlertDialog.Builder(requireContext()).apply {
            setTitle(R.string.settings_backup_import_strategy_title)
            setSingleChoiceItems(strategies, selectedIndex) { _: DialogInterface, which: Int ->
                selectedIndex = which
            }
            setNegativeButton(R.string.settings_backup_import_action_import) { _: DialogInterface, _: Int ->
                val strategy = when (selectedIndex) {
                    1 -> BackupService.ImportStrategy.REPLACE
                    2 -> BackupService.ImportStrategy.SKIP
                    else -> BackupService.ImportStrategy.MERGE
                }
                listener?.onImportStrategySelected(this@ImportStrategyDialogFragment, strategy)
            }
            setPositiveButton(android.R.string.cancel) { _: DialogInterface, _: Int ->
                listener?.onImportStrategyCancelled(this@ImportStrategyDialogFragment)
            }
        }.create()
    }

    override fun onCancel(dialog: DialogInterface) {
        listener?.onImportStrategyCancelled(this@ImportStrategyDialogFragment)
        super.onCancel(dialog)
    }

    interface ImportStrategyDialogListener {
        fun onImportStrategySelected(
            dialog: DialogFragment,
            strategy: BackupService.ImportStrategy,
        ) {
        }

        fun onImportStrategyCancelled(dialog: DialogFragment) {}
    }
}

