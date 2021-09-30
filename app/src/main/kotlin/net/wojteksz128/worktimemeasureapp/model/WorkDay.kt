package net.wojteksz128.worktimemeasureapp.model

import java.util.*

data class WorkDay(
    val id: Long?,
    var date: Date,
    var beginSlot: Date,
    var endSlot: Date,
    val events: MutableSet<ComeEvent>
)
