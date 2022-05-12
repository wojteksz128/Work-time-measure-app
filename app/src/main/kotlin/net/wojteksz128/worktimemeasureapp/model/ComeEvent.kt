package net.wojteksz128.worktimemeasureapp.model

import net.wojteksz128.worktimemeasureapp.util.datetime.minus
import java.util.*

data class ComeEvent(
    val id: Long?,
    var startDate: Date,
    var endDate: Date?,
    var durationMillis: Long?,
    val workDayId: Long,
) : DomainModel {
    val isEnded: Boolean
        get() = endDate != null

    constructor(startDate: Date, endDate: Date?, workDayId: Long)
            : this(null, startDate, endDate, endDate?.let { (it - startDate).time }, workDayId)

    constructor(startDate: Date, workDay: WorkDay)
            : this(startDate, null, workDay.id!!)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ComeEvent

        if (id != other.id) return false
        if (startDate != other.startDate) return false
        if (workDayId != other.workDayId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + startDate.hashCode()
        result = 31 * result + workDayId.hashCode()
        return result
    }

}
