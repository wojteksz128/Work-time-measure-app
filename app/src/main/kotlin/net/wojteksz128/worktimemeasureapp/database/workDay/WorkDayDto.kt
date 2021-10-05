package net.wojteksz128.worktimemeasureapp.database.workDay

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "work_day")
class WorkDayDto(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        val id: Long?,

        @ColumnInfo(name = "date")
        var date: Date,

        @ColumnInfo(name = "beginSlot")
        var beginSlot: Date,

        @ColumnInfo(name = "endSlot")
        var endSlot: Date
) {

    // TODO: 30.09.2021 Remove after implement mapper
    @Ignore
    constructor(date: Date)
            : this(null, date, WorkDayUtils.calculateBeginSlot(date), WorkDayUtils.calculateEndSlot(date))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WorkDayDto

        if (id != other.id) return false
        if (date != other.date) return false
        if (beginSlot != other.beginSlot) return false
        if (endSlot != other.endSlot) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + date.hashCode()
        result = 31 * result + beginSlot.hashCode()
        result = 31 * result + endSlot.hashCode()
        return result
    }


}
