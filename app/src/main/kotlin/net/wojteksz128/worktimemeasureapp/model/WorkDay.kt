package net.wojteksz128.worktimemeasureapp.model

import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayUtils.calculateBeginSlot
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayUtils.calculateEndSlot
import java.util.*

data class WorkDay(
    val id: Long?,
    var date: Date,
    var beginSlot: Date,
    var endSlot: Date,
    val events: MutableList<ComeEvent> = mutableListOf(), // TODO: 30.09.2021 Change to set?!
) : DomainModel {

    constructor(date: Date)
            : this(null, date, calculateBeginSlot(date), calculateEndSlot(date))

    fun isAllEventsEnded() = events.all { it.isEnded }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WorkDay

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
