package net.wojteksz128.worktimemeasureapp.util.json

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

class IntStringJsonAdapter : TypeAdapter<Int>() {
    override fun write(jsonWriter: JsonWriter?, value: Int?) {
        jsonWriter?.let {
            if (value != null)
                it.value(value.toString())
            else
                it.nullValue()
        }
    }

    override fun read(jsonReader: JsonReader?): Int {
        return jsonReader?.let {
            val nextString = it.nextString()
            if (nextString != null && nextString.isNotEmpty())
                nextString.toInt()
            else
                0
        } ?: 0
    }
}
