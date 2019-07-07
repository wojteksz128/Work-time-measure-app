package net.wojteksz128.worktimemeasureapp.database.workDay

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity(tableName = "work_day")
class WorkDay {

    @PrimaryKey(autoGenerate = true)
    val id: Int
    var date: Date? = null
    var beginSlot: Date? = null
    var endSlot: Date? = null
    var percentDeclaredTime: Double = 0.toDouble()


    constructor(id: Int, date: Date, beginSlot: Date, endSlot: Date, percentDeclaredTime: Double) {
        this.id = id
        this.date = date
        this.beginSlot = beginSlot
        this.endSlot = endSlot
        this.percentDeclaredTime = percentDeclaredTime
    }

    @Ignore
    constructor(date: Date) {
        this.date = date
        this.beginSlot = WorkDayUtils.calculateBeginSlot(date)
        this.endSlot = WorkDayUtils.calculateEndSlot(date)
        this.percentDeclaredTime = 0.0
    }
}
