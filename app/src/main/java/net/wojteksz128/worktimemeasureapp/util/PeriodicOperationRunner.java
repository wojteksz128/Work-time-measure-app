package net.wojteksz128.worktimemeasureapp.util;

import java.util.concurrent.atomic.AtomicBoolean;

public class PeriodicOperationRunner<T> {

    private int sleepDuration = 1000;
    private final Consumer<T> consumer;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final Thread thread;

    public PeriodicOperationRunner(Consumer<T> consumer) {
        this.consumer = consumer;
        this.thread = new Thread() {

            @Override
            public void run() {
                running.set(true);
                while (running.get()) {
                    try {
                        Thread.sleep(sleepDuration);
                        if (PeriodicOperationRunner.this.consumer != null && running.get()) {
                            PeriodicOperationRunner.this.consumer.invoke();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    public void start() {
        thread.start();
    }

    public void stop() {
        running.set(false);
    }
}
