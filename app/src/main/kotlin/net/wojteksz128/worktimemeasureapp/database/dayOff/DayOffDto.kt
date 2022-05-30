package net.wojteksz128.worktimemeasureapp.database.dayOff

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import net.wojteksz128.worktimemeasureapp.database.EntityDto
import net.wojteksz128.worktimemeasureapp.model.fieldType.DayOffSource
import net.wojteksz128.worktimemeasureapp.model.fieldType.DayOffType
import org.threeten.bp.Month

@Entity(tableName = "day_off")
data class DayOffDto(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    override val id: Long?,

    @ColumnInfo(name = "uuid")
    var uuid: String?,

    @ColumnInfo(name = "type")
    var type: DayOffType,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "startDay")
    var startDay: Int,

    @ColumnInfo(name = "startMonth")
    var startMonth: Month,

    @ColumnInfo(name = "startYear")
    var startYear: Int?,

    @ColumnInfo(name = "finishDay")
    var finishDay: Int,

    @ColumnInfo(name = "finishMonth")
    var finishMonth: Month,

    @ColumnInfo(name = "finishYear")
    var finishYear: Int?,

    @ColumnInfo(name = "source")
    var source: DayOffSource
) : EntityDto
