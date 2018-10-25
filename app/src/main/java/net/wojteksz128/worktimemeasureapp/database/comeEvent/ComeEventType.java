package net.wojteksz128.worktimemeasureapp.database.comeEvent;

import net.wojteksz128.worktimemeasureapp.R;

public enum ComeEventType {
    COME_IN(R.drawable.come_in_background, R.string.main_come_in_label),
    COME_OUT(R.drawable.come_out_background, R.string.main_come_out_label);

    private final int background;
    private final int displayLabel;

    ComeEventType(int background, int displayLabel) {
        this.background = background;
        this.displayLabel = displayLabel;
    }

    public int getBackground() {
        return background;
    }

    public int getDisplayLabel() {
        return displayLabel;
    }
}
