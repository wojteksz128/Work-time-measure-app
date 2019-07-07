package net.wojteksz128.worktimemeasureapp.util

abstract class FunctionWithParameter<T>(private val parameter: T) {

    protected abstract fun action(obj: T)

    fun invoke() = action(parameter)
}
