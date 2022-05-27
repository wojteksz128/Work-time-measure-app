package net.wojteksz128.worktimemeasureapp.model.fieldType

enum class DayType(val isDayOff: Boolean) {
    WorkDay(false),
    Weekend(true)
}