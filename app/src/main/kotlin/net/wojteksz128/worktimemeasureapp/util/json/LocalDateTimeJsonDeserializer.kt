package net.wojteksz128.worktimemeasureapp.util.json

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.lang.reflect.Type

class LocalDateTimeJsonDeserializer : JsonDeserializer<LocalDateTime>,
    JsonSerializer<LocalDateTime> {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    override fun serialize(
        src: LocalDateTime?,
        typeOfSrc: Type,
        context: JsonSerializationContext,
    ): JsonElement = JsonPrimitive(src?.format(formatter))

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext,
    ): LocalDateTime? = LocalDateTime.parse(json.asString, formatter)
}