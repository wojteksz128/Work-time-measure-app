package net.wojteksz128.worktimemeasureapp.util

// TODO: 09.06.2021 Usuń, gdy będzie to konieczne - zastąpione przez kotlinowy odpowiednik
@Deprecated("Change for Kotlin implementation")
abstract class FunctionWithParameter<T>(private val parameter: T) {

    protected abstract fun action(obj: T)

    fun invoke() = action(parameter)
}
