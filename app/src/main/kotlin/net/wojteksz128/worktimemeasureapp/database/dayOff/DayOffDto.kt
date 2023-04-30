package net.wojteksz128.worktimemeasureapp.database.dayOff

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import net.wojteksz128.worktimemeasureapp.database.EntityDto
import net.wojteksz128.worktimemeasureapp.model.fieldType.DayOffSource
import net.wojteksz128.worktimemeasureapp.model.fieldType.DayOffType
import org.threeten.bp.LocalDate

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

    @ColumnInfo(name = "startDate")
    var startDate: LocalDate,

    @ColumnInfo(name = "finishDate")
    var finishDate: LocalDate,

    @ColumnInfo(name = "source")
    var source: DayOffSource,
) : EntityDto
