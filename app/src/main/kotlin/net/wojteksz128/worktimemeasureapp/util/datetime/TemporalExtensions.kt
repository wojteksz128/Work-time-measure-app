package net.wojteksz128.worktimemeasureapp.util.datetime

import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.jdk8.DefaultInterfaceTemporal

fun DefaultInterfaceTemporal?.formatToString(
    formatPattern: String,
    timeZone: ZoneId = ZoneId.systemDefault(),
    defaultValue: String = "",
): String {
    if (this == null) return defaultValue

    val formatter = DateTimeFormatter.ofPattern(formatPattern).withZone(timeZone)
    return formatter.format(this)
}