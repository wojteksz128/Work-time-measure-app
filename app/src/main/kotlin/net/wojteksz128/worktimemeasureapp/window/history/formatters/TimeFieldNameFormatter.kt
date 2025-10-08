package net.wojteksz128.worktimemeasureapp.window.history.formatters

import android.util.Log
import com.google.gson.Gson
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import org.threeten.bp.ZonedDateTime
import javax.inject.Inject

class TimeFieldNameFormatter @Inject constructor(
    private val dateTimeUtils: DateTimeUtils,
    private val gson: Gson,
    private val dateTimeFormat: String,
) : FieldNameFormatter, ClassTagAware {

    override fun format(value: String?): String? {
        if (value == null || value == "null") return null

        try {
            return dateTimeUtils.formatDate(dateTimeFormat, parseDateTime(value))
        } catch (e: Exception) {
            Log.w(classTag, "format: error parsing date field: $value", e)
            return value
        }
    }

    private fun parseDateTime(value: String): ZonedDateTime =
        runCatching { ZonedDateTime.parse(value) }.getOrElse {
            gson.fromJson(
                value,
                ZonedDateTime::class.java
            )
        }
}