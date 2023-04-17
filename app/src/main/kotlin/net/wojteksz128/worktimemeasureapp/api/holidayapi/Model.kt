package net.wojteksz128.worktimemeasureapp.api.holidayapi

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import net.wojteksz128.worktimemeasureapp.util.json.IntStringJsonAdapter
import java.util.*

data class HolidayApiHolidaysResponse(
    val holidays: Set<HolidayApiHoliday>,
    override val status: Int,
    override val requests: HolidayApiUsage,
    override val error: String?,
    override val warning: String?
) : HolidayApiResponse

data class HolidayApiHoliday(
    val name: String,
    val date: Date,
    val observed: Date,
    val public: Boolean,
    val country: String,
    val uuid: String,
    val weekday: HolidayApiWeekday,
    val subdivisions: Set<String>? = null
)

data class HolidayApiWeekday(
    val date: HolidayApiWeekdayInfo,
    val observed: HolidayApiWeekdayInfo
)

data class HolidayApiWeekdayInfo(
    val name: String,
    val numeric: String
)

data class HolidayApiCountriesResponse(
    val countries: Set<HolidayApiCountry>,
    override val status: Int,
    override val requests: HolidayApiUsage,
    override val error: String?,
    override val warning: String?
) : HolidayApiResponse

data class HolidayApiCountry(
    val code: String,
    val name: String,
    val codes: HolidayApiCountryCodes,
    val languages: Set<String>,
    val flag: String,
    val subdivisions: Set<HolidayApiSubdivision>
)

data class HolidayApiCountryCodes(
    @SerializedName("alpha-2")
    val alpha_2: String,
    @SerializedName("alpha-3")
    val alpha_3: String,
    @JsonAdapter(IntStringJsonAdapter::class)
    val numeric: Int
)

data class HolidayApiSubdivision(
    val code: String,
    val name: String,
    val languages: Set<String>
)

data class HolidayApiLanguageResponse(
    val languages: Set<HolidayApiLanguage>,
    override val status: Int,
    override val requests: HolidayApiUsage,
    override val error: String?,
    override val warning: String?
) : HolidayApiResponse

data class HolidayApiLanguage(
    val code: String,
    val name: String
)

data class HolidayApiWorkdayResponse(
    val date: Date,
    val weekday: HolidayApiWeekdayInfo,
    override val status: Int,
    override val requests: HolidayApiUsage,
    override val error: String?,
    override val warning: String?,
) : HolidayApiResponse

data class HolidayApiWorkdaysResponse(
    val workdays: Int,
    override val status: Int,
    override val requests: HolidayApiUsage,
    override val error: String?,
    override val warning: String?,
) : HolidayApiResponse

data class HolidayApiErrorResponse(
    override val status: Int,
    override val requests: HolidayApiUsage,
    override val error: String?,
    override val warning: String?,
) : HolidayApiResponse

interface HolidayApiResponse {
    val status: Int
    val requests: HolidayApiUsage
    val error: String?
    val warning: String?
}

data class HolidayApiUsage(
    val available: Int,
    val used: Int,
    val resets: Date,
)
