package net.wojteksz128.worktimemeasureapp.database.converter

import androidx.room.TypeConverter
import org.threeten.bp.LocalDate
import java.util.Date

class DateConverters {

    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return if (timestamp == null) null else Date(timestamp)
    }

    @TypeConverter
    fun toTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? =
        dateString?.let {
            LocalDate.parse(dateString)
        }

    @TypeConverter
    fun toLocalDateString(date: LocalDate?): String? =
        date?.toString()
}