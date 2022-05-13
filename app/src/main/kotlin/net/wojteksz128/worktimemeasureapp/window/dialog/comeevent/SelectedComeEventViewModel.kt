package net.wojteksz128.worktimemeasureapp.window.dialog.comeevent

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import net.wojteksz128.worktimemeasureapp.util.recyclerView.ViewHolderInformation
import net.wojteksz128.worktimemeasureapp.window.history.ComeEventsAdapter.ComeEventViewHolder
import javax.inject.Inject

@HiltViewModel
class SelectedComeEventViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application), ClassTagAware {
    var viewHolderInformation: ViewHolderInformation<ComeEventViewHolder>? = null
    val selected = MutableLiveData<ComeEvent>()

    fun select(comeEvent: ComeEvent) {
        select(comeEvent, null)
    }

    fun select(
        comeEvent: ComeEvent,
        viewHolderInformation: ViewHolderInformation<ComeEventViewHolder>?
    ) {
        Log.d(
            classTag, "select: Selection changed\n" +
                    "\tOld selected come event: ${selected.value}\n" +
                    "\tNew selected come event: $comeEvent\n" +
                    "\tviewHolderInformation: $viewHolderInformation"
        )
        selected.value = comeEvent
        this.viewHolderInformation = viewHolderInformation
    }
}