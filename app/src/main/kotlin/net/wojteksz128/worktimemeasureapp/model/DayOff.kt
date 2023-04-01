package net.wojteksz128.worktimemeasureapp.model

import net.wojteksz128.worktimemeasureapp.model.fieldType.DayOffSource
import net.wojteksz128.worktimemeasureapp.model.fieldType.DayOffType
import org.threeten.bp.LocalDate
import org.threeten.bp.Month

data class DayOff(
    val id: Long?,
    val uuid: String?,
    val type: DayOffType,
    val name: String,
    val startDay: Int,
    val startMonth: Month,
    val startYear: Int?,
    val finishDay: Int,
    val finishMonth: Month,
    val finishYear: Int?,
    val source: DayOffSource,
) : DomainModel {

    val startDate: LocalDate
        get() = LocalDate.of(startYear ?: LocalDate.now().year, startMonth, startDay)

    val finishDate: LocalDate
        get() = LocalDate.of(finishYear ?: LocalDate.now().year, finishMonth, finishDay)
}
