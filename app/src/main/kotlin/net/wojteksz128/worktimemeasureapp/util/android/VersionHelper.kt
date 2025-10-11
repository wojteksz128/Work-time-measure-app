package net.wojteksz128.worktimemeasureapp.util.android

import android.os.Build

sealed class ApiVersionHandler<T> {
    class Success<T>(val result: T) : ApiVersionHandler<T>()
    class RequiresFallback<T> : ApiVersionHandler<T>()
}

fun <T> fromVersion(version: Int, block: () -> T): ApiVersionHandler<T> {
    return if (Build.VERSION.SDK_INT >= version) ApiVersionHandler.Success(block())
    else ApiVersionHandler.RequiresFallback()
}

infix fun <T, R : T> ApiVersionHandler<R>.before(fallback: () -> T): T {
    return when (this) {
        is ApiVersionHandler.Success -> this.result
        is ApiVersionHandler.RequiresFallback -> fallback()
    }
}

inline fun onVersion(version: Int, block: () -> Unit) {
    if (Build.VERSION.SDK_INT >= version)
        block()
}