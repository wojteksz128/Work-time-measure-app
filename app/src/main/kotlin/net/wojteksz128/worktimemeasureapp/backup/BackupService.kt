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
import net.wojteksz128.worktimemeasureapp.database.history.EntityHistoryDto
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayDao
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayDto
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupService @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val database: AppDatabase,
    private val baseGson: Gson,
    private val settings: Settings,
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
        val history = database.entityHistoryDao().findAll()

        val comeEventsBackup =
            workDaysWithEvents.flatMap { it.events.map { e -> ComeEventBackup(e) } }
        val workDaysBackup = workDaysWithEvents.map { WorkDayBackup(it.workDay) }
        val daysOffBackup = daysOff.map { DayOffBackup(it) }
        val historyBackup = history.map { EntityHistoryBackup(it) }
        val settingsBackup = buildMap {
            listOf(settings.Profile, settings.WorkTime, settings.DaysOff, settings.Sync)
                .flatMap { it.childNodes }
                .forEach { item -> put(item.key, item.valueNullable?.toString()) }
        }

        return BackupData(
            comeEvents = comeEventsBackup,
            workDays = workDaysBackup,
            daysOff = daysOffBackup,
            history = historyBackup,
            settings = settingsBackup,
        )
    }

    suspend fun isDatabaseEmpty(): Boolean = withContext(Dispatchers.IO) {
        database.workDayDao().findAll().isEmpty() &&
                database.comeEventDao().findAll().isEmpty() &&
                database.dayOffDao().findAll().isEmpty() &&
                database.entityHistoryDao().findAll().isEmpty()
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
            val historyDtoList = backupData.history.map { it.toEntityHistoryDto() }

            when (strategy) {
                ImportStrategy.REPLACE ->
                    importBackupUsingReplaceStrategy(
                        workDayDtoList,
                        comeEventDtoList,
                        dayOffDtoList,
                        historyDtoList
                    )
                ImportStrategy.MERGE ->
                    importBackupUsingMergeStrategy(
                        workDayDtoList,
                        comeEventDtoList,
                        dayOffDtoList,
                        historyDtoList
                    )
                ImportStrategy.SKIP ->
                    importBackupUsingSkipStrategy(
                        workDayDtoList,
                        comeEventDtoList,
                        dayOffDtoList,
                        historyDtoList
                    )
            }

            restoreSettings(backupData.settings)

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
        historyDtoList: List<EntityHistoryDto>,
    ) {
        Log.d(classTag, "Import strategy: REPLACE — clearing existing data")
        database.entityHistoryDao().deleteAll()
        database.comeEventDao().deleteAll()
        database.workDayDao().deleteAll()
        database.dayOffDao().deleteAll()

        workDayDtoList.forEach { database.workDayDao().insert(it) }
        comeEventDtoList.forEach { database.comeEventDao().insert(it) }
        dayOffDtoList.forEach { database.dayOffDao().insert(it) }
        historyDtoList.forEach { database.entityHistoryDao().insert(it) }
    }

    private suspend fun importBackupUsingMergeStrategy(
        workDayDtoList: List<WorkDayDto>,
        comeEventDtoList: List<ComeEventDto>,
        dayOffDtoList: List<DayOffDto>,
        historyDtoList: List<EntityHistoryDto>,
    ) {
        Log.d(classTag, "Import strategy: MERGE — updating existing data")
        importWorkDays(workDayDtoList) { dao, dto -> dao.update(dto) }
        importComeEvents(comeEventDtoList) { dao, dto -> dao.update(dto) }
        importDayOffs(dayOffDtoList) { dao, dto -> dao.update(dto) }
        importHistory(historyDtoList)
    }

    private suspend fun importBackupUsingSkipStrategy(
        workDayDtoList: List<WorkDayDto>,
        comeEventDtoList: List<ComeEventDto>,
        dayOffDtoList: List<DayOffDto>,
        historyDtoList: List<EntityHistoryDto>,
    ) {
        Log.d(classTag, "Import strategy: SKIP — skipping existing data")
        importWorkDays(workDayDtoList)
        importComeEvents(comeEventDtoList)
        importDayOffs(dayOffDtoList)
        importHistory(historyDtoList)
    }

    private suspend fun importWorkDays(
        workDayDtoList: List<WorkDayDto>,
        actionIfExists: suspend (WorkDayDao, WorkDayDto) -> Unit = { _, _ -> },
    ) {
        val workDayDao = database.workDayDao()

        Log.d(classTag, "Importing work days")
        val existingIds = workDayDao.findAll().map { it.workDay.id }
        workDayDtoList.forEach { dto ->
            if (dto.id == null || dto.id !in existingIds) workDayDao.insert(dto)
            else actionIfExists(workDayDao, dto)
        }
    }

    private suspend fun importComeEvents(
        comeEventDtoList: List<ComeEventDto>,
        actionIfExists: suspend (ComeEventDao, ComeEventDto) -> Unit = { _, _ -> },
    ) {
        val comeEventDao = database.comeEventDao()

        Log.d(classTag, "Importing come events")
        val existingIds = comeEventDao.findAll().map { it.id }
        comeEventDtoList.forEach { dto ->
            if (dto.id == null || dto.id !in existingIds) comeEventDao.insert(dto)
            else actionIfExists(comeEventDao, dto)
        }
    }

    private suspend fun importDayOffs(
        dayOffDtoList: List<DayOffDto>,
        actionIfExists: suspend (DayOffDao, DayOffDto) -> Unit = { _, _ -> },
    ) {
        val dayOffDao = database.dayOffDao()

        Log.d(classTag, "Importing days off")
        val existingIds = dayOffDao.findAll().map { it.id }
        dayOffDtoList.forEach { dto ->
            if (dto.id == null || dto.id !in existingIds) dayOffDao.insert(dto)
            else actionIfExists(dayOffDao, dto)
        }
    }

    private suspend fun importHistory(historyDtoList: List<EntityHistoryDto>) {
        val historyDao = database.entityHistoryDao()

        Log.d(classTag, "Importing history")
        val existingIds = historyDao.findAll().mapNotNull { it.id }.toSet()
        historyDtoList.forEach { dto ->
            if (dto.id == null || dto.id !in existingIds) historyDao.insert(dto)
        }
    }

    private fun restoreSettings(settingsMap: Map<String, String?>) {
        if (settingsMap.isEmpty()) return

        Log.d(classTag, "Restoring settings")
        val knownItems =
            listOf(settings.Profile, settings.WorkTime, settings.DaysOff, settings.Sync)
                .flatMap { it.childNodes }
                .associateBy { it.key }
        settingsMap.forEach { (key, rawValue) ->
            knownItems[key]?.restoreValue(rawValue)
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
