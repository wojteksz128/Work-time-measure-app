package net.wojteksz128.worktimemeasureapp.util.dialog

import android.app.Application
import android.util.Log
import android.view.View
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
    val startTime = MutableLiveData<Date?>()
    val startTimeEditForm = ViewVisibility(false)

    val finishTime = MutableLiveData<Date?>()
    val finishTimeEditForm = ViewVisibility(false)

    fun fill(comeEvent: ComeEvent) {
        Log.d(
            classTag, "fill: Fill dialog using\n" +
                    "\tnew data = $comeEvent\n" +
                    "\told startTime = ${startTime.value}\n" +
                    "\told finishTime = ${finishTime.value}"
        )
        startTime.value = comeEvent.startDate
        finishTime.value = comeEvent.endDate
    }

    fun fillStartTimeChange() {
        startTimeEditForm.show()
    }

    // TODO: Sprawdź z ustawieniami czasu lokalnego
    fun acceptStartTimeChange() {
        // TODO: Zmień
        startTimeEditForm.hide()
    }

    fun clearStartTime() {
        startTime.value = null
    }

    fun fillFinishTimeChange() {
        finishTimeEditForm.show()
    }

    // TODO: Sprawdź z ustawieniami czasu lokalnego
    fun acceptFinishTimeChange() {
        finishTimeEditForm.hide()
    }

    fun clearFinishTime() {
        finishTime.value = null
    }
}

class ViewVisibility(defaultVisible: Boolean) {
    val visible = MutableLiveData(if (defaultVisible) View.VISIBLE else View.GONE)
    val invisible = MutableLiveData(if (defaultVisible) View.GONE else View.VISIBLE)

    fun show() {
        visible.value = View.VISIBLE
        invisible.value = View.GONE
    }

    fun hide() {
        visible.value = View.GONE
        invisible.value = View.VISIBLE
    }
}