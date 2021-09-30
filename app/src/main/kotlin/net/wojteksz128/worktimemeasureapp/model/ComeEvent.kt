package net.wojteksz128.worktimemeasureapp.model

import org.threeten.bp.Duration
import java.util.*

data class ComeEvent(
    val id: Long?,
    var startDate: Date,
    var endDate: Date,
    var duration: Duration,
    val workDayId: Long
)