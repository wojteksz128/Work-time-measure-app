package net.wojteksz128.worktimemeasureapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.wojteksz128.worktimemeasureapp.database.ComeEvent;

import java.util.List;

public class ComeEventAdapter extends RecyclerView.Adapter<ComeEventViewHolder> {

    private List<ComeEvent> events;


    @NonNull
    @Override
    public ComeEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.day_list_item, parent, false);
        return new ComeEventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComeEventViewHolder holder, int position) {
        holder.bind(events.get(position));
    }

    @Override
    public int getItemCount() {
        return events != null ? events.size() : 0;
    }

    public void setEvents(List<ComeEvent> events) {
        this.events = events;
        notifyDataSetChanged();
    }
}
