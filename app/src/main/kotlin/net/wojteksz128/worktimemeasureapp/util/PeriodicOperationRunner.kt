package net.wojteksz128.worktimemeasureapp.util

import android.util.Log

import java.util.concurrent.atomic.AtomicBoolean

// TODO: 08.06.2021 Make works better for CPU time (not sleep) - https://stackoverflow.com/questions/11434056/how-to-run-a-method-every-x-seconds
// TODO: 08.06.2021 Parametrize runner
@Deprecated("Change for WorkTimeTimer implementation")
class PeriodicOperationRunner<T> {

    private val sleepDuration = 1000
    private var functionWithParameter: FunctionWithParameter<T>? = null
    private val running = AtomicBoolean(false)
    private var thread: Thread? = null

    val isRunning: Boolean
        get() = running.get()

    fun start() {
        if (thread == null || !isRunning) {
            this.thread = PeriodicRun()
            thread!!.start()
        }
    }

    fun stop() {
        running.set(false)
    }

    fun setConsumer(functionWithParameter: FunctionWithParameter<T>) {
        this.functionWithParameter = functionWithParameter
    }

    private inner class PeriodicRun : Thread() {

        @Suppress("PrivatePropertyName")
        private val TAG = PeriodicRun::class.java.simpleName

        override fun run() {
            running.set(true)
            while (running.get()) {
                try {
                    sleep(sleepDuration.toLong())
                    if (functionWithParameter != null && running.get()) {
                        functionWithParameter!!.invoke()
                    } else {
                        Log.w(TAG, "run: functionWithParameter is null or running is ending")
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

            }
        }
    }
}
