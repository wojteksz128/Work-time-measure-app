package net.wojteksz128.worktimemeasureapp.util;

import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

public class PeriodicOperationRunner<T> {

    private int sleepDuration = 1000;
    private Consumer<T> consumer;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private Thread thread;


    public PeriodicOperationRunner() {}

    public void start() {
        if (thread == null || !isRunning()) {
            this.thread = new PeriodicRun();
            thread.start();
        }
    }

    public void stop() {
        running.set(false);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isRunning() {
        return running.get();
    }

    public void setConsumer(Consumer<T> consumer) {
        this.consumer = consumer;
    }

    private class PeriodicRun extends Thread {

        private final String TAG = PeriodicOperationRunner.PeriodicRun.class.getSimpleName();

        @Override
        public void run() {
            PeriodicOperationRunner.this.running.set(true);
            while (running.get()) {
                try {
                    Thread.sleep(PeriodicOperationRunner.this.sleepDuration);
                    if (PeriodicOperationRunner.this.consumer != null && running.get()) {
                        PeriodicOperationRunner.this.consumer.invoke();
                    } else {
                        Log.w(TAG, "run: consumer is null or running is ending");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
