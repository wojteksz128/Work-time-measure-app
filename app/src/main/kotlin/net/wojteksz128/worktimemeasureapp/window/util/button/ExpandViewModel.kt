package net.wojteksz128.worktimemeasureapp.window.util.button

import androidx.lifecycle.MutableLiveData

open class ExpandViewModel {
    val comeEventsExpanded = MutableLiveData(false)

    fun switchExpansion() {
        val expansion = comeEventsExpanded.value ?: false
        comeEventsExpanded.value = !expansion
    }
}