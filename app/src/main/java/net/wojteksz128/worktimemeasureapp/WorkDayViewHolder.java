package net.wojteksz128.worktimemeasureapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEvent;
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayEvents;
import net.wojteksz128.worktimemeasureapp.util.DateTimeUtils;

import java.util.List;
import java.util.TimeZone;

class WorkDayViewHolder extends RecyclerView.ViewHolder {

    private final TextView mDateTV;
    private final TextView mWorkDurationTV;
    private final LinearLayout mEventsListLayout;

    private final View mView;

    public WorkDayViewHolder(View itemView) {
        super(itemView);

        mView = itemView;
        mDateTV = itemView.findViewById(R.id.day_label);
        mWorkDurationTV = itemView.findViewById(R.id.day_work_duration);
        mEventsListLayout = itemView.findViewById(R.id.day_events_list);
    }

    public void bind(WorkDayEvents workDay) {
        fillDateLabel(workDay);
        fillDurationLabel(workDay);

        prepareListWithEvents(workDay.getEvents());
    }

    private void fillDurationLabel(WorkDayEvents workDay) {
        final String duration = DateTimeUtils.formatDate(mView.getContext().getString(R.string.main_work_day_duration_format),
                DateTimeUtils.mergeComeEventsDuration(workDay), TimeZone.getTimeZone("UTC"));
        mWorkDurationTV.setText(duration);
    }

    private void fillDateLabel(WorkDayEvents workDay) {
        final String dateLabel = DateTimeUtils.formatDate(mView.getContext().getString(R.string.main_work_day_label_format),
                workDay.getWorkDay().getDate());
        mDateTV.setText(dateLabel);
    }

    private void prepareListWithEvents(List<ComeEvent> events) {
        final Context context = mView.getContext();
        final LayoutInflater inflater = LayoutInflater.from(context);

        mEventsListLayout.removeAllViews();

        for (ComeEvent event : events) {
            final ComeEventViewHolder eventViewHolder = new ComeEventViewHolder(inflater.inflate(R.layout.main_day_event_list_item, mEventsListLayout, false));
            eventViewHolder.bind(event);
            mEventsListLayout.addView(eventViewHolder.getView());
        }
    }
}
