package net.wojteksz128.worktimemeasureapp.database.workDay

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "work_day")
data class WorkDayDto(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        val id: Long?,

        @ColumnInfo(name = "date")
        var date: Date,

        @ColumnInfo(name = "beginSlot")
        var beginSlot: Date,

        @ColumnInfo(name = "endSlot")
        var endSlot: Date
)
