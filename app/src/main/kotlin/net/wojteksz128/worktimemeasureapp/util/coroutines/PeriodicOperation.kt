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
                Log.d(classTag, "timer: Background - tick")
                params.backgroundAction()
                scopeForSaving.launch {
                    Log.d(classTag, "timer: Main thread - tick")
                    params.mainThreadAction()
                }
            }
        }

        fun stop() = timer?.cancel()
    }

    data class PeriodicOperationParams(
        val delayMillis: Long = 0,
        val repeatMillis: Long = 0,
        val backgroundAction: () -> Unit = {},
        val mainThreadAction: () -> Unit = {},
    )
}
