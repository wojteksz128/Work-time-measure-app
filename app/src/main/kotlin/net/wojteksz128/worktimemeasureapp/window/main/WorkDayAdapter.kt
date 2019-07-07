package net.wojteksz128.worktimemeasureapp.window.main

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayEvents

internal class WorkDayAdapter : RecyclerView.Adapter<WorkDayViewHolder>() {

    private var workDays: List<WorkDayEvents>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkDayViewHolder {

        val context = parent.context
        val inflater = LayoutInflater.from(context)

        val view = inflater.inflate(R.layout.main_work_day_list_item, parent, false)
        return WorkDayViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkDayViewHolder, position: Int) {
        holder.bind(workDays!![position])
    }

    override fun getItemCount(): Int {
        return if (workDays != null) workDays!!.size else 0
    }

    fun setWorkDays(workDays: List<WorkDayEvents>) {
        this.workDays = workDays
        notifyDataSetChanged()
    }
}
