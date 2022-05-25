package net.wojteksz128.worktimemeasureapp.util.collection

@Suppress("UNCHECKED_CAST")
inline fun <reified T> Any.asSet(): Set<T> =
    if (this is Set<*> && this.all { it is T })
        this as Set<T>
    else
        throw IllegalStateException("Object ($this) cannot be type of Set<${T::class.java.simpleName}>")
