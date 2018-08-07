package net.wojteksz128.worktimemeasureapp;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import net.wojteksz128.worktimemeasureapp.database.ComeEvent;

public class ComeEventViewHolder extends RecyclerView.ViewHolder {

    private final TextView dateTV;
    private final TextView typeTV;
    private final View view;


    public ComeEventViewHolder(View itemView) {
        super(itemView);

        view = itemView;
        dateTV = itemView.findViewById(R.id.day_item_date);
        typeTV = itemView.findViewById(R.id.day_item_type);
    }

    public void bind(ComeEvent comeEvent) {
        dateTV.setText(comeEvent.getDate().toString());

        typeTV.setText(comeEvent.getType().name());
        // TODO: 2018-08-07 Solve problem with API versions
        typeTV.setBackground(view.getContext().getDrawable(comeEvent.getType().getBackground()));
        typeTV.setPadding(4, 4, 4, 4);
    }
}
