package net.wojteksz128.worktimemeasureapp.util.json

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.lang.reflect.Type

class LocalDateTimeJsonDeserializer : JsonDeserializer<LocalDateTime> {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext,
    ): LocalDateTime? = LocalDateTime.parse(json.asString, formatter)
}