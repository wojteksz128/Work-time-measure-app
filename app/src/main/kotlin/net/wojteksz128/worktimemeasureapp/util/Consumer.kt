package net.wojteksz128.worktimemeasureapp.util

abstract class Consumer<T>(private val parameter: T) {

    abstract fun action(obj: T)

    internal operator fun invoke() {
        action(parameter)
    }
}
