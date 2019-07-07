package net.wojteksz128.worktimemeasureapp.window.history

import android.arch.paging.PagedListAdapter
import android.support.v7.util.DiffUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayEvents

internal class WorkDayAdapter : PagedListAdapter<WorkDayEvents, WorkDayViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkDayViewHolder {

        val context = parent.context
        val inflater = LayoutInflater.from(context)

        val view = inflater.inflate(R.layout.history_work_day_list_item, parent, false)
        return WorkDayViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkDayViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }


    private class DiffCallback : DiffUtil.ItemCallback<WorkDayEvents>() {

        override fun areItemsTheSame(oldItem: WorkDayEvents?, newItem: WorkDayEvents?): Boolean {
            return oldItem?.workDay!!.id!! == newItem?.workDay!!.id!!
        }

        override fun areContentsTheSame(oldItem: WorkDayEvents?, newItem: WorkDayEvents?) = oldItem?.equals(newItem)
                ?: false
    }
}
