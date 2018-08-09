package net.wojteksz128.worktimemeasureapp.database;

import android.arch.persistence.room.TypeConverter;

import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventType;

import java.util.Date;

public class DatabaseConverters {

    public static class DateConverter {

        @TypeConverter
        public static Date toDate(Long timestamp) {
            return timestamp == null ? null : new Date(timestamp);
        }

        @TypeConverter
        public static Long toTimestamp(Date date) {
            return date == null ? null : date.getTime();
        }
    }

    public static class ComeEventTypeConverter {

        @TypeConverter
        public static ComeEventType toComeEventType(String type) {
            return type == null ? null : ComeEventType.valueOf(type);
        }

        @TypeConverter
        public static String toString(ComeEventType eventType) {
            return eventType == null ? null : eventType.name();
        }
    }
}
