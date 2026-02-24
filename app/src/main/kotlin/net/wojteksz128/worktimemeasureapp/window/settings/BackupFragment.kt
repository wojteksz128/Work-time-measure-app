package net.wojteksz128.worktimemeasureapp.window.settings

import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.backup.BackupService
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import net.wojteksz128.worktimemeasureapp.window.dialog.backup.ImportStrategyDialogFragment
import net.wojteksz128.worktimemeasureapp.window.dialog.backup.ImportStrategyDialogFragment.ImportStrategyDialogListener
import net.wojteksz128.worktimemeasureapp.window.dialog.showDialogWithListener
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class BackupFragment : BasePreferenceFragment(R.xml.backup_preferences), ClassTagAware {

    @Inject
    lateinit var backupService: BackupService

    private var pendingImportFile: File? = null

    private val exportBackupLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) performExportBackup(uri)
    }

    private val importBackupLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) performImportBackup(uri)
    }

    override fun onPreferencesInit() {
        findPreference<Preference>(getString(R.string.settings_key_backup_export))
            ?.setOnPreferenceClickListener {
                val fileName = "wtm_backup_${System.currentTimeMillis()}.wtm_backup.json"
                exportBackupLauncher.launch(fileName)
                true
            }

        findPreference<Preference>(getString(R.string.settings_key_backup_import))
            ?.setOnPreferenceClickListener {
                importBackupLauncher.launch("application/json")
                true
            }

        findPreference<Preference>(getString(R.string.settings_key_backup_share))
            ?.setOnPreferenceClickListener {
                performShareBackup()
                true
            }
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
                    is BackupService.Result.Error ->
                        showMessage(getString(R.string.settings_backup_export_error) + ": " + result.exception.message)
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
                requireContext().contentResolver.openInputStream(uri)?.use { input ->
                    tempFile.outputStream().use { output -> input.copyTo(output) }
                }
                if (backupService.isDatabaseEmpty()) {
                    doImport(tempFile, BackupService.ImportStrategy.MERGE)
                } else {
                    pendingImportFile = tempFile
                    showDialogWithListener(
                        ImportStrategyDialogFragment::class.java,
                        parentFragmentManager,
                        ImportStrategyListener()
                    )
                }
            } catch (e: Exception) {
                showMessage(getString(R.string.settings_backup_import_error) + ": " + e.message)
            }
        }
    }

    private suspend fun doImport(tempFile: File, strategy: BackupService.ImportStrategy) {
        when (val result = backupService.importBackup(tempFile, strategy)) {
            is BackupService.Result.Success ->
                showMessage(getString(R.string.settings_backup_import_success))

            is BackupService.Result.Error ->
                showMessage(getString(R.string.settings_backup_import_error) + ": " + result.exception.message)
        }
        tempFile.delete()
        pendingImportFile = null
    }

    private fun performShareBackup() {
        lifecycleScope.launch {
            try {
                when (val result = backupService.exportBackup()) {
                    is BackupService.Result.Success -> {
                        val backupUri = FileProvider.getUriForFile(
                            requireContext(),
                            "net.wojteksz128.worktimemeasureapp.provider",
                            result.file
                        )
                        startActivity(
                            Intent.createChooser(
                                Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_STREAM, backupUri)
                                    type = "application/json"
                                },
                                getString(R.string.settings_backup_share)
                            )
                        )
                    }

                    is BackupService.Result.Error ->
                        showMessage(getString(R.string.settings_backup_share_error) + ": " + result.exception.message)
                }
            } catch (e: Exception) {
                showMessage(getString(R.string.settings_backup_share_error) + ": " + e.message)
            }
        }
    }

    private fun showMessage(message: String) {
        Snackbar.make(requireContext(), requireView(), message, Snackbar.LENGTH_LONG).show()
    }

    private inner class ImportStrategyListener : ImportStrategyDialogListener {
        override fun onImportStrategySelected(
            dialog: DialogFragment,
            strategy: BackupService.ImportStrategy,
        ) {
            val file = pendingImportFile ?: return
            lifecycleScope.launch { doImport(file, strategy) }
        }

        override fun onImportStrategyCancelled(dialog: DialogFragment) {
            pendingImportFile?.delete()
            pendingImportFile = null
        }
    }
}
