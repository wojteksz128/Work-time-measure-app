package net.wojteksz128.worktimemeasureapp;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import net.wojteksz128.worktimemeasureapp.database.ComeEvent;

public class ComeEventViewHolder extends RecyclerView.ViewHolder {

    final TextView dateTV;
    final TextView typeTV;


    public ComeEventViewHolder(View itemView, ComeEvent comeEvent) {
        super(itemView);

        dateTV = itemView.findViewById(R.id.day_item_date);
        typeTV = itemView.findViewById(R.id.day_item_type);

        dateTV.setText(comeEvent.getDate().toString());
        typeTV.setText(comeEvent.getType().name());
    }
}
