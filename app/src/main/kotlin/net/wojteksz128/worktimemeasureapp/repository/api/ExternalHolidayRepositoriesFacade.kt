package net.wojteksz128.worktimemeasureapp.repository.api

import net.wojteksz128.worktimemeasureapp.api.HolidayProvider

class ExternalHolidayRepositoriesFacade(
    private val holidayApiRepository: HolidayApiRepository,
    private val nagerDateApiV3Repository: NagerDateApiV3Repository,
) {

    @Suppress("REDUNDANT_ELSE_IN_WHEN")
    fun forAPI(holidayProvider: HolidayProvider): ExternalHolidayRepository =
        when (holidayProvider) {
            HolidayProvider.HolidayAPI -> holidayApiRepository
            HolidayProvider.NagerDateAPI -> nagerDateApiV3Repository
            else -> throw IllegalArgumentException("External holiday provider cannot be recognized.")
        }
}

