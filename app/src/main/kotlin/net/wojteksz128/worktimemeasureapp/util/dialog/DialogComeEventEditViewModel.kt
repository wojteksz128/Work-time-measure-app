package net.wojteksz128.worktimemeasureapp.util.dialog

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DialogComeEventEditViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application), ClassTagAware {
    private lateinit var comeEventToModify: ComeEvent

    val startTime = MutableLiveData<Date?>()

    val finishTime = MutableLiveData<Date?>()

    fun fill(comeEvent: ComeEvent) {
        Log.d(
            classTag, "fill: Fill dialog using\n" +
                    "\tnew data = $comeEvent\n" +
                    "\told startTime = ${startTime.value}\n" +
                    "\told finishTime = ${finishTime.value}"
        )
        comeEventToModify = comeEvent
        startTime.value = comeEvent.startDate
        finishTime.value = comeEvent.endDate
    }

    fun prepareModified(): ComeEvent {
        return comeEventToModify.copy(
            startDate = startTime.value!!,
            endDate = finishTime.value,
            durationMillis = finishTime.value?.let { it.time - startTime.value!!.time })
    }
}

