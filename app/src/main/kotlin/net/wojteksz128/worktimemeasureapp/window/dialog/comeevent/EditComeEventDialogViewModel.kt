package net.wojteksz128.worktimemeasureapp.window.dialog.comeevent

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.repository.WorkDayRepository
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.datetime.isTheSameDay
import net.wojteksz128.worktimemeasureapp.util.livedata.SemaphoreLiveData
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class EditComeEventDialogViewModel @Inject constructor(
    application: Application,
    private val workDayRepository: WorkDayRepository,
    private val dateTimeProvider: DateTimeProvider,
) : AndroidViewModel(application), ClassTagAware {
    private lateinit var comeEventToModify: ComeEvent
    val startTime = MutableLiveData<LocalDateTime?>()
    val startTimeInEditMode = MutableLiveData(false)

    val finishTime = MutableLiveData<LocalDateTime?>()
    val finishTimeInEditMode = MutableLiveData(false)

    val workDayDate = MutableLiveData<LocalDate?>()

    val useFullFormat = MediatorLiveData<Boolean>().apply {
        val updater = {
            val start = startTime.value
            val finish = finishTime.value
            value = if (start != null && finish != null)
                !start.isTheSameDay(finish)
            else false
        }

        addSource(startTime) { updater() }
        addSource(finishTime) { updater() }
    }

    val positiveButtonEnabled = SemaphoreLiveData(1, startTimeInEditMode, finishTimeInEditMode)

    suspend fun fill(comeEvent: ComeEvent) {
        Log.d(
            classTag, "fill: Fill dialog using\n" +
                    "\tnew data = $comeEvent\n" +
                    "\told startTime = ${startTime.value}\n" +
                    "\told finishTime = ${finishTime.value}"
        )
        comeEventToModify = comeEvent
        startTime.value = comeEvent.startDate.toLocalDateTime()
        startTimeInEditMode.value = false
        finishTime.value = comeEvent.endDate?.toLocalDateTime()
        finishTimeInEditMode.value = false
        workDayDate.value = workDayRepository.getWorkDayById(comeEvent.workDayId)?.date
    }

    fun prepareModified(): ComeEvent {
        return comeEventToModify.copy(
            startDate = startTime.value!!.atZone(dateTimeProvider.currentTimeZone),
            endDate = finishTime.value?.atZone(dateTimeProvider.currentTimeZone)
        )
    }
}

