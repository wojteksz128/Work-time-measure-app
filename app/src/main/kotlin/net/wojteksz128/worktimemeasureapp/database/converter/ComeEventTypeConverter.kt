package net.wojteksz128.worktimemeasureapp.database.converter

import android.arch.persistence.room.TypeConverter
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventType

class ComeEventTypeConverter {

    @TypeConverter
    fun toComeEventType(type: String?): ComeEventType? {
        return if (type == null) null else ComeEventType.valueOf(type)
    }

    @TypeConverter
    fun toString(eventType: ComeEventType?): String? {
        return eventType?.name
    }
}