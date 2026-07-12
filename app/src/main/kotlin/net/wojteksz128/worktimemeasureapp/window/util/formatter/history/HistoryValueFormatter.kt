package net.wojteksz128.worktimemeasureapp.window.util.formatter.history

interface HistoryValueFormatter {
    fun format(fieldName: String, value: String?): String?
}