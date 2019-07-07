package net.wojteksz128.worktimemeasureapp.database.comeEvent

import android.arch.persistence.room.*
import android.arch.persistence.room.ForeignKey.CASCADE
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDay
import java.util.*

@Entity(tableName = "come_event"
        , foreignKeys = [ForeignKey(entity = WorkDay::class, parentColumns = ["id"], childColumns = ["workDayId"], onDelete = CASCADE)])
class ComeEvent(
        @PrimaryKey(autoGenerate = true)
        val id: Long?,
        var startDate: Date,
        var endDate: Date?,
        var duration: Date?,
        @ColumnInfo(index = true)
        val workDayId: Long
) : Comparable<ComeEvent> {

    val isEnded: Boolean
        get() = this.endDate != null && this.duration != null

    @Ignore
    constructor(startDate: Date, endDate: Date?, duration: Date?, workDayId: Long)
            : this(null, startDate, endDate, duration, workDayId)

    @Ignore
    constructor(startDate: Date, workDay: WorkDay)
            : this(startDate, null, null, workDay.id!!)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val event = other as ComeEvent?

        if (workDayId != event!!.workDayId) return false
        return if (if (id != null) id != event.id else event.id != null) false else startDate == event.startDate
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + startDate.hashCode()
        result = 31 * result + workDayId.toInt()
        return result
    }

    override fun compareTo(other: ComeEvent): Int {
        return (this.startDate.time - other.startDate.time).toInt()
    }
}
