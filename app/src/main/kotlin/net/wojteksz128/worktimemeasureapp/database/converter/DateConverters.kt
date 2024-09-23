package net.wojteksz128.worktimemeasureapp.database.converter

import androidx.room.TypeConverter
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import java.util.Date

class DateConverters {

    private val localDateFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @TypeConverter
    fun toDate(localDateTimeString: String?): Date? = localDateTimeString?.let {
        val localDateTime = LocalDateTime.parse(localDateTimeString, localDateFormatter)
        val instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant()
        Date(instant.toEpochMilli())
    }

    @TypeConverter
    fun toTimestamp(date: Date?): String? = date?.let {
        Instant.ofEpochMilli(it.time).atZone(ZoneId.systemDefault()).format(localDateFormatter)
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? = dateString?.let {
        LocalDate.parse(dateString)
    }

    @TypeConverter
    fun toLocalDateString(date: LocalDate?): String? = date?.toString()
}