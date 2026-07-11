package net.wojteksz128.worktimemeasureapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils.Companion.getEndDayTime
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils.Companion.getStartDayTime
import org.threeten.bp.LocalDate
import org.threeten.bp.ZonedDateTime

@Parcelize
data class WorkDay(
    val id: Long?,
    var date: LocalDate,
    var beginSlot: ZonedDateTime,
    var endSlot: ZonedDateTime,
    val events: MutableList<ComeEvent> = mutableListOf(),
) : DomainModel, Parcelable {

    constructor(date: LocalDate)
            : this(null, date, getStartDayTime(date), getEndDayTime(date))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WorkDay

        if (id != other.id) return false
        if (date != other.date) return false
        if (beginSlot != other.beginSlot) return false
        if (endSlot != other.endSlot) return false
        // TODO: Is it creates new problems? Earlier I do not compare events
        if (events != other.events) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + date.hashCode()
        result = 31 * result + beginSlot.hashCode()
        result = 31 * result + endSlot.hashCode()
        result = 31 * result + events.hashCode()
        return result
    }
}
