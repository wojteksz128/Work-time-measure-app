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
    val startTimeHour = MutableLiveData<Int>()
    val startTimeMinute = MutableLiveData<Int>()
    val startTimeSecond = MutableLiveData<Int>()
    val startTimeIsAm = MutableLiveData<Boolean>()

    val finishTime = MutableLiveData<Date?>()
    val finishTimeEditForm = ViewVisibility(false)
    val finishTimeHour = MutableLiveData<Int>()
    val finishTimeMinute = MutableLiveData<Int>()
    val finishTimeSecond = MutableLiveData<Int>()
    val finishTimeIsAm = MutableLiveData<Boolean>()

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
        val calendar = Calendar.getInstance()
        startTime.value?.let { calendar.time = it }

        startTimeHour.value = calendar.get(Calendar.HOUR_OF_DAY)
        startTimeMinute.value = calendar.get(Calendar.MINUTE)
        startTimeSecond.value = calendar.get(Calendar.SECOND)
        startTimeIsAm.value = calendar.get(Calendar.AM_PM) == Calendar.AM
        startTimeEditForm.show()
    }

    // TODO: Sprawdź z ustawieniami czasu lokalnego
    fun acceptStartTimeChange() {
        startTime.value?.let {
            it.hours = startTimeHour.value!!
            it.minutes = startTimeMinute.value!!
            it.seconds = startTimeSecond.value!!
        }
        startTimeEditForm.hide()
    }

    fun clearStartTime() {
        startTime.value = null
    }

    fun setStartTimeIsAm(value: Boolean) {
        startTimeIsAm.value = value
    }

    fun fillFinishTimeChange() {
        val calendar = Calendar.getInstance()
        finishTime.value?.let { calendar.time = it }

        finishTimeHour.value = calendar.get(Calendar.HOUR_OF_DAY)
        finishTimeMinute.value = calendar.get(Calendar.MINUTE)
        finishTimeSecond.value = calendar.get(Calendar.SECOND)
        finishTimeIsAm.value = calendar.get(Calendar.AM_PM) == Calendar.AM
        finishTimeEditForm.show()
    }

    // TODO: Sprawdź z ustawieniami czasu lokalnego
    fun acceptFinishTimeChange() {
        finishTime.value?.let {
            it.hours = finishTimeHour.value!!
            it.minutes = finishTimeMinute.value!!
            it.seconds = finishTimeSecond.value!!
        }
        finishTimeEditForm.hide()
    }

    fun clearFinishTime() {
        finishTime.value = null
    }

    fun setFinishTimeIsAm(value: Boolean) {
        startTimeIsAm.value = value
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