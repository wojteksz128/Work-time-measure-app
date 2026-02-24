package net.wojteksz128.worktimemeasureapp.backup

import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventDto
import net.wojteksz128.worktimemeasureapp.database.dayOff.DayOffDto
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayDto
import net.wojteksz128.worktimemeasureapp.model.fieldType.DayOffSource
import net.wojteksz128.worktimemeasureapp.model.fieldType.DayOffType
import org.threeten.bp.LocalDate
import org.threeten.bp.ZonedDateTime

private const val CURRENT_BACKUP_VERSION = "1.0"

data class BackupData(
    val comeEvents: List<ComeEventBackup> = emptyList(),
    val workDays: List<WorkDayBackup> = emptyList(),
    val daysOff: List<DayOffBackup> = emptyList(),
    val backupVersion: String = CURRENT_BACKUP_VERSION,
    val backupTimestamp: Long = System.currentTimeMillis(),
)

data class ComeEventBackup(
    val id: Long?,
    val startDate: ZonedDateTime,
    val endDate: ZonedDateTime?,
    val workDayId: Long,
) {
    constructor(comeEventDto: ComeEventDto) : this(
        id = comeEventDto.id,
        startDate = comeEventDto.startDate,
        endDate = comeEventDto.endDate,
        workDayId = comeEventDto.workDayId
    )

    fun toComeEventDto() = ComeEventDto(
        id = id,
        startDate = startDate,
        endDate = endDate,
        workDayId = workDayId
    )
}

data class WorkDayBackup(
    val id: Long?,
    val date: LocalDate,
    val beginSlot: ZonedDateTime,
    val endSlot: ZonedDateTime,
) {
    constructor(workDay: WorkDayDto) : this(
        id = workDay.id,
        date = workDay.date,
        beginSlot = workDay.beginSlot,
        endSlot = workDay.endSlot
    )

    fun toWorkDayDto() = WorkDayDto(
        id = id,
        date = date,
        beginSlot = beginSlot,
        endSlot = endSlot
    )
}

data class DayOffBackup(
    val id: Long?,
    val uuid: String?,
    val type: String,
    val name: String,
    val startDate: LocalDate,
    val finishDate: LocalDate,
    val source: String,
) {
    constructor(dayOff: DayOffDto) : this(
        id = dayOff.id,
        uuid = dayOff.uuid,
        type = dayOff.type.name,
        name = dayOff.name,
        startDate = dayOff.startDate,
        finishDate = dayOff.finishDate,
        source = dayOff.source.name
    )

    fun toDayOffDto() = DayOffDto(
        id = id,
        uuid = uuid,
        type = DayOffType.valueOf(type),
        name = name,
        startDate = startDate,
        finishDate = finishDate,
        source = DayOffSource.valueOf(source)
    )
}
