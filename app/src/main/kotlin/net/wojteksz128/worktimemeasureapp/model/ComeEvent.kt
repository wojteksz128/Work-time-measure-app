package net.wojteksz128.worktimemeasureapp.model

import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import org.threeten.bp.Duration
import java.util.*

data class ComeEvent(
    val id: Long?,
    var startDate: Date,
    var endDate: Date?,
    var durationMillis: Long?,
    val workDayId: Long,
) : Comparable<ComeEvent> {
    val isEnded: Boolean
        get() = endDate != null

    val duration: Duration
        get() = Duration.ofMillis(((endDate ?: DateTimeProvider.currentTime) - startDate).time)

    constructor(startDate: Date, endDate: Date?, workDayId: Long)
            : this(null, startDate, endDate, endDate?.let { (it - startDate).time }, workDayId)

    constructor(startDate: Date, workDay: WorkDay)
            : this(startDate, null, workDay.id!!)

    // TODO: 30.09.2021 Verify it is necessary
    override fun compareTo(other: ComeEvent): Int {
        return (startDate - other.startDate).time.toInt()
    }

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

private operator fun Date.minus(other: Date): Date =
    Date(this.time - other.time)
