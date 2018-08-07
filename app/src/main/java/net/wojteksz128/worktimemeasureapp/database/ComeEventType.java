package net.wojteksz128.worktimemeasureapp.database;

import net.wojteksz128.worktimemeasureapp.R;

public enum ComeEventType {
    COME_IN(R.drawable.come_in_background),
    COME_OUT(R.drawable.come_out_background);

    private final int background;

    ComeEventType(int background) {
        this.background = background;
    }

    public int getBackground() {
        return background;
    }
}
