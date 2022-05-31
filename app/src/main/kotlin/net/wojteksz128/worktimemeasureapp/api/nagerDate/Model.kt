package net.wojteksz128.worktimemeasureapp.api.nagerDate

import java.util.*

data class NagerDateCountryInfoDto(
    val commonName: String?,
    val officialName: String?,
    val countryCode: String?,
    val region: String?,
    val borders: Set<String>
)

data class NagerDateCountryV3Dto(
    val countryCode: String?,
    val name: String?
)

data class NagerDateLongWeekendV3Dto(
    val startDate: Date,
    val endDate: Date,
    val dayCount: Int,
    val needBridgeDay: Boolean
)

data class NagerDatePublicHolidayV3Dto(
    val date: Date,
    val localName: String?,
    val name: String?,
    val countryCode: String?,
    val fixed: Boolean,
    val global: Boolean,
    val counties: Set<String>?,
    val launchYear: Int?,
    val types: Set<NagerDatePublicHolidayType>?
)

enum class NagerDatePublicHolidayType {
    Public,
    Bank,
    School,
    Authorities,
    Optional,
    Observance
}

object NagerDateIsTodayPublicHolidayMapper {

    fun mapResponseTo(responseCode: Int) =
        when (responseCode) {
            200 -> true
            204 -> false
            else -> throw IllegalStateException("Unexpected Error")
        }
}
