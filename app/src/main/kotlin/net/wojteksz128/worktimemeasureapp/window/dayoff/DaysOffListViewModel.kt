package net.wojteksz128.worktimemeasureapp.window.dayoff

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import net.wojteksz128.worktimemeasureapp.model.DayOff
import net.wojteksz128.worktimemeasureapp.repository.DayOffRepository
import javax.inject.Inject

@HiltViewModel
class DaysOffListViewModel @Inject constructor(
    application: Application,
    dayOffRepository: DayOffRepository,
) : AndroidViewModel(application) {
    val daysOff: LiveData<List<DayOff>>

    init {
        daysOff = dayOffRepository.getAllInLiveData()
    }
}
