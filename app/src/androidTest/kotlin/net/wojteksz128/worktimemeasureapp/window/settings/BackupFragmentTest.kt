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
        backupService.stub {
            onBlocking { isDatabaseEmpty() } doAnswer { true }
            onBlocking { exportBackup() } doAnswer {
                BackupService.Result.Error(RuntimeException("Not stubbed"))
            }
            onBlocking { importBackup(any(), any()) } doAnswer {
                BackupService.Result.Error(RuntimeException("Not stubbed"))
            }
        }
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
        val fakeUri = Uri.parse("content://fake/backup.json")
        val resultData = Intent().setData(fakeUri)
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
        intending(hasAction(Intent.ACTION_CREATE_DOCUMENT)).respondWith(result)

        backupSettings {
            clickExport()
        }

        intended(hasAction(Intent.ACTION_CREATE_DOCUMENT))
    }

    @Test
    fun whenImportClicked_thenLaunchesGetContentIntent() {
        val fakeUri = Uri.parse("content://fake/backup.json")
        val resultData = Intent().setData(fakeUri)
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
        intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(result)

        backupSettings {
            clickImport()
        }

        intended(hasAction(Intent.ACTION_GET_CONTENT))
    }

    @Test
    fun whenShareClicked_thenLaunchesChooserIntent() {
        val tempFile = File(context.cacheDir, "backup_share_test.json").also { it.writeText("{}") }
        backupService.stub {
            onBlocking { exportBackup() } doAnswer { BackupService.Result.Success(tempFile) }
        }

        backupSettings {
            clickShare()
        }

        intended(hasAction(Intent.ACTION_CHOOSER))
    }

    @Test
    fun whenExportSucceeds_thenSnackbarWithSuccessMessageIsShown() {
        val tempFile = File(context.cacheDir, "backup_export_test.json").also { it.writeText("{}") }
        backupService.stub {
            onBlocking { exportBackup() } doAnswer { BackupService.Result.Success(tempFile) }
        }

        val destFile = File(context.cacheDir, "backup_dest.json")
        val destUri = Uri.fromFile(destFile)
        val resultData = Intent().setData(destUri)
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
        intending(hasAction(Intent.ACTION_CREATE_DOCUMENT)).respondWith(result)

        backupSettings {
            clickExport()
            verifySnackbarIsDisplayed()
            verifySnackbarWithText(context.getString(R.string.settings_backup_export_success))
        }
    }

    @Test
    fun whenImportWithEmptyDatabase_thenImportIsPerformedDirectly() {
        val importFile =
            File(context.cacheDir, "backup_import_test.json").also { it.writeText("{}") }
        val importUri = Uri.fromFile(importFile)

        backupService.stub {
            onBlocking { isDatabaseEmpty() } doAnswer { true }
            onBlocking { importBackup(any(), any()) } doAnswer {
                BackupService.Result.Success(importFile)
            }
        }

        val resultData = Intent().setData(importUri)
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
        intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(result)

        backupSettings {
            clickImport()
        }

        verifyBlocking(backupService, timeout(2000)) {
            importBackup(any(), any())
        }
    }

    @Test
    fun whenImportWithNonEmptyDatabase_thenStrategyDialogIsShown() {
        val importFile =
            File(context.cacheDir, "backup_import_nonempty.json").also { it.writeText("{}") }
        val importUri = Uri.fromFile(importFile)

        backupService.stub {
            onBlocking { isDatabaseEmpty() } doAnswer { false }
        }

        val resultData = Intent().setData(importUri)
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
        intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(result)

        backupSettings {
            clickImport()
            verifyImportStrategyDialogIsDisplayed()
        }
    }

    @Test
    fun whenImportStrategyDialogCancelled_thenImportIsNotPerformed() {
        val importFile =
            File(context.cacheDir, "backup_import_cancel.json").also { it.writeText("{}") }
        val importUri = Uri.fromFile(importFile)

        backupService.stub {
            onBlocking { isDatabaseEmpty() } doAnswer { false }
        }

        val resultData = Intent().setData(importUri)
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
        intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(result)

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
        val importFile =
            File(context.cacheDir, "backup_import_merge.json").also { it.writeText("{}") }
        val importUri = Uri.fromFile(importFile)

        backupService.stub {
            onBlocking { isDatabaseEmpty() } doAnswer { false }
            onBlocking { importBackup(any(), any()) } doAnswer {
                BackupService.Result.Success(importFile)
            }
        }

        val resultData = Intent().setData(importUri)
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
        intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(result)

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
        val importFile =
            File(context.cacheDir, "backup_import_replace.json").also { it.writeText("{}") }
        val importUri = Uri.fromFile(importFile)

        backupService.stub {
            onBlocking { isDatabaseEmpty() } doAnswer { false }
            onBlocking { importBackup(any(), any()) } doAnswer {
                BackupService.Result.Success(importFile)
            }
        }

        val resultData = Intent().setData(importUri)
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
        intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(result)

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
        val importFile =
            File(context.cacheDir, "backup_import_skip.json").also { it.writeText("{}") }
        val importUri = Uri.fromFile(importFile)

        backupService.stub {
            onBlocking { isDatabaseEmpty() } doAnswer { false }
            onBlocking { importBackup(any(), any()) } doAnswer {
                BackupService.Result.Success(importFile)
            }
        }

        val resultData = Intent().setData(importUri)
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
        intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(result)

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
}
