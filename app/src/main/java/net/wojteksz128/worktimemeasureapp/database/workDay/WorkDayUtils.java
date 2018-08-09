package net.wojteksz128.worktimemeasureapp.database.workDay;

import java.util.Date;

public class WorkDayUtils {

    private static final long ONE_DAY_MILLISECONDS = 24 * 60 * 60 * 1000;


    public static Date calculateBeginSlot(Date date) {
        return new Date(date.getTime() / ONE_DAY_MILLISECONDS * ONE_DAY_MILLISECONDS);
    }

    public static Date calculateEndSlot(Date date) {
        return new Date(calculateBeginSlot(date).getTime() + ONE_DAY_MILLISECONDS);
    }
}
