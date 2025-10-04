package net.wojteksz128.worktimemeasureapp.window.dialog.comeevent

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.repository.WorkDayRepository
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import net.wojteksz128.worktimemeasureapp.util.datetime.toDate
import net.wojteksz128.worktimemeasureapp.util.datetime.toZonedDateTime
import net.wojteksz128.worktimemeasureapp.util.livedata.SemaphoreLiveData
import org.threeten.bp.LocalDate
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class EditComeEventDialogViewModel @Inject constructor(
    application: Application,
    private val workDayRepository: WorkDayRepository,
) : AndroidViewModel(application), ClassTagAware {
    private lateinit var comeEventToModify: ComeEvent
    val startTime = MutableLiveData<Date?>()
    val startTimeInEditMode = MutableLiveData(false)

    val finishTime = MutableLiveData<Date?>()
    val finishTimeInEditMode = MutableLiveData(false)

    val workDayDate = MutableLiveData<LocalDate?>()

    val positiveButtonEnabled = SemaphoreLiveData(1, startTimeInEditMode, finishTimeInEditMode)

    suspend fun fill(comeEvent: ComeEvent) {
        Log.d(
            classTag, "fill: Fill dialog using\n" +
                    "\tnew data = $comeEvent\n" +
                    "\told startTime = ${startTime.value}\n" +
                    "\told finishTime = ${finishTime.value}"
        )
        comeEventToModify = comeEvent
        startTime.value = comeEvent.startDate.toDate()
        startTimeInEditMode.value = false
        finishTime.value = comeEvent.endDate?.toDate()
        finishTimeInEditMode.value = false
        workDayDate.value = workDayRepository.getWorkDayById(comeEvent.workDayId)?.date
    }

    fun prepareModified(): ComeEvent {
        return comeEventToModify.copy(
            startDate = startTime.value!!.toZonedDateTime(),
            endDate = finishTime.value?.toZonedDateTime()
        )
    }
}

