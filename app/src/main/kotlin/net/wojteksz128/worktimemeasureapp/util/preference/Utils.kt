package net.wojteksz128.worktimemeasureapp.util.preference

import androidx.preference.MultiSelectListPreference

fun MultiSelectListPreference.setSummaryFromValues(values: Set<String>) {
    summary = values.map { findIndexOfValue(it) }.sorted().joinToString(", ") { entries[it] }
}