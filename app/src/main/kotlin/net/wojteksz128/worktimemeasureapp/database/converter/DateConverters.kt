package net.wojteksz128.worktimemeasureapp.database.converter

import androidx.room.TypeConverter
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.util.Date

class DateConverters {

    private val localDateFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    private val storeTimeZone = ZoneId.of("UTC")

    @TypeConverter
    fun toDate(localDateTimeString: String?): Date? = localDateTimeString?.let {
        val localDateTime = LocalDateTime.parse(localDateTimeString, localDateFormatter)
        val zonedDateTime = localDateTime.atZone(storeTimeZone)
        val instant = zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).toInstant()
        Date(instant.toEpochMilli())
    }

    @TypeConverter
    fun toTimestamp(date: Date?): String? = date?.let {
        Instant.ofEpochMilli(it.time).atZone(ZoneId.systemDefault())
            .withZoneSameInstant(storeTimeZone).format(localDateFormatter)
    }

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