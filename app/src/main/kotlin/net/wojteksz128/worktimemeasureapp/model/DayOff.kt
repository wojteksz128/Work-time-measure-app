package net.wojteksz128.worktimemeasureapp.model

import net.wojteksz128.worktimemeasureapp.model.fieldType.DayOffSource
import net.wojteksz128.worktimemeasureapp.model.fieldType.DayOffType
import org.threeten.bp.Month

data class DayOff(
    val id: Long?,
    val type: DayOffType,
    val startDay: Int,
    val startMonth: Month,
    val startYear: Int?,
    val finishDay: Int,
    val finishMonth: Int,
    val finishYear: Int?,
    val source: DayOffSource
) : DomainModel
