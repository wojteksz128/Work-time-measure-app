package net.wojteksz128.worktimemeasureapp.model.fieldType

class DayType private constructor(val isDayOff: Boolean, val name: String? = null) {

    companion object {
        val WorkDay = DayType(false)
        val Weekend = DayType(true)

        fun ofDayOff(name: String) = DayType(true, name)
    }
}