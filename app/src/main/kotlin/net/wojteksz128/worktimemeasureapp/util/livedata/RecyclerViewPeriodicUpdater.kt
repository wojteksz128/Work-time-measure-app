package net.wojteksz128.worktimemeasureapp.util.livedata

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import net.wojteksz128.worktimemeasureapp.util.coroutines.PeriodicOperation

// TODO: 22.09.2021 synchronizacja z dashboard oraz WorkDayAdapter
class RecyclerViewPeriodicUpdater(private val adapter: RecyclerView.Adapter<*>, repeatMillis: Long = 1000) : ClassTagAware {
    private var counterParams = PeriodicOperation.PeriodicOperationParams(repeatMillis = repeatMillis,
        mainThreadAction = {
            Log.d(classTag, "startTimer: Update items $updatedItemsPosition in adapter ${adapter.javaClass.simpleName}")
            updatedItemsPosition.forEach { adapter.notifyItemChanged(it) }
        })
    private var workTimeCounterRunner: PeriodicOperation.PeriodicOperationRunner? = null
    private var runnerParamsSet = mutableSetOf(counterParams)

    private val updatedItemsPosition = mutableSetOf<Int>()

    fun addItem(position: Int) {
        Log.d(classTag, "addItem: Item at position $position added to updated in adapter ${adapter.javaClass.simpleName}")
        updatedItemsPosition.add(position)
        startTimer()
    }

    fun removeItem(position: Int) {
        Log.d(classTag, "removeItem: Item at position $position removed to updated in adapter ${adapter.javaClass.simpleName}")
        updatedItemsPosition.remove(position)
        if (updatedItemsPosition.isEmpty())
            stopTimer()
    }

    private fun startTimer() {
        if (workTimeCounterRunner?.isActive == true) {  // Istnieje i jest aktywny (prawdopodobnie zewnętrzny)
            Log.d(classTag, "startTimer: Attach updater params to existing runner (connected with adapter ${adapter.javaClass.simpleName})")
            workTimeCounterRunner!!.attach(counterParams)
        } else { // Jest zatrzymany lub nie istnieje
            Log.d(classTag, "startTimer: Start new runner with only my params (connected with adapter ${adapter.javaClass.simpleName})")
            workTimeCounterRunner = PeriodicOperation.start(counterParams)
            workTimeCounterRunner!!.attach(runnerParamsSet)
        }
    }

    private fun stopTimer() {
        Log.d(classTag, "stopTimer: Detach updater params from existing runner (connected with adapter ${adapter.javaClass.simpleName})")
        workTimeCounterRunner?.detach(counterParams)
    }

    fun syncWith(anotherRunner: PeriodicOperation.PeriodicOperationRunner) {
        if (workTimeCounterRunner != null) {
            workTimeCounterRunner!!.syncWith(anotherRunner)
        }
        workTimeCounterRunner = anotherRunner
    }

    fun syncWith(anotherUpdater: RecyclerViewPeriodicUpdater) {
        if (anotherUpdater.workTimeCounterRunner != null) {
            anotherUpdater.workTimeCounterRunner?.let { syncWith(it) }
        } else {
            Log.d(classTag, "syncWith(RecyclerViewPeriodicUpdater): Timers cannot be synchronized with null runner")
        }
    }
}