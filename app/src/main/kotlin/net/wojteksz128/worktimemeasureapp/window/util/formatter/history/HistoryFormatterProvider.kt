package net.wojteksz128.worktimemeasureapp.window.util.formatter.history

import javax.inject.Inject

class HistoryFormatterProvider @Inject constructor(
    private val timeFormatter: TimeFieldNameFormatter,
    private val hiddenFormatter: HiddenFieldNameFormatter,
) {

    fun getFormatter(entityType: String): HistoryValueFormatter {
        val config = when (entityType) {
            "ComeEventDto" -> mapOf(
                "startDate" to timeFormatter,
                "endDate" to timeFormatter,
            )

            "WorkDayDto" -> mapOf(
                "date" to hiddenFormatter,
                "beginSlot" to timeFormatter,
                "endSlot" to timeFormatter,
            )

            else -> emptyMap()
        }
        return ConfigurableHistoryValueFormatter(config)
    }
}