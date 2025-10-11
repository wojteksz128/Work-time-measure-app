package net.wojteksz128.worktimemeasureapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.threeten.bp.ZonedDateTime

@Parcelize
data class ComeEvent(
    val id: Long?,
    var startDate: ZonedDateTime,
    var endDate: ZonedDateTime?,
    val workDayId: Long,
) : DomainModel, Parcelable {
    val isEnded: Boolean
        get() = endDate != null

    constructor(startDate: ZonedDateTime, endDate: ZonedDateTime?, workDayId: Long)
            : this(
        null,
        startDate,
        endDate,
        workDayId
    )

    constructor(startDate: ZonedDateTime, workDay: WorkDay)
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
