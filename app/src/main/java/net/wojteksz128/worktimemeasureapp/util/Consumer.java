package net.wojteksz128.worktimemeasureapp.util;

public abstract class Consumer<T> {

    private T parameter;

    public Consumer(T parameter) {
        this.parameter = parameter;
    }

    public abstract void action(T obj);

    void invoke() {
        action(parameter);
    }
}
