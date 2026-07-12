package net.wojteksz128.worktimemeasureapp.util.datetime

import org.threeten.bp.Duration
import kotlin.math.abs

fun min(a: Duration, b: Duration) = if (a < b) a else b

fun Duration.floorToSeconds(): Duration = Duration.ofSeconds(seconds)

fun Duration.ceilToSeconds(): Duration =
    if (nano == 0) Duration.ofSeconds(seconds) else Duration.ofSeconds(seconds + 1)

fun Duration?.toCounterString(defaultValue: String = "0:00:00"): String {
    if (this == null) return defaultValue

    val hours = abs(this.toHours()).toInt()
    val minutes = abs(this.toMinutesPart())
    val seconds = abs(this.toSecondsPart())
    val sign =
        if (this.isNegative && (hours != 0 || minutes != 0 || seconds != 0)) "-" else ""

    return buildString {
        append(sign)
        append(hours)
        append(":")
        append(minutes.toString().padStart(2, '0'))
        append(":")
        append(seconds.toString().padStart(2, '0'))
    }
}