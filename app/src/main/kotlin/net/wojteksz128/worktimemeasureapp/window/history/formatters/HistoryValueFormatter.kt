package net.wojteksz128.worktimemeasureapp.window.history.formatters

interface HistoryValueFormatter {
    fun format(fieldName: String, value: String?): String?
}