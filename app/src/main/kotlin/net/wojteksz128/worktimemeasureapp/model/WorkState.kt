package net.wojteksz128.worktimemeasureapp.model

import net.wojteksz128.worktimemeasureapp.util.datetime.WorkTimeBalance

data class WorkState(
    val workDay: WorkDay,
    val workTimeBalance: WorkTimeBalance,
)