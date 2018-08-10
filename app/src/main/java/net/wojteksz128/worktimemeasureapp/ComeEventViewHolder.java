package net.wojteksz128.worktimemeasureapp;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;

import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEvent;

// DONE: 10.08.2018 Change view and viewholder implementations
public class ComeEventViewHolder extends RecyclerView.ViewHolder {

    private final TextView mStartDateTV;
    private final TextView mEndDateTV;
    private final TextView mDurationTV;

    private final View view;


    @SuppressWarnings("WeakerAccess")
    public ComeEventViewHolder(View itemView) {
        super(itemView);

        view = itemView;
        mStartDateTV = itemView.findViewById(R.id.main_day_event_start_date);
        mEndDateTV = itemView.findViewById(R.id.main_day_event_end_date);
        mDurationTV = itemView.findViewById(R.id.main_day_event_duration);
    }

    public void bind(ComeEvent comeEvent) {
        final String startTime = DateFormat.format(view.getContext().getString(R.string.main_day_event_time_format), comeEvent.getStartDate()).toString();
        final String endTime = DateFormat.format(view.getContext().getString(R.string.main_day_event_time_format), comeEvent.getEndDate()).toString();
        final String duration = DateFormat.format("H:mm:ss", comeEvent.getDuration()).toString();

        mStartDateTV.setText(startTime);
        mEndDateTV.setText(endTime);
        mDurationTV.setText(duration);
    }

    public View getView() {
        return view;
    }
}
