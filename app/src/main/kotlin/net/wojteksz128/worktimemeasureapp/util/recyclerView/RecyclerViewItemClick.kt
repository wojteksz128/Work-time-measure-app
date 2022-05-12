package net.wojteksz128.worktimemeasureapp.util.recyclerView

import android.view.View

interface RecyclerViewItemClick<T> {
    var onItemClickListenerProvider: (T) -> (View) -> Unit
}