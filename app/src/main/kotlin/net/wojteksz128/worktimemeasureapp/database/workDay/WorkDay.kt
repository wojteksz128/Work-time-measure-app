package net.wojteksz128.worktimemeasureapp.database.workDay

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
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
