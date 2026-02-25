package net.wojteksz128.worktimemeasureapp.window.settings

import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.backup.BackupService
import net.wojteksz128.worktimemeasureapp.settings.InitialSettingsPreparer
import net.wojteksz128.worktimemeasureapp.util.launchFragmentInHiltContainer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.timeout
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.never
import org.mockito.kotlin.stub
import org.mockito.kotlin.verifyBlocking
import java.io.File
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class BackupFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var initialSettingsPreparer: InitialSettingsPreparer

    @Inject
    lateinit var backupService: BackupService

    private lateinit var context: Context

    @Before
    fun setup() {
        Intents.init()
        hiltRule.inject()
        context = ApplicationProvider.getApplicationContext()
        PreferenceManager.getDefaultSharedPreferences(context).edit().clear().apply()
        initialSettingsPreparer.initSettings()
        stubBackupServiceDefaults()
        launchFragmentInHiltContainer<BackupFragment>()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun givenFragmentStarted_thenDisplaysBackupPreferences() {
        backupSettings {
            verifyIsDisplayed()
        }
    }

    @Test
    fun whenExportClicked_thenLaunchesCreateDocumentIntent() {
        stubCreateDocumentResult()

        backupSettings {
            clickExport()
        }

        intended(hasAction(Intent.ACTION_CREATE_DOCUMENT))
    }

    @Test
    fun whenImportClicked_thenLaunchesGetContentIntent() {
        stubGetContentResult()

        backupSettings {
            clickImport()
        }

        intended(hasAction(Intent.ACTION_GET_CONTENT))
    }

    @Test
    fun whenShareClicked_thenLaunchesChooserIntent() {
        stubExportSuccess()

        backupSettings {
            clickShare()
        }

        intended(hasAction(Intent.ACTION_CHOOSER))
    }

    @Test
    fun whenExportSucceeds_thenSnackbarWithSuccessMessageIsShown() {
        stubExportSuccess()
        stubCreateDocumentResult()

        backupSettings {
            clickExport()
            verifySnackbarIsDisplayed()
            verifySnackbarWithText(context.getString(R.string.settings_backup_export_success))
        }
    }

    @Test
    fun whenImportWithEmptyDatabase_thenImportIsPerformedDirectly() {
        stubBackupServiceWithEmptyDatabase()
        stubGetContentResult()

        backupSettings {
            clickImport()
        }

        verifyBlocking(backupService, timeout(2000)) {
            importBackup(any(), any())
        }
    }

    @Test
    fun whenImportWithNonEmptyDatabase_thenStrategyDialogIsShown() {
        stubBackupServiceWithNonEmptyDatabase()
        stubGetContentResult()

        backupSettings {
            clickImport()
            verifyImportStrategyDialogIsDisplayed()
        }
    }

    @Test
    fun whenImportStrategyDialogCancelled_thenImportIsNotPerformed() {
        stubBackupServiceWithNonEmptyDatabase()
        stubGetContentResult()

        backupSettings {
            clickImport()
            onImportStrategyDialog {
                cancelImport()
            }
        }

        verifyBlocking(backupService, never()) {
            importBackup(any(), any())
        }
    }

    @Test
    fun whenImportStrategyMergeSelected_thenImportBackupCalledWithMergeStrategy() {
        stubBackupServiceWithNonEmptyDatabase(
            importResult = BackupService.Result.Success(
                aTempFile(
                    "merge"
                )
            )
        )
        stubGetContentResult()

        backupSettings {
            clickImport()
            onImportStrategyDialog {
                selectMerge()
                confirmImport()
            }
        }

        verifyBlocking(backupService, timeout(2000)) {
            importBackup(any(), eq(BackupService.ImportStrategy.MERGE))
        }
    }

    @Test
    fun whenImportStrategyReplaceSelected_thenImportBackupCalledWithReplaceStrategy() {
        stubBackupServiceWithNonEmptyDatabase(
            importResult = BackupService.Result.Success(
                aTempFile(
                    "replace"
                )
            )
        )
        stubGetContentResult()

        backupSettings {
            clickImport()
            onImportStrategyDialog {
                selectReplace()
                confirmImport()
            }
        }

        verifyBlocking(backupService, timeout(2000)) {
            importBackup(any(), eq(BackupService.ImportStrategy.REPLACE))
        }
    }

    @Test
    fun whenImportStrategySkipSelected_thenImportBackupCalledWithSkipStrategy() {
        stubBackupServiceWithNonEmptyDatabase(
            importResult = BackupService.Result.Success(
                aTempFile(
                    "skip"
                )
            )
        )
        stubGetContentResult()

        backupSettings {
            clickImport()
            onImportStrategyDialog {
                selectSkip()
                confirmImport()
            }
        }

        verifyBlocking(backupService, timeout(2000)) {
            importBackup(any(), eq(BackupService.ImportStrategy.SKIP))
        }
    }

    // ──────────────────────────── helpers — stubs ────────────────────────────

    private fun stubBackupServiceDefaults() {
        backupService.stub {
            onBlocking { isDatabaseEmpty() } doAnswer { true }
            onBlocking { exportBackup() } doAnswer {
                BackupService.Result.Error(RuntimeException("Not stubbed"))
            }
            onBlocking { importBackup(any(), any()) } doAnswer {
                BackupService.Result.Error(RuntimeException("Not stubbed"))
            }
        }
    }

    private fun stubExportSuccess() {
        backupService.stub {
            onBlocking { exportBackup() } doAnswer { BackupService.Result.Success(aTempFile("export")) }
        }
    }

    private fun stubBackupServiceWithEmptyDatabase(
        importResult: BackupService.Result = BackupService.Result.Success(aTempFile("import_empty")),
    ) {
        backupService.stub {
            onBlocking { isDatabaseEmpty() } doAnswer { true }
            onBlocking { importBackup(any(), any()) } doAnswer { importResult }
        }
    }

    private fun stubBackupServiceWithNonEmptyDatabase(
        importResult: BackupService.Result = BackupService.Result.Error(RuntimeException("Not stubbed")),
    ) {
        backupService.stub {
            onBlocking { isDatabaseEmpty() } doAnswer { false }
            onBlocking { importBackup(any(), any()) } doAnswer { importResult }
        }
    }

    // ──────────────────────────── helpers — intent stubs ────────────────────────────

    private fun stubCreateDocumentResult(uri: Uri = Uri.fromFile(aTempFile("dest"))) {
        val resultData = Intent().setData(uri)
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
        intending(hasAction(Intent.ACTION_CREATE_DOCUMENT)).respondWith(result)
    }

    private fun stubGetContentResult(uri: Uri = Uri.fromFile(aTempFile("import"))) {
        val resultData = Intent().setData(uri)
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
        intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(result)
    }

    // ──────────────────────────── helpers — factories ────────────────────────────

    private fun aTempFile(tag: String): File =
        File(context.cacheDir, "backup_${tag}_test.json").also { it.writeText("{}") }
}
