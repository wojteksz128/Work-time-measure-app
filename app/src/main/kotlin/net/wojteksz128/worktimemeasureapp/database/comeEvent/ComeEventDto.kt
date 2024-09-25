package net.wojteksz128.worktimemeasureapp.database.comeEvent

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import net.wojteksz128.worktimemeasureapp.database.EntityDto
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayDto
import org.threeten.bp.Duration
import org.threeten.bp.ZonedDateTime

@Entity(
    tableName = "come_event",
    foreignKeys = [
        ForeignKey(
            entity = WorkDayDto::class,
            parentColumns = ["id"],
            childColumns = ["workDayId"],
            onDelete = CASCADE
        )
    ]
)
data class ComeEventDto(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    override val id: Long?,

    @ColumnInfo(name = "startDate")
    var startDate: ZonedDateTime,

    @ColumnInfo(name = "endDate")
    var endDate: ZonedDateTime?,

    @ColumnInfo(name = "duration")
    var duration: Duration?,

    @ColumnInfo(
        name = "workDayId",
        index = true
    )
    val workDayId: Long,
) : EntityDto
