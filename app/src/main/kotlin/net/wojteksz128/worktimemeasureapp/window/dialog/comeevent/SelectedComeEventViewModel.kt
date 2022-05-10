package net.wojteksz128.worktimemeasureapp.window.dialog.comeevent

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import javax.inject.Inject

@HiltViewModel
class SelectedComeEventViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application), ClassTagAware {
    val selected = MutableLiveData<ComeEvent>()

    fun select(comeEvent: ComeEvent) {
        Log.d(
            classTag, "select: Selection changed\n" +
                    "\tOld selected come event: ${selected.value}\n" +
                    "\tNew selected come event: $comeEvent"
        )
        selected.value = comeEvent
    }
}