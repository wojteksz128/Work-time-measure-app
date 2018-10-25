package net.wojteksz128.worktimemeasureapp.window.main;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.wojteksz128.worktimemeasureapp.R;
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayEvents;

import java.util.List;

public class WorkDayAdapter extends RecyclerView.Adapter<WorkDayViewHolder> {

    private List<WorkDayEvents> workDays;

    @NonNull
    @Override
    public WorkDayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.main_work_day_list_item, parent, false);
        return new WorkDayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkDayViewHolder holder, int position) {
        holder.bind(workDays.get(position));
    }

    @Override
    public int getItemCount() {
        return workDays != null ? workDays.size() : 0;
    }

    public void setWorkDays(List<WorkDayEvents> workDays) {
        this.workDays = workDays;
        notifyDataSetChanged();
    }
}
