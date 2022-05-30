package net.wojteksz128.worktimemeasureapp.model.fieldType

import net.wojteksz128.worktimemeasureapp.model.DayOff

class DayType private constructor(val isDayOff: Boolean, val dayOffInfo: DayOff? = null) {

    companion object {
        val WorkDay = DayType(false)
        val Weekend = DayType(true)

        fun ofDayOff(name: DayOff) = DayType(true, name)
    }
}