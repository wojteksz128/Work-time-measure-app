package net.wojteksz128.worktimemeasureapp.util.coroutines

import android.util.Log
import kotlinx.coroutines.*
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware

object PeriodicOperation : ClassTagAware {

    fun start(params: PeriodicOperationParams): PeriodicOperationRunner {
        val workTimeTimerRunner = PeriodicOperationRunner(params)

        if (!workTimeTimerRunner.isActive) {
            Log.d(classTag, "startTimer: start work time counter")
            workTimeTimerRunner.start()
        }

        return workTimeTimerRunner
    }

    fun cancel(periodicOperationRunner: PeriodicOperationRunner) {
        if (periodicOperationRunner.isActive) {
            Log.d(classTag, "cancelTimer: end work time counter")
            periodicOperationRunner.stop()
        }
    }


    data class PeriodicOperationRunner(val params: PeriodicOperationParams) : ClassTagAware {
        // TODO: 27.08.2021 change way of calling another scope
        private val scopeForSaving = CoroutineScope(Job() + Dispatchers.Main)

        private var timer: Job? = null

        val isActive: Boolean
            get() = timer?.isActive == true

        private fun startCoroutineTimer(action: () -> Unit) =
            scopeForSaving.launch(Dispatchers.IO) {
                delay(params.delayMillis)
                if (params.repeatMillis > 0) while (true) {
                    action()
                    delay(params.repeatMillis)
                } else action()
            }

        fun start() {
            if (!isActive) timer = startCoroutineTimer {
                synchronized(params) {
                    Log.d(classTag, "timer: Background - tick runs ${params.backgroundActions.size} actions")
                    params.backgroundActions.forEachIndexed { index, it ->
                        Log.d(classTag, "timer: Background - launch action $index")
                        it()
                    }
                    scopeForSaving.launch {
                        Log.d(classTag, "timer: Main thread - tick runs ${params.mainThreadActions.size} actions")
                        params.mainThreadActions.forEachIndexed { index, it ->
                            Log.d(classTag, "timer: Main thread - launch action $index")
                            it()
                        }
                    }
                }
            }
        }

        fun stop() = timer?.cancel()

        fun syncWith(anotherRunner: PeriodicOperationRunner) {
            if(anotherRunner != this) {
                stop()
                anotherRunner.params.merge(params)
            }
        }

        fun attach(anotherParams: PeriodicOperationParams) {
            params.merge(anotherParams)
        }

        fun attach(anotherParamsCollection: Collection<PeriodicOperationParams>) {
            anotherParamsCollection.forEach { attach(it) }
        }

        fun detach(anotherParams: PeriodicOperationParams) {
            params.remove(anotherParams)
            if (params.backgroundActions.isEmpty() && params.mainThreadActions.isEmpty()) {
                Log.d(classTag, "detach: background and main thread actions empty - stop timer")
                stop()
            }
        }
    }

    class PeriodicOperationParams(
        val delayMillis: Long = 0,
        val repeatMillis: Long = 0,
        backgroundAction: () -> Unit = {},
        mainThreadAction: () -> Unit = {},
    ) {
        val backgroundActions: Set<() -> Unit>
            get() = mBackgroundActions

        val mainThreadActions: Set<() -> Unit>
            get() = mMainThreadActions

        private val mMainThreadActions = mutableSetOf(mainThreadAction)
        private val mBackgroundActions = mutableSetOf(backgroundAction)

        fun merge(anotherParams: PeriodicOperationParams) {
            if (anotherParams.delayMillis != delayMillis || anotherParams.repeatMillis != repeatMillis) {
                throw IllegalStateException("Try to sync params with not the same delay or repeat time")
            }
            synchronized(anotherParams) {
                mBackgroundActions += anotherParams.mBackgroundActions
                mMainThreadActions += anotherParams.mMainThreadActions
            }
        }

        fun remove(anotherParams: PeriodicOperationParams) {
            mBackgroundActions -= anotherParams.mBackgroundActions
            mMainThreadActions -= anotherParams.mMainThreadActions
        }
    }
}
