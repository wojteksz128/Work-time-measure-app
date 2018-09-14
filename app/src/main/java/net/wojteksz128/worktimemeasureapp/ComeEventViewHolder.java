package net.wojteksz128.worktimemeasureapp;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEvent;
import net.wojteksz128.worktimemeasureapp.util.DateTimeUtils;

import java.util.TimeZone;

// DONE: 10.08.2018 Change mView and viewholder implementations
public class ComeEventViewHolder extends RecyclerView.ViewHolder {

    private final TextView mStartDateTV;
    private final TextView mEndDateTV;
    private final TextView mDurationTV;

    private final View mView;
    private ComeEvent comeEvent;


    @SuppressWarnings("WeakerAccess")
    public ComeEventViewHolder(View itemView) {
        super(itemView);

        mView = itemView;
        mStartDateTV = itemView.findViewById(R.id.main_day_event_start_date);
        mEndDateTV = itemView.findViewById(R.id.main_day_event_end_date);
        mDurationTV = itemView.findViewById(R.id.main_day_event_duration);
    }

    public void bind(ComeEvent comeEvent) {
        this.comeEvent = comeEvent;

        fillStartTime(comeEvent);
        fillEndTime(comeEvent);
        fillDuration(comeEvent);
    }

    private void fillDuration(ComeEvent comeEvent) {
        final String duration = comeEvent.getDuration() != null ? DateTimeUtils.formatDate(mView.getContext().getString(R.string.main_work_day_duration_format),
                comeEvent.getDuration(), TimeZone.getTimeZone("UTC")) : "";
        mDurationTV.setText(duration);
    }

    private void fillEndTime(ComeEvent comeEvent) {
        final String endTime = comeEvent.getEndDate() != null ? DateTimeUtils.formatDate(mView.getContext().getString(R.string.main_day_event_time_format), comeEvent.getEndDate()) : "Teraz";
        mEndDateTV.setText(endTime);
    }

    private void fillStartTime(ComeEvent comeEvent) {
        final String startTime = DateTimeUtils.formatDate(mView.getContext().getString(R.string.main_day_event_time_format), comeEvent.getStartDate());
        mStartDateTV.setText(startTime);
    }

    public View getView() {
        return mView;
    }

    public void updateBind() {
        this.bind(this.comeEvent);
    }
}
