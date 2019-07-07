package net.wojteksz128.worktimemeasureapp.window.main

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEvent

class ComeEventAdapter : RecyclerView.Adapter<ComeEventViewHolder>() {

    private var events: List<ComeEvent>? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComeEventViewHolder {

        val context = parent.context
        val inflater = LayoutInflater.from(context)

        val view = inflater.inflate(R.layout.main_day_event_list_item, parent, false)
        return ComeEventViewHolder(view)
    }

    override fun onBindViewHolder(holder: ComeEventViewHolder, position: Int) {
        holder.bind(events!![position])
    }

    override fun getItemCount(): Int {
        return if (events != null) events!!.size else 0
    }

    fun setEvents(events: List<ComeEvent>) {
        this.events = events
        notifyDataSetChanged()
    }
}
