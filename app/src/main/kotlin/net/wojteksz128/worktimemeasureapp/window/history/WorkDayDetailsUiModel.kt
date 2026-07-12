package net.wojteksz128.worktimemeasureapp.window.history

data class WorkDayDetailsUiModel(
    val yearAndMonth: String = "",
    val day: String = "",
    val dayOfWeek: String = "",
    val durationText: String = "",
    val expectedDurationText: String = "",
    val comeEvents: List<ComeEventItemUiModel> = emptyList(),
    val historyItems: List<HistoryDisplayItem> = emptyList(),
    val isEventsListVisible: Boolean = false,
    val isNoEventsLabelVisible: Boolean = false,
    val isHistoryListVisible: Boolean = false,
    val isNoHistoryLabelVisible: Boolean = false,
)
