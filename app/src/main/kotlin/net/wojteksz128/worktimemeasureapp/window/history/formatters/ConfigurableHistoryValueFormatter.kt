package net.wojteksz128.worktimemeasureapp.window.history.formatters

class ConfigurableHistoryValueFormatter(
    private val fieldFormatters: Map<String, FieldNameFormatter>,
) : HistoryValueFormatter {

    override fun format(fieldName: String, value: String?): String? {
        if (DEFAULT_HIDDEN_FIELDS.contains(fieldName)) return null

        val formatter = fieldFormatters[fieldName]

        return formatter?.format(value) ?: value
    }

    companion object {
        private val DEFAULT_HIDDEN_FIELDS = setOf("id", "changeGroupId", "entityId", "workDayId")
    }
}