package net.wojteksz128.worktimemeasureapp.util

interface ClassTagAware {
    val classTag: String
        get() = this::class.java.simpleName
}