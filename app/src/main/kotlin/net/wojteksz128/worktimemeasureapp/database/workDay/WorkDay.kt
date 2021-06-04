package net.wojteksz128.worktimemeasureapp.database.workDay

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "work_day")
class WorkDay(
        @PrimaryKey(autoGenerate = true)
        val id: Long?,
        var date: Date,
        var beginSlot: Date,
        var endSlot: Date
) {

    @Ignore
    constructor(date: Date)
            : this(null, date, WorkDayUtils.calculateBeginSlot(date), WorkDayUtils.calculateEndSlot(date))
}
