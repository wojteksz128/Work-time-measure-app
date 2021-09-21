package net.wojteksz128.worktimemeasureapp.util.coroutines

import android.util.Log
import kotlinx.coroutines.*
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware

object PeriodicOperation : ClassTagAware {

    fun start(params: PeriodicOperationParams): PeriodicOperationRunner {
        val workTimeTimerRunner = PeriodicOperationRunner(params)

        if (!workTimeTimerRunner.timer.isActive) {
            Log.d(classTag, "startTimer: start work time counter")
            workTimeTimerRunner.timer.start()
        }

        return workTimeTimerRunner
    }

    fun cancel(periodicOperationRunner: PeriodicOperationRunner) {
        if (periodicOperationRunner.timer.isActive) {
            Log.d(classTag, "cancelTimer: end work time counter")
            periodicOperationRunner.timer.cancel()
        }
    }


    data class PeriodicOperationRunner(val params: PeriodicOperationParams) : ClassTagAware {
        // TODO: 27.08.2021 change way of calling another scope
        private val job = Job()
        private val scopeForSaving = CoroutineScope(job + Dispatchers.Main)

        val timer: Job = startCoroutineTimer {
            Log.d(classTag, "timer: Background - tick")
            params.backgroundAction()
            scopeForSaving.launch {
                Log.d(classTag, "timer: Main thread - tick")
                params.mainThreadAction()
            }
        }

        private fun startCoroutineTimer(action: () -> Unit) =
            scopeForSaving.launch(Dispatchers.IO) {
                delay(params.delayMillis)
                if (params.repeatMillis > 0) while (true) {
                    action()
                    delay(params.repeatMillis)
                } else action()
            }
    }

    data class PeriodicOperationParams(
        val delayMillis: Long = 0,
        val repeatMillis: Long = 0,
        val backgroundAction: () -> Unit = {},
        val mainThreadAction: () -> Unit = {},
    )
}
