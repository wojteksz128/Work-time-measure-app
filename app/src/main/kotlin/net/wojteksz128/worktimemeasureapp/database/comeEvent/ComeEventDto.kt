package net.wojteksz128.worktimemeasureapp.database.comeEvent

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import net.wojteksz128.worktimemeasureapp.database.EntityDto
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayDto
import java.util.*

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
    var startDate: Date,

    @ColumnInfo(name = "endDate")
    var endDate: Date?,

    @ColumnInfo(name = "duration")
    var durationLong: Long?,

    @ColumnInfo(
        name = "workDayId",
        index = true
    )
    val workDayId: Long,
) : EntityDto
