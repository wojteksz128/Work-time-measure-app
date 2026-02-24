package net.wojteksz128.worktimemeasureapp.backup

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.wojteksz128.worktimemeasureapp.database.AppDatabase
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventDao
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventDto
import net.wojteksz128.worktimemeasureapp.database.dayOff.DayOffDao
import net.wojteksz128.worktimemeasureapp.database.dayOff.DayOffDto
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayDao
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayDto
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupService @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val database: AppDatabase,
    private val baseGson: Gson,
) : ClassTagAware {
    companion object {
        private const val BACKUP_DIR = "backups"
        private const val BACKUP_EXTENSION = ".wtm_backup.json"
    }

    private val gson: Gson by lazy {
        baseGson.newBuilder().setPrettyPrinting().create()
    }

    suspend fun exportBackup(fileName: String? = null) = withContext(Dispatchers.IO) {
        try {
            val backupDir = File(context.filesDir, BACKUP_DIR).apply { mkdirs() }
            val timestamp = System.currentTimeMillis()
            val backupFileName = fileName ?: "backup_${timestamp}$BACKUP_EXTENSION"
            val backupFile = File(backupDir, backupFileName)

            val backupData = createBackupData()

            val json = gson.toJson(backupData)
            backupFile.writeText(json)

            Log.d(classTag, "Backup exported successfully to: ${backupFile.absolutePath}")
            Result.Success(backupFile)
        } catch (e: Exception) {
            Log.e(classTag, "Export backup failed", e)
            Result.Error(e)
        }
    }

    private suspend fun createBackupData(): BackupData {
        val workDaysWithEvents = database.workDayDao().findAll()
        val daysOff = database.dayOffDao().findAll()

        val comeEventsBackup = workDaysWithEvents.flatMap { workDayWithEvents ->
            workDayWithEvents.events.map { ComeEventBackup(it) }
        }
        val workDaysBackup = workDaysWithEvents.map { WorkDayBackup(it.workDay) }
        val daysOffBackup = daysOff.map { dto -> DayOffBackup(dto) }

        return BackupData(
            comeEvents = comeEventsBackup,
            workDays = workDaysBackup,
            daysOff = daysOffBackup
        )
    }

    suspend fun isDatabaseEmpty(): Boolean = withContext(Dispatchers.IO) {
        database.workDayDao().findAll().isEmpty() &&
                database.comeEventDao().findAll().isEmpty() &&
                database.dayOffDao().findAll().isEmpty()
    }

    suspend fun importBackup(
        backupFile: File,
        strategy: ImportStrategy = ImportStrategy.MERGE,
    ) = withContext(Dispatchers.IO) {
        try {
            if (!backupFile.exists()) {
                throw IllegalArgumentException("Backup file does not exist: ${backupFile.absolutePath}")
            }

            val json = backupFile.readText()
            val backupData = gson.fromJson(json, BackupData::class.java)

            val workDayDtoList = backupData.workDays.map { it.toWorkDayDto() }
            val comeEventDtoList = backupData.comeEvents.map { it.toComeEventDto() }
            val dayOffDtoList = backupData.daysOff.map { it.toDayOffDto() }

            when (strategy) {
                ImportStrategy.REPLACE ->
                    importBackupUsingReplaceStrategy(
                        workDayDtoList,
                        comeEventDtoList,
                        dayOffDtoList
                    )

                ImportStrategy.MERGE ->
                    importBackupUsingMergeStrategy(workDayDtoList, comeEventDtoList, dayOffDtoList)

                ImportStrategy.SKIP ->
                    importBackupUsingSkipStrategy(workDayDtoList, comeEventDtoList, dayOffDtoList)
            }

            Log.d(
                classTag,
                "Backup imported successfully (strategy=$strategy) from: ${backupFile.absolutePath}"
            )
            Result.Success(backupFile)
        } catch (e: Exception) {
            Log.e(classTag, "Import backup failed", e)
            Result.Error(e)
        }
    }

    private suspend fun importBackupUsingReplaceStrategy(
        workDayDtoList: List<WorkDayDto>,
        comeEventDtoList: List<ComeEventDto>,
        dayOffDtoList: List<DayOffDto>,
    ) {
        val workDayDao = database.workDayDao()
        val comeEventDao = database.comeEventDao()
        val dayOffDao = database.dayOffDao()

        Log.d(classTag, "Import strategy: REPLACE — clearing existing data")
        comeEventDao.findAll().forEach { comeEventDao.delete(it) }
        workDayDao.findAll().forEach { workDayDao.delete(it.workDay) }
        dayOffDao.findAll().forEach { dayOffDao.delete(it) }

        workDayDtoList.forEach { workDayDao.insert(it) }
        comeEventDtoList.forEach { comeEventDao.insert(it) }
        dayOffDtoList.forEach { dayOffDao.insert(it) }
    }

    private suspend fun importBackupUsingMergeStrategy(
        workDayDtoList: List<WorkDayDto>,
        comeEventDtoList: List<ComeEventDto>,
        dayOffDtoList: List<DayOffDto>,
    ) {
        Log.d(classTag, "Import strategy: MERGE — updating existing data")
        importWorkDays(workDayDtoList) { dao, dto -> dao.update(dto) }
        importComeEvents(comeEventDtoList) { dao, dto -> dao.update(dto) }
        importDayOffs(dayOffDtoList) { dao, dto -> dao.update(dto) }
    }

    private suspend fun importBackupUsingSkipStrategy(
        workDayDtoList: List<WorkDayDto>,
        comeEventDtoList: List<ComeEventDto>,
        dayOffDtoList: List<DayOffDto>,
    ) {
        Log.d(classTag, "Import strategy: SKIP — skipping existing data")
        importWorkDays(workDayDtoList)
        importComeEvents(comeEventDtoList)
        importDayOffs(dayOffDtoList)
    }

    private suspend fun importWorkDays(
        workDayDtoList: List<WorkDayDto>,
        actionIfExists: suspend (WorkDayDao, WorkDayDto) -> Unit = { _, _ -> },
    ) {
        val workDayDao = database.workDayDao()

        Log.d(classTag, "Importing work days")
        val existingWorkDaysIds = workDayDao.findAll().map { it.workDay.id }
        workDayDtoList.forEach { importedWorkDay ->
            if (importedWorkDay.id == null || importedWorkDay.id !in existingWorkDaysIds) {
                workDayDao.insert(importedWorkDay)
            } else {
                actionIfExists(workDayDao, importedWorkDay)
            }
        }
    }

    private suspend fun importComeEvents(
        comeEventDtoList: List<ComeEventDto>,
        actionIfExists: suspend (ComeEventDao, ComeEventDto) -> Unit = { _, _ -> },
    ) {
        val comeEventDao = database.comeEventDao()

        Log.d(classTag, "Importing come events")
        val existingComeEventsIds = comeEventDao.findAll().map { it.id }
        comeEventDtoList.forEach { importedComeEvent ->
            if (importedComeEvent.id == null || importedComeEvent.id !in existingComeEventsIds) {
                comeEventDao.insert(importedComeEvent)
            } else {
                actionIfExists(comeEventDao, importedComeEvent)
            }
        }
    }

    private suspend fun importDayOffs(
        dayOffDtoList: List<DayOffDto>,
        actionIfExists: suspend (DayOffDao, DayOffDto) -> Unit = { _, _ -> },
    ) {
        val dayOffDao = database.dayOffDao()

        Log.d(classTag, "Importing days off")
        val existingDayOffsIds = dayOffDao.findAll().map { it.id }
        dayOffDtoList.forEach { importedDayOff ->
            if (importedDayOff.id == null || importedDayOff.id !in existingDayOffsIds) {
                dayOffDao.insert(importedDayOff)
            } else {
                actionIfExists(dayOffDao, importedDayOff)
            }
        }
    }

    enum class ImportStrategy {
        REPLACE,
        MERGE,
        SKIP,
    }

    sealed class Result {
        data class Success(val file: File) : Result()
        data class Error(val exception: Exception) : Result()
    }
}
