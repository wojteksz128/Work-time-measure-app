package net.wojteksz128.worktimemeasureapp.database.comeEvent


import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDay
import java.util.*

@Entity(tableName = "come_event")
class ComeEvent : Comparable<ComeEvent> {

    @PrimaryKey(autoGenerate = true)
    val id: Long?
    var startDate: Date? = null
    var endDate: Date? = null
    var duration: Date? = null
    var workDayId: Int = 0

    val isEnded: Boolean
        get() = this.endDate != null && this.duration != null


    constructor(id: Long?, startDate: Date, endDate: Date, duration: Date, workDayId: Int) {
        this.id = id
        this.startDate = startDate
        this.endDate = endDate
        this.duration = duration
        this.workDayId = workDayId
    }

    @Ignore
    constructor(startDate: Date, endDate: Date, duration: Date, workDayId: Int) {
        this.startDate = startDate
        this.endDate = endDate
        this.duration = duration
        this.workDayId = workDayId
    }

    @Ignore
    constructor(startDate: Date, workDay: WorkDay) {
        this.startDate = startDate
        this.workDayId = workDay.id
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val event = o as ComeEvent?

        if (workDayId != event!!.workDayId) return false
        return if (if (id != null) id != event.id else event.id != null) false else startDate == event.startDate
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + startDate!!.hashCode()
        result = 31 * result + workDayId
        return result
    }

    override fun compareTo(comeEvent: ComeEvent): Int {
        return (this.startDate!!.time - comeEvent.getStartDate().getTime()).toInt()
    }
}
