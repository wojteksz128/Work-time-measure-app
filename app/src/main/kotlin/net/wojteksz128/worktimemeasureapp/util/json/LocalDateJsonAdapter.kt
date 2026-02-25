package net.wojteksz128.worktimemeasureapp.util.json

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.lang.reflect.Type

class LocalDateJsonAdapter : JsonDeserializer<LocalDate>, JsonSerializer<LocalDate> {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    override fun serialize(
        src: LocalDate?,
        typeOfSrc: Type,
        context: JsonSerializationContext,
    ): JsonElement = JsonPrimitive(src?.format(formatter))

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext,
    ): LocalDate? = LocalDate.parse(json.asString, formatter)
}
