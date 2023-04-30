package net.wojteksz128.worktimemeasureapp.model

import net.wojteksz128.worktimemeasureapp.model.fieldType.DayOffSource
import net.wojteksz128.worktimemeasureapp.model.fieldType.DayOffType
import org.threeten.bp.LocalDate

data class DayOff(
    val id: Long?,
    val uuid: String?,
    val type: DayOffType,
    val name: String,
    val startDate: LocalDate,
    val finishDate: LocalDate,
    val source: DayOffSource,
) : DomainModel {

    /*val startDate: LocalDate
        get() = LocalDate.of(startYear ?: LocalDate.now().year, startMonth, startDay)

    val finishDate: LocalDate
        get() = LocalDate.of(finishYear ?: LocalDate.now().year, finishMonth, finishDay)*/
}
