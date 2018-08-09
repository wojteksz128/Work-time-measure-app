package net.wojteksz128.worktimemeasureapp;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;

import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEvent;

public class ComeEventViewHolder extends RecyclerView.ViewHolder {

    private final TextView dateTV;
    private final TextView typeTV;

    private final View view;


    @SuppressWarnings("WeakerAccess")
    public ComeEventViewHolder(View itemView) {
        super(itemView);

        view = itemView;
        dateTV = itemView.findViewById(R.id.day_item_date);
        typeTV = itemView.findViewById(R.id.day_item_type);
    }

    public void bind(ComeEvent comeEvent) {
        final String enterTime = DateFormat.format(view.getContext().getString(R.string.main_day_event_time_format), comeEvent.getDate()).toString();
        dateTV.setText(enterTime);

        typeTV.setText(comeEvent.getType().getDisplayLabel());
        setTypeBackground(view.getContext().getResources().getDrawable(comeEvent.getType().getBackground()));
        typeTV.setPadding(8, 4, 8, 4);
    }

    private void setTypeBackground(Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            typeTV.setBackground(drawable);
        } else {
            typeTV.setBackgroundDrawable(drawable);
        }
    }

    public View getView() {
        return view;
    }
}
