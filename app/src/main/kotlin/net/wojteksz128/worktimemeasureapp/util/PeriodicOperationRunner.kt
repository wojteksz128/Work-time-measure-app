package net.wojteksz128.worktimemeasureapp.util

import android.util.Log

import java.util.concurrent.atomic.AtomicBoolean


class PeriodicOperationRunner<T> {

    private val sleepDuration = 1000
    private var consumer: Consumer<T>? = null
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

    fun setConsumer(consumer: Consumer<T>) {
        this.consumer = consumer
    }

    private inner class PeriodicRun : Thread() {

        private val TAG = PeriodicRun::class.java.simpleName

        override fun run() {
            this@PeriodicOperationRunner.running.set(true)
            while (running.get()) {
                try {
                    Thread.sleep(this@PeriodicOperationRunner.sleepDuration.toLong())
                    if (this@PeriodicOperationRunner.consumer != null && running.get()) {
                        this@PeriodicOperationRunner.consumer!!.invoke()
                    } else {
                        Log.w(TAG, "run: consumer is null or running is ending")
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

            }
        }
    }
}
