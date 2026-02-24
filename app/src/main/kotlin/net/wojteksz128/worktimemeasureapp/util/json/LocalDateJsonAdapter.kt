package net.wojteksz128.worktimemeasureapp.util.json

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.lang.reflect.Type

class LocalDateJsonAdapter : JsonDeserializer<LocalDate> {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext,
    ): LocalDate? = LocalDate.parse(json.asString, formatter)
}
