package net.wojteksz128.worktimemeasureapp.database.comeEvent

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDay
import net.wojteksz128.worktimemeasureapp.util.DateTimeProvider
import org.threeten.bp.Duration
import java.util.*

@Entity(tableName = "come_event"
        , foreignKeys = [ForeignKey(entity = WorkDay::class, parentColumns = ["id"], childColumns = ["workDayId"], onDelete = CASCADE)])
class ComeEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Long?,
    var startDate: Date,
    var endDate: Date?,
    @ColumnInfo(name = "duration")
    var durationLong: Long?,
    @ColumnInfo(index = true)
    val workDayId: Long,
) : Comparable<ComeEvent> {

    var duration: Duration
        get() = Duration.ofMillis(durationLong ?: DateTimeProvider.currentTime.time - startDate.time)
        set(value) {
            durationLong = value.toMillis()
        }

    val isEnded: Boolean
        get() = this.endDate != null && this.durationLong != null

    @Ignore
    constructor(startDate: Date, endDate: Date?, duration: Long?, workDayId: Long)
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
