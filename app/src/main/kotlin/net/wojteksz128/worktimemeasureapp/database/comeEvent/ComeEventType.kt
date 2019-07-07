package net.wojteksz128.worktimemeasureapp.database.comeEvent

import net.wojteksz128.worktimemeasureapp.R

// TODO 2019-07-07: Usuń background i displayLabel, jeśli nie będą używane
@Suppress("unused")
enum class ComeEventType(val background: Int, val displayLabel: Int) {
    COME_IN(R.drawable.come_in_background, R.string.main_come_in_label),
    COME_OUT(R.drawable.come_out_background, R.string.main_come_out_label)
}
