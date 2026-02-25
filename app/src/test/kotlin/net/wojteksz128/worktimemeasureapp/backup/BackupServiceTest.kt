package net.wojteksz128.worktimemeasureapp.backup

import android.content.Context
import com.google.gson.GsonBuilder
import kotlinx.coroutines.runBlocking
import net.wojteksz128.worktimemeasureapp.backup.migration.BackupMigrationRunner
import net.wojteksz128.worktimemeasureapp.database.AppDatabase
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventDao
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventDto
import net.wojteksz128.worktimemeasureapp.database.dayOff.DayOffDao
import net.wojteksz128.worktimemeasureapp.database.dayOff.DayOffDto
import net.wojteksz128.worktimemeasureapp.database.history.EntityHistoryDao
import net.wojteksz128.worktimemeasureapp.database.history.EntityHistoryDto
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayDao
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayDto
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayWithEventsDto
import net.wojteksz128.worktimemeasureapp.model.fieldType.DayOffSource
import net.wojteksz128.worktimemeasureapp.model.fieldType.DayOffType
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.util.json.LocalDateJsonAdapter
import net.wojteksz128.worktimemeasureapp.util.json.ZonedDateTimeDeserializer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.stub
import org.mockito.kotlin.times
import org.mockito.kotlin.verifyBlocking
import org.mockito.kotlin.whenever
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime

class BackupServiceTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private val context: Context = mock()
    private val database: AppDatabase = mock()
    private val workDayDao: WorkDayDao = mock()
    private val comeEventDao: ComeEventDao = mock()
    private val dayOffDao: DayOffDao = mock()
    private val entityHistoryDao: EntityHistoryDao = mock()
    private val settings: Settings = mock()
    private val migrationRunner: BackupMigrationRunner = mock()

    private val gson = GsonBuilder()
        .registerTypeAdapter(ZonedDateTime::class.java, ZonedDateTimeDeserializer())
        .registerTypeAdapter(LocalDate::class.java, LocalDateJsonAdapter())
        .create()

    private val testTime = ZonedDateTime.of(
        LocalDateTime.of(2024, 1, 10, 12, 0, 0),
        ZoneOffset.UTC
    )
    private val testDate = LocalDate.of(2024, 1, 10)

    private lateinit var backupService: BackupService

    private val profileSettings: Settings.ProfileSettings = mock {
        on { childNodes } doReturn emptySet()
    }
    private val workTimeSettings: Settings.WorkTimeSettings = mock {
        on { childNodes } doReturn emptySet()
    }
    private val daysOffSettings: Settings.DaysOffSettings = mock {
        on { childNodes } doReturn emptySet()
    }
    private val syncSettings: Settings.SyncSettings = mock {
        on { childNodes } doReturn emptySet()
    }

    @Before
    fun setUp() {
        whenever(context.filesDir).thenReturn(tempFolder.root)
        whenever(database.workDayDao()).thenReturn(workDayDao)
        whenever(database.comeEventDao()).thenReturn(comeEventDao)
        whenever(database.dayOffDao()).thenReturn(dayOffDao)
        whenever(database.entityHistoryDao()).thenReturn(entityHistoryDao)
        whenever(settings.Profile).thenReturn(profileSettings)
        whenever(settings.WorkTime).thenReturn(workTimeSettings)
        whenever(settings.DaysOff).thenReturn(daysOffSettings)
        whenever(settings.Sync).thenReturn(syncSettings)

        backupService = BackupService(context, database, gson, settings, migrationRunner)
    }

    // ──────────────────────────── exportBackup ────────────────────────────

    @Test
    fun `givenEmptyDatabase, whenExportBackup, thenReturnsSuccess`() = runBlocking {
        stubEmptyDatabase()

        val result = backupService.exportBackup()

        assertTrue(result is BackupService.Result.Success)
    }

    @Test
    fun `givenEmptyDatabase, whenExportBackup, thenCreatesJsonFile`() = runBlocking {
        stubEmptyDatabase()

        val result = backupService.exportBackup().asSuccess()

        assertTrue(result.file.exists())
        assertTrue(result.file.name.endsWith(".wtm_backup.json"))
    }

    @Test
    fun `givenWorkDaysInDatabase, whenExportBackup, thenBackupContainsWorkDays`() =
        runBlocking {
            stubDatabase(workDays = listOf(aWorkDayWithEvents(id = 1L)))

            val json = backupService.exportBackup().asSuccess().file.readText()

            assertTrue(json.contains("workDays"))
        }

    @Test
    fun `givenDatabaseWithEvents, whenExportBackup, thenBackupContainsComeEvents`() =
        runBlocking {
            val comeEvent = aComeEvent()
            stubDatabase(workDays = listOf(aWorkDayWithEvents(events = listOf(comeEvent))))

            val json = backupService.exportBackup().asSuccess().file.readText()

            assertTrue(json.contains("comeEvents"))
        }

    @Test
    fun `givenCustomFileName, whenExportBackup, thenFileHasGivenName`() = runBlocking {
        stubEmptyDatabase()
        val customName = "my_custom_backup.json"

        val result = backupService.exportBackup(customName).asSuccess()

        assertEquals(customName, result.file.name)
    }

    // ──────────────────────────── isDatabaseEmpty ────────────────────────────

    @Test
    fun `givenEmptyDatabase, whenIsDatabaseEmpty, thenReturnsTrue`() = runBlocking {
        stubEmptyDatabase()

        assertTrue(backupService.isDatabaseEmpty())
    }

    @Test
    fun `givenWorkDaysPresent, whenIsDatabaseEmpty, thenReturnsFalse`() = runBlocking {
        stubDatabase(workDays = listOf(aWorkDayWithEvents(id = 1L)))

        assertFalse(backupService.isDatabaseEmpty())
    }

    // ──────────────────────────── importBackup — REPLACE ────────────────────────────

    @Test
    fun `givenValidBackupFile, whenImportWithReplaceStrategy, thenDeletesAllAndInsertsData`() =
        runBlocking {
            val backupFile = createBackupFile(workDays = 1, comeEvents = 1)
            stubEmptyDatabase()

            val result =
                backupService.importBackup(backupFile, BackupService.ImportStrategy.REPLACE)

            assertTrue(result is BackupService.Result.Success)
            verifyAllTablesCleared()
            verifyBlocking(workDayDao) { insert(any()) }
            verifyBlocking(comeEventDao) { insert(any()) }
        }

    @Test
    fun `givenValidBackupFile, whenImportWithReplaceStrategy, thenInsertsWorkDays`() =
        runBlocking {
            val backupFile = createBackupFile(workDays = 2)
            stubEmptyDatabase()

            backupService.importBackup(backupFile, BackupService.ImportStrategy.REPLACE)

            verifyBlocking(workDayDao, times(2)) { insert(any()) }
        }

    // ──────────────────────────── importBackup — MERGE ────────────────────────────

    @Test
    fun `givenValidBackupFile, whenImportWithMergeStrategy, thenDoesNotDeleteExistingData`() =
        runBlocking {
            val backupFile = createBackupFile(workDays = 1)
            stubEmptyDatabase()

            backupService.importBackup(backupFile, BackupService.ImportStrategy.MERGE)

            verifyNoTablesCleared()
        }

    @Test
    fun `givenNewWorkDayInBackup, whenImportWithMergeStrategy, thenInsertsNewWorkDay`() =
        runBlocking {
            val backupFile = createBackupFile(workDays = 1)
            stubEmptyDatabase()

            backupService.importBackup(backupFile, BackupService.ImportStrategy.MERGE)

            verifyBlocking(workDayDao) { insert(any()) }
        }

    @Test
    fun `givenExistingWorkDayInBackup, whenImportWithMergeStrategy, thenUpdatesExistingWorkDay`() =
        runBlocking {
            val existingId = 1L
            stubDatabase(workDays = listOf(aWorkDayWithEvents(id = existingId)))

            val backupFile = createBackupFile(workDays = 1, existingWorkDayId = existingId)

            backupService.importBackup(backupFile, BackupService.ImportStrategy.MERGE)

            verifyBlocking(workDayDao, never()) { insert(any()) }
            verifyBlocking(workDayDao) { update(any()) }
        }

    // ──────────────────────────── importBackup — SKIP ────────────────────────────

    @Test
    fun `givenValidBackupFile, whenImportWithSkipStrategy, thenDoesNotDeleteExistingData`() =
        runBlocking {
            val backupFile = createBackupFile(workDays = 1)
            stubEmptyDatabase()

            backupService.importBackup(backupFile, BackupService.ImportStrategy.SKIP)

            verifyNoTablesCleared()
        }

    @Test
    fun `givenExistingWorkDayInBackup, whenImportWithSkipStrategy, thenSkipsExistingWorkDay`() =
        runBlocking {
            val existingId = 1L
            stubDatabase(workDays = listOf(aWorkDayWithEvents(id = existingId)))

            val backupFile = createBackupFile(workDays = 1, existingWorkDayId = existingId)

            backupService.importBackup(backupFile, BackupService.ImportStrategy.SKIP)

            verifyBlocking(workDayDao, never()) { insert(any()) }
            verifyBlocking(workDayDao, never()) { update(any()) }
        }

    @Test
    fun `givenNewWorkDayInBackup, whenImportWithSkipStrategy, thenInsertsNewWorkDay`() =
        runBlocking {
            val backupFile = createBackupFile(workDays = 1)
            stubEmptyDatabase()

            backupService.importBackup(backupFile, BackupService.ImportStrategy.SKIP)

            verifyBlocking(workDayDao) { insert(any()) }
        }

    // ──────────────────────────── importBackup — errors ────────────────────────────

    @Test
    fun `givenNonExistentFile, whenImportBackup, thenReturnsError`() = runBlocking {
        val nonExistentFile = tempFolder.root.resolve("nonexistent.json")

        val result = backupService.importBackup(nonExistentFile)

        assertTrue(result is BackupService.Result.Error)
    }

    @Test
    fun `givenNonExistentFile, whenImportBackup, thenErrorContainsIllegalArgumentException`() =
        runBlocking {
            val nonExistentFile = tempFolder.root.resolve("nonexistent.json")

            val exception = backupService.importBackup(nonExistentFile).asError().exception

            assertTrue(exception is IllegalArgumentException)
        }

    // ──────────────────────────── history in backup ────────────────────────────

    @Test
    fun `givenHistoryInBackup, whenImportWithReplaceStrategy, thenInsertsHistory`() =
        runBlocking {
            val backupFile = createBackupFile(historyEntries = 2)
            stubEmptyDatabase()

            backupService.importBackup(backupFile, BackupService.ImportStrategy.REPLACE)

            verifyBlocking(entityHistoryDao, times(2)) { insert(any()) }
        }

    @Test
    fun `givenHistoryInDatabase, whenExportBackup, thenBackupContainsHistory`() =
        runBlocking {
            stubDatabase(historyEntries = listOf(aHistoryEntry(changeGroupId = "group-1")))

            val json = backupService.exportBackup().asSuccess().file.readText()

            assertTrue(json.contains("history"))
            assertTrue(json.contains("group-1"))
        }

    // ──────────────────────────── helpers — stubs ────────────────────────────

    private fun stubEmptyDatabase() = stubDatabase()

    private fun stubDatabase(
        workDays: List<WorkDayWithEventsDto> = emptyList(),
        daysOff: List<DayOffDto> = emptyList(),
        historyEntries: List<EntityHistoryDto> = emptyList(),
    ) {
        workDayDao.stub { onBlocking { findAll() } doReturn workDays }
        comeEventDao.stub { onBlocking { findAll() } doReturn emptyList() }
        dayOffDao.stub { onBlocking { findAll() } doReturn daysOff }
        entityHistoryDao.stub { onBlocking { findAll() } doReturn historyEntries }
    }

    // ──────────────────────────── helpers — verifications ────────────────────────────

    private fun verifyAllTablesCleared() {
        verifyBlocking(entityHistoryDao) { deleteAll() }
        verifyBlocking(workDayDao) { deleteAll() }
        verifyBlocking(comeEventDao) { deleteAll() }
        verifyBlocking(dayOffDao) { deleteAll() }
    }

    private fun verifyNoTablesCleared() {
        verifyBlocking(workDayDao, never()) { deleteAll() }
        verifyBlocking(comeEventDao, never()) { deleteAll() }
        verifyBlocking(dayOffDao, never()) { deleteAll() }
        verifyBlocking(entityHistoryDao, never()) { deleteAll() }
    }

    // ──────────────────────────── helpers — result unwrappers ────────────────────────────

    private fun BackupService.Result.asSuccess() =
        this as? BackupService.Result.Success
            ?: error("Expected Result.Success but was $this")

    private fun BackupService.Result.asError() =
        this as? BackupService.Result.Error
            ?: error("Expected Result.Error but was $this")

    // ──────────────────────────── helpers — object builders ────────────────────────────

    private fun aWorkDay(id: Long = 1L, date: LocalDate = testDate) = WorkDayDto(
        id = id,
        date = date,
        beginSlot = testTime,
        endSlot = testTime.plusHours(8),
    )

    private fun aWorkDayWithEvents(
        id: Long = 1L,
        events: List<ComeEventDto> = emptyList(),
    ) = WorkDayWithEventsDto(aWorkDay(id = id), events)

    private fun aComeEvent(id: Long = 1L, workDayId: Long = 1L) = ComeEventDto(
        id = id,
        startDate = testTime,
        endDate = testTime.plusHours(8),
        workDayId = workDayId,
    )

    private fun aHistoryEntry(
        id: Long = 1L,
        changeGroupId: String = "group-$id",
    ) = EntityHistoryDto(
        id = id,
        changeGroupId = changeGroupId,
        entityType = "WorkDayDto",
        entityId = 1L,
        actionType = "INSERT",
        fieldName = "date",
        oldValue = null,
        newValue = "2024-01-10",
        timestamp = testTime,
    )

    // ──────────────────────────── helpers — backup file factory ────────────────────────────

    @Suppress("EmptyRange")
    private fun createBackupFile(
        workDays: Int = 0,
        comeEvents: Int = 0,
        daysOff: Int = 0,
        historyEntries: Int = 0,
        existingWorkDayId: Long? = null,
    ): java.io.File {
        val backupData = BackupData(
            workDays = (1..workDays).map { i ->
                WorkDayBackup(
                    id = existingWorkDayId ?: i.toLong(),
                    date = testDate.plusDays(i.toLong() - 1),
                    beginSlot = testTime,
                    endSlot = testTime.plusHours(8),
                )
            },
            comeEvents = (1..comeEvents).map { i ->
                ComeEventBackup(
                    id = i.toLong(),
                    startDate = testTime,
                    endDate = testTime.plusHours(8),
                    workDayId = existingWorkDayId ?: 1L,
                )
            },
            daysOff = (1..daysOff).map { i ->
                DayOffBackup(
                    id = i.toLong(),
                    uuid = "uuid-$i",
                    type = DayOffType.PublicHoliday.name,
                    name = "Holiday $i",
                    startDate = testDate,
                    finishDate = testDate,
                    source = DayOffSource.ExternalAPI.name,
                )
            },
            history = (1..historyEntries).map { i ->
                EntityHistoryBackup(
                    id = i.toLong(),
                    changeGroupId = "group-$i",
                    entityType = "WorkDayDto",
                    entityId = 1L,
                    actionType = "INSERT",
                    fieldName = "date",
                    oldValue = null,
                    newValue = "2024-01-10",
                    timestamp = testTime,
                )
            },
        )
        return tempFolder.newFile("backup_test_${System.nanoTime()}.json")
            .also { it.writeText(gson.toJson(backupData)) }
    }
}

