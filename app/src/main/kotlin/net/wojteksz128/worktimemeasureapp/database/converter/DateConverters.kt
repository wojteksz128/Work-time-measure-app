package net.wojteksz128.worktimemeasureapp.database.converter

import androidx.room.TypeConverter
import org.threeten.bp.LocalDate
import org.threeten.bp.ZonedDateTime

class DateConverters {

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? = dateString?.let {
        LocalDate.parse(dateString)
    }

    @TypeConverter
    fun toLocalDateString(date: LocalDate?): String? = date?.toString()

    @TypeConverter
    fun toZonedDateTime(dateString: String?): ZonedDateTime? = dateString?.let {
        ZonedDateTime.parse(dateString)
    }

    @TypeConverter
    fun toZonedDateTimeString(date: ZonedDateTime?): String? = date?.toString()

}