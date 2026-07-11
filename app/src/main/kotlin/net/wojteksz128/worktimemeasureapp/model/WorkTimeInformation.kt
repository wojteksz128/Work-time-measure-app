package net.wojteksz128.worktimemeasureapp.model

import net.wojteksz128.worktimemeasureapp.util.datetime.ceilToSeconds
import org.threeten.bp.Duration
import org.threeten.bp.ZonedDateTime

data class WorkTimeInformation(
    val requiredTime: Duration,
    private val workTimeSupplier: () -> Duration,
    private val currentTimeSupplier: () -> ZonedDateTime,
) {

    val remainingWorkTime: Duration
        get() = (requiredTime - workTimeSupplier()).ceilToSeconds()

    val endTime: ZonedDateTime
        get() = currentTimeSupplier() + remainingWorkTime
}