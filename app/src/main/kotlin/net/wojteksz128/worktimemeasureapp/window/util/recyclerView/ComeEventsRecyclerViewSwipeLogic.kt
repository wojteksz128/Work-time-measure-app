package net.wojteksz128.worktimemeasureapp.window.util.recyclerView

import android.content.Context
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.util.recyclerView.RecyclerLeftSwipeActionParams
import net.wojteksz128.worktimemeasureapp.util.recyclerView.RecyclerRightSwipeActionParams

class ComeEventRecyclerLeftSwipeActionParams(context: Context) :
    RecyclerLeftSwipeActionParams(R.color.teal_200, R.drawable.ic_baseline_edit_24, context)

class ComeEventRecyclerRightSwipeActionParams(context: Context) :
    RecyclerRightSwipeActionParams(R.color.colorAlert, R.drawable.ic_baseline_delete_24, context)