package net.wojteksz128.worktimemeasureapp.backup

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.wojteksz128.worktimemeasureapp.database.AppDatabase
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventDto
import net.wojteksz128.worktimemeasureapp.database.dayOff.DayOffDto
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayDto
import net.wojteksz128.worktimemeasureapp.model.fieldType.DayOffSource
import net.wojteksz128.worktimemeasureapp.model.fieldType.DayOffType
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: AppDatabase,
    private val baseGson: Gson,
) {
    companion object {
        private const val TAG = "BackupService"
        private const val BACKUP_DIR = "backups"
        private const val BACKUP_EXTENSION = ".wtm_backup.json"
    }

    private val gson: Gson by lazy {
        baseGson.newBuilder().setPrettyPrinting().create()
    }

    suspend fun exportBackup(fileName: String? = null): Result {
        return withContext(Dispatchers.IO) {
            try {
                val backupDir = File(context.filesDir, BACKUP_DIR).apply { mkdirs() }
                val timestamp = System.currentTimeMillis()
                val backupFileName = fileName ?: "backup_${timestamp}$BACKUP_EXTENSION"
                val backupFile = File(backupDir, backupFileName)

                val workDaysWithEvents = database.workDayDao().findAll()
                val daysOff = database.dayOffDao().findAll()

                val comeEventsBackup = workDaysWithEvents.flatMap { workDayWithEvents ->
                    workDayWithEvents.events.map { dto ->
                        ComeEventBackup(
                            id = dto.id,
                            startDate = dto.startDate,
                            endDate = dto.endDate,
                            workDayId = dto.workDayId
                        )
                    }
                }

                val workDaysBackup = workDaysWithEvents.map { workDayWithEvents ->
                    val dto = workDayWithEvents.workDay
                    WorkDayBackup(
                        id = dto.id,
                        date = dto.date,
                        beginSlot = dto.beginSlot,
                        endSlot = dto.endSlot
                    )
                }

                val daysOffBackup = daysOff.map { dto ->
                    DayOffBackup(
                        id = dto.id,
                        uuid = dto.uuid,
                        type = dto.type.name,
                        name = dto.name,
                        startDate = dto.startDate,
                        finishDate = dto.finishDate,
                        source = dto.source.name
                    )
                }

                val backupData = BackupData(
                    comeEvents = comeEventsBackup,
                    workDays = workDaysBackup,
                    daysOff = daysOffBackup
                )

                val json = gson.toJson(backupData)
                backupFile.writeText(json)

                Log.d(TAG, "Backup exported successfully to: ${backupFile.absolutePath}")
                Result.Success(backupFile)
            } catch (e: Exception) {
                Log.e(TAG, "Export backup failed", e)
                Result.Error(e)
            }
        }
    }

    suspend fun isDatabaseEmpty(): Boolean = withContext(Dispatchers.IO) {
        database.workDayDao().findAll().isEmpty() &&
                database.comeEventDao().findAll().isEmpty() &&
                database.dayOffDao().findAll().isEmpty()
    }

    suspend fun importBackup(
        backupFile: File,
        strategy: ImportStrategy = ImportStrategy.MERGE,
    ): Result {
        return withContext(Dispatchers.IO) {
            try {
                if (!backupFile.exists()) {
                    throw IllegalArgumentException("Backup file does not exist: ${backupFile.absolutePath}")
                }

                val json = backupFile.readText()
                val backupData: BackupData = gson.fromJson(json, BackupData::class.java)

                val workDayDtos = backupData.workDays.map { backup ->
                    WorkDayDto(
                        id = backup.id,
                        date = backup.date,
                        beginSlot = backup.beginSlot,
                        endSlot = backup.endSlot
                    )
                }

                val comeEventDtos = backupData.comeEvents.map { backup ->
                    ComeEventDto(
                        id = backup.id,
                        startDate = backup.startDate,
                        endDate = backup.endDate,
                        workDayId = backup.workDayId
                    )
                }

                val dayOffDtos = backupData.daysOff.map { backup ->
                    DayOffDto(
                        id = backup.id,
                        uuid = backup.uuid,
                        type = DayOffType.valueOf(backup.type),
                        name = backup.name,
                        startDate = backup.startDate,
                        finishDate = backup.finishDate,
                        source = DayOffSource.valueOf(backup.source)
                    )
                }

                val workDayDao = database.workDayDao()
                val comeEventDao = database.comeEventDao()
                val dayOffDao = database.dayOffDao()

                when (strategy) {
                    ImportStrategy.REPLACE -> {
                        Log.d(TAG, "Import strategy: REPLACE — clearing existing data")
                        comeEventDao.findAll().forEach { comeEventDao.delete(it) }
                        workDayDao.findAll().forEach { workDayDao.delete(it.workDay) }
                        dayOffDao.findAll().forEach { dayOffDao.delete(it) }
                        workDayDtos.forEach { workDayDao.insert(it) }
                        comeEventDtos.forEach { comeEventDao.insert(it) }
                        dayOffDtos.forEach { dayOffDao.insert(it) }
                    }

                    ImportStrategy.MERGE -> {
                        workDayDtos.forEach { dto ->
                            if (dto.id != null && workDayDao.findByIdOrNull(dto.id.toInt()) != null)
                                workDayDao.update(dto)
                            else
                                workDayDao.insert(dto)
                        }
                        comeEventDtos.forEach { dto ->
                            if (dto.id != null && comeEventDao.findById(dto.id.toInt()) != null)
                                comeEventDao.update(dto)
                            else
                                comeEventDao.insert(dto)
                        }
                        dayOffDtos.forEach { dto ->
                            if (dto.id != null && dayOffDao.findByIdOrNull(dto.id!!) != null)
                                dayOffDao.update(dto)
                            else
                                dayOffDao.insert(dto)
                        }
                    }

                    ImportStrategy.SKIP -> {
                        workDayDtos.forEach { dto ->
                            if (dto.id == null || workDayDao.findByIdOrNull(dto.id.toInt()) == null)
                                workDayDao.insert(dto)
                        }
                        comeEventDtos.forEach { dto ->
                            if (dto.id == null || comeEventDao.findById(dto.id.toInt()) == null)
                                comeEventDao.insert(dto)
                        }
                        dayOffDtos.forEach { dto ->
                            if (dto.id == null || dayOffDao.findByIdOrNull(dto.id!!) == null)
                                dayOffDao.insert(dto)
                        }
                    }
                }

                Log.d(
                    TAG,
                    "Backup imported successfully (strategy=$strategy) from: ${backupFile.absolutePath}"
                )
                Result.Success(backupFile)
            } catch (e: Exception) {
                Log.e(TAG, "Import backup failed", e)
                Result.Error(e)
            }
        }
    }

    suspend fun getBackupFiles(): List<File> {
        return withContext(Dispatchers.IO) {
            try {
                val backupDir = File(context.filesDir, BACKUP_DIR)
                if (backupDir.exists() && backupDir.isDirectory) {
                    backupDir.listFiles { file ->
                        file.isFile && file.extension == BACKUP_EXTENSION.substringAfterLast(".")
                    }?.toList() ?: emptyList()
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get backup files", e)
                emptyList()
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
