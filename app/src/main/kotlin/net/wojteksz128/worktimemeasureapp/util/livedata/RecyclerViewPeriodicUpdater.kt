package net.wojteksz128.worktimemeasureapp.util.livedata

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import net.wojteksz128.worktimemeasureapp.util.coroutines.PeriodicOperation

// TODO: 22.09.2021 synchronizacja z dashboard oraz WorkDayAdapter
class RecyclerViewPeriodicUpdater(private val repeatMillis: Long = 1000) : ClassTagAware {
    private var workTimeCounterRunner: PeriodicOperation.PeriodicOperationRunner? = null
    private val updatedItemsPosition = mutableSetOf<Int>()

    fun addItem(position: Int, adapter: RecyclerView.Adapter<*>) {
        Log.d(classTag, "addItem: Item at position $position added to updated in adapter ${adapter.javaClass.simpleName}")
        updatedItemsPosition.add(position)
        startTimer(adapter)
    }

    fun removeItem(position: Int, adapter: RecyclerView.Adapter<*>) {
        Log.d(classTag, "removeItem: Item at position $position removed to updated in adapter ${adapter.javaClass.simpleName}")
        updatedItemsPosition.remove(position)
        if (updatedItemsPosition.isEmpty())
            stopTimer(adapter)
    }

    private fun startTimer(adapter: RecyclerView.Adapter<*>) {
        if (workTimeCounterRunner?.timer?.isActive != true) {
            Log.d(classTag, "startTimer: Updater starts for adapter ${adapter.javaClass.simpleName}")
            val params = PeriodicOperation.PeriodicOperationParams(repeatMillis = repeatMillis,
                mainThreadAction = {
                    Log.d(classTag, "startTimer: Update items $updatedItemsPosition in adapter ${adapter.javaClass.simpleName}")
                    updatedItemsPosition.forEach { adapter.notifyItemChanged(it) }
                })
            workTimeCounterRunner = PeriodicOperation.start(params)
        }
    }

    private fun stopTimer(adapter: RecyclerView.Adapter<*>) {
        Log.d(classTag, "stopTimer: Updater stops for adapter ${adapter.javaClass.simpleName}")
        workTimeCounterRunner?.let { PeriodicOperation.cancel(it) }
    }
}