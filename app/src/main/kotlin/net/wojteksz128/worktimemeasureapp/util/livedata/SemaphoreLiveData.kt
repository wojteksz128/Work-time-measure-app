package net.wojteksz128.worktimemeasureapp.util.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import java.util.concurrent.atomic.AtomicInteger

class SemaphoreLiveData(private val permits: Int, vararg sources: LiveData<Boolean>) :
    MediatorLiveData<Boolean>() {
    private val counter = AtomicInteger(0)

    init {
        sources.forEach { source ->
            addSource(source) { sourceValue ->
                if (sourceValue)
                    counter.incrementAndGet()
                else {
                    counter.decrementAndGet()
                    if (counter.get() < 0)
                        counter.set(0)
                }
                this.value = counter.get() < permits
            }
        }
    }
}