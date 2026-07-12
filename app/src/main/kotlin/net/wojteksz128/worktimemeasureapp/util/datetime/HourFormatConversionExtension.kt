package net.wojteksz128.worktimemeasureapp.util.datetime

fun convert12To24HourFormat(hour: Int, amPm: AmPm): Int = hour % 12 + when (amPm) {
    AmPm.AM -> 0
    AmPm.PM -> 12
}

fun convert24To12HourFormat(hour: Int): Pair<Int, AmPm> = when (hour) {
    0 -> Pair(12, AmPm.AM)
    in 1..11 -> Pair(hour, AmPm.AM)
    12 -> Pair(12, AmPm.PM)
    in 13..23 -> Pair(hour - 12, AmPm.PM)
    else -> throw IllegalArgumentException("Invalid 24-hour format hour: $hour")
}

enum class AmPm {
    AM, PM
}