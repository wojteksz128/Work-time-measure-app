package net.wojteksz128.worktimemeasureapp.window.history.formatters

import javax.inject.Inject

class HiddenFieldNameFormatter @Inject constructor() : FieldNameFormatter {
    override fun format(value: String?): String? = null
}