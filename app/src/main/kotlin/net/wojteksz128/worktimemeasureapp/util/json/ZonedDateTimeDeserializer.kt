package net.wojteksz128.worktimemeasureapp.util.json

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import java.lang.reflect.Type

class ZonedDateTimeDeserializer : JsonDeserializer<ZonedDateTime> {

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext,
    ): ZonedDateTime? {
        return if (json.isJsonPrimitive)
            ZonedDateTime.parse(json.asString)
        else if (json.isJsonObject) {
            val jsonObject = json.asJsonObject
            try {
                val dateTime = jsonObject.getAsJsonObject("dateTime")
                val date = dateTime.getAsJsonObject("date")
                val time = dateTime.getAsJsonObject("time")
                val zone = jsonObject.getAsJsonObject("zone")

                ZonedDateTime.of(
                    date.get("year").asInt,
                    date.get("month").asInt,
                    date.get("day").asInt,
                    time.get("hour").asInt,
                    time.get("minute").asInt,
                    time.get("second").asInt,
                    time.get("nano").asInt,
                    ZoneId.of(zone.get("id").asString)
                )
            } catch (e: Exception) {
                throw JsonParseException("Malformed ZonedDateTime JSON object: $jsonObject", e)
            }
        } else {
            throw JsonParseException("Malformed ZonedDateTime JSON object: $json")
        }
    }
}