package net.wojteksz128.worktimemeasureapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEvent;
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayEvents;

import java.util.List;

class WorkDayViewHolder extends RecyclerView.ViewHolder {

    private final TextView dateTV;
    private final TextView workDurationTV;
    private final LinearLayout eventsList;
    private final View view;


    public WorkDayViewHolder(View itemView) {
        super(itemView);

        view = itemView;
        dateTV = itemView.findViewById(R.id.day_label);
        workDurationTV = itemView.findViewById(R.id.day_workDuration);
        eventsList = itemView.findViewById(R.id.day_events_list);
    }

    public void bind(WorkDayEvents workDay) {
        final String label = DateFormat.format(view.getContext().getString(R.string.main_work_day_label_format), workDay.getWorkDay().getDate()).toString();
        dateTV.setText(label);

        workDurationTV.setText(DateFormat.format(view.getContext().getString(R.string.main_work_day_duration_format), workDay.getWorkDay().getWorktime()));
        prepareListWithEvents(workDay.getEvents());
    }

    private void prepareListWithEvents(List<ComeEvent> events) {
        final Context context = view.getContext();
        final LayoutInflater inflater = LayoutInflater.from(context);

        eventsList.removeAllViews();

        for (ComeEvent event : events) {
            final ComeEventViewHolder eventViewHolder = new ComeEventViewHolder(inflater.inflate(R.layout.main_day_event_list_item, eventsList, false));
            eventViewHolder.bind(event);
            eventsList.addView(eventViewHolder.getView());
        }
    }
}
