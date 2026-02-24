package net.wojteksz128.worktimemeasureapp.backup

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
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
) {
    companion object {
        private const val TAG = "BackupService"
        private const val BACKUP_DIR = "backups"
        private const val BACKUP_EXTENSION = ".wtm_backup.json"
        private val gson: Gson = GsonBuilder()
            .setPrettyPrinting()
            .create()
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

    suspend fun importBackup(backupFile: File): Result {
        return withContext(Dispatchers.IO) {
            try {
                if (!backupFile.exists()) {
                    throw IllegalArgumentException("Backup file does not exist: ${backupFile.absolutePath}")
                }

                val json = backupFile.readText()
                val backupData: BackupData = gson.fromJson(json, BackupData::class.java)

                val comeEventDtos = backupData.comeEvents.map { backup ->
                    ComeEventDto(
                        id = backup.id,
                        startDate = backup.startDate,
                        endDate = backup.endDate,
                        workDayId = backup.workDayId
                    )
                }

                val workDayDtos = backupData.workDays.map { backup ->
                    WorkDayDto(
                        id = backup.id,
                        date = backup.date,
                        beginSlot = backup.beginSlot,
                        endSlot = backup.endSlot
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

                workDayDtos.forEach { database.workDayDao().insert(it) }
                comeEventDtos.forEach { database.comeEventDao().insert(it) }
                dayOffDtos.forEach { database.dayOffDao().insert(it) }

                Log.d(TAG, "Backup imported successfully from: ${backupFile.absolutePath}")
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

    sealed class Result {
        data class Success(val file: File) : Result()
        data class Error(val exception: Exception) : Result()
    }
}





