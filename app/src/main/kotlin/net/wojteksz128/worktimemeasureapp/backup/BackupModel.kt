package net.wojteksz128.worktimemeasureapp.backup

import com.google.gson.annotations.SerializedName
import org.threeten.bp.LocalDate
import org.threeten.bp.ZonedDateTime

data class BackupData(
    @SerializedName("comeEvents")
    val comeEvents: List<ComeEventBackup> = emptyList(),
    @SerializedName("workDays")
    val workDays: List<WorkDayBackup> = emptyList(),
    @SerializedName("daysOff")
    val daysOff: List<DayOffBackup> = emptyList(),
    @SerializedName("backupVersion")
    val backupVersion: String = "1.0",
    @SerializedName("backupTimestamp")
    val backupTimestamp: Long = System.currentTimeMillis(),
)

data class ComeEventBackup(
    @SerializedName("id")
    val id: Long?,
    @SerializedName("startDate")
    val startDate: ZonedDateTime,
    @SerializedName("endDate")
    val endDate: ZonedDateTime?,
    @SerializedName("workDayId")
    val workDayId: Long,
)

data class WorkDayBackup(
    @SerializedName("id")
    val id: Long?,
    @SerializedName("date")
    val date: LocalDate,
    @SerializedName("beginSlot")
    val beginSlot: ZonedDateTime,
    @SerializedName("endSlot")
    val endSlot: ZonedDateTime,
)

data class DayOffBackup(
    @SerializedName("id")
    val id: Long?,
    @SerializedName("uuid")
    val uuid: String?,
    @SerializedName("type")
    val type: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("startDate")
    val startDate: LocalDate,
    @SerializedName("finishDate")
    val finishDate: LocalDate,
    @SerializedName("source")
    val source: String,
)


