package net.wojteksz128.worktimemeasureapp.database

import android.arch.persistence.room.TypeConverter
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventType
import java.util.*

class DatabaseConverters {

    object DateConverter {

        @TypeConverter
        fun toDate(timestamp: Long?): Date? {
            return if (timestamp == null) null else Date(timestamp)
        }

        @TypeConverter
        fun toTimestamp(date: Date?): Long? {
            return date?.time
        }
    }

    object ComeEventTypeConverter {

        @TypeConverter
        fun toComeEventType(type: String?): ComeEventType? {
            return if (type == null) null else ComeEventType.valueOf(type)
        }

        @TypeConverter
        fun toString(eventType: ComeEventType?): String? {
            return eventType?.name
        }
    }
}
