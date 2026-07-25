package net.wojteksz128.worktimemeasureapp.window.dashboard

import net.wojteksz128.worktimemeasureapp.window.history.ComeEventItemUiModel

data class DashboardUiState(
    val standardRemainingTodayText: String = "0:00:00",
    val monthlyBalanceText: String = "0:00:00",
    val todayWorkTimeText: String = "0:00:00",
    val currentDayLabel: String = "",
    val isEventsListVisible: Boolean = false,
    val isNoEventsLabelVisible: Boolean = true,
    val isLoading: Boolean = true,
    val comeEvents: List<ComeEventItemUiModel> = emptyList(),
)
