package net.wojteksz128.worktimemeasureapp.window.settings

import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.backup.BackupService
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class BackupFragment : BasePreferenceFragment(R.xml.backup_preferences) {

    @Inject
    lateinit var backupService: BackupService

    private val exportBackupLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            performExportBackup(uri)
        }
    }

    private val importBackupLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            performImportBackup(uri)
        }
    }

    override fun onPreferencesInit() {
        super.onPreferencesInit()

        findPreference<Preference>("backup_export")?.setOnPreferenceClickListener {
            showExportDialog()
            true
        }

        findPreference<Preference>("backup_import")?.setOnPreferenceClickListener {
            importBackupLauncher.launch("application/json")
            true
        }

        findPreference<Preference>("backup_share")?.setOnPreferenceClickListener {
            showShareDialog()
            true
        }
    }

    private fun showExportDialog() {
        val timestamp = System.currentTimeMillis()
        val fileName = "wtm_backup_${timestamp}.wtm_backup.json"
        exportBackupLauncher.launch(fileName)
    }

    private fun performExportBackup(uri: Uri) {
        lifecycleScope.launch {
            try {
                when (val result = backupService.exportBackup()) {
                    is BackupService.Result.Success -> {
                        val inputStream = result.file.inputStream()
                        val outputStream = requireContext().contentResolver.openOutputStream(uri)
                        if (outputStream != null) {
                            inputStream.copyTo(outputStream)
                            outputStream.close()
                            inputStream.close()
                            showMessage(getString(R.string.settings_backup_export_success))
                        }
                    }

                    is BackupService.Result.Error -> {
                        showMessage(getString(R.string.settings_backup_export_error) + ": " + result.exception.message)
                    }
                }
            } catch (e: Exception) {
                showMessage(getString(R.string.settings_backup_export_error) + ": " + e.message)
            }
        }
    }

    private fun performImportBackup(uri: Uri) {
        lifecycleScope.launch {
            try {
                val tempFile =
                    File.createTempFile("backup_import", ".json", requireContext().cacheDir)
                val inputStream = requireContext().contentResolver.openInputStream(uri)
                val outputStream = tempFile.outputStream()
                if (inputStream != null) {
                    inputStream.copyTo(outputStream)
                    outputStream.close()
                    inputStream.close()

                    val isEmpty = backupService.isDatabaseEmpty()
                    if (isEmpty) {
                        doImport(tempFile, BackupService.ImportStrategy.MERGE)
                    } else {
                        showImportStrategyDialog(tempFile)
                    }
                }
            } catch (e: Exception) {
                showMessage(getString(R.string.settings_backup_import_error) + ": " + e.message)
            }
        }
    }

    private fun showImportStrategyDialog(tempFile: File) {
        val strategies = arrayOf(
            getString(R.string.settings_backup_import_strategy_merge),
            getString(R.string.settings_backup_import_strategy_replace),
            getString(R.string.settings_backup_import_strategy_skip),
        )
        var selectedIndex = 0
        AlertDialog.Builder(requireContext()).apply {
            setTitle(R.string.settings_backup_import_strategy_title)
            setSingleChoiceItems(
                strategies,
                selectedIndex
            ) { _: android.content.DialogInterface, which: Int ->
                selectedIndex = which
            }
            setPositiveButton(R.string.settings_backup_import_action_import) { _: android.content.DialogInterface, _: Int ->
                val strategy = when (selectedIndex) {
                    1 -> BackupService.ImportStrategy.REPLACE
                    2 -> BackupService.ImportStrategy.SKIP
                    else -> BackupService.ImportStrategy.MERGE
                }
                lifecycleScope.launch { doImport(tempFile, strategy) }
            }
            setNegativeButton(android.R.string.cancel) { _: android.content.DialogInterface, _: Int ->
                tempFile.delete()
            }
            setOnCancelListener { tempFile.delete() }
        }.show()
    }

    private suspend fun doImport(tempFile: File, strategy: BackupService.ImportStrategy) {
        when (val result = backupService.importBackup(tempFile, strategy)) {
            is BackupService.Result.Success -> {
                showMessage(getString(R.string.settings_backup_import_success))
                tempFile.delete()
            }

            is BackupService.Result.Error -> {
                showMessage(getString(R.string.settings_backup_import_error) + ": " + result.exception.message)
                tempFile.delete()
            }
        }
    }

    private fun showShareDialog() {
        lifecycleScope.launch {
            try {
                when (val result = backupService.exportBackup()) {
                    is BackupService.Result.Success -> {
                        val backupUri = FileProvider.getUriForFile(
                            requireContext(),
                            "net.wojteksz128.worktimemeasureapp.provider",
                            result.file
                        )

                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_STREAM, backupUri)
                            type = "application/json"
                        }
                        startActivity(
                            Intent.createChooser(
                                shareIntent,
                                getString(R.string.settings_backup_share)
                            )
                        )
                    }

                    is BackupService.Result.Error -> {
                        showMessage(getString(R.string.settings_backup_share_error) + ": " + result.exception.message)
                    }
                }
            } catch (e: Exception) {
                showMessage(getString(R.string.settings_backup_share_error) + ": " + e.message)
            }
        }
    }

    private fun showMessage(message: String) {
        Snackbar.make(requireContext(), requireView(), message, Snackbar.LENGTH_LONG).show()
    }
}
