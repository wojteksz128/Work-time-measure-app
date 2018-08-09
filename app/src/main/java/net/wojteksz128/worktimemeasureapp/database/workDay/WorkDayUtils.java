package net.wojteksz128.worktimemeasureapp.database.workDay;

import java.util.Calendar;
import java.util.Date;

public class WorkDayUtils {

    public static Date calculateBeginSlot(Date date) {
        final Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    public static Date calculateEndSlot(Date date) {
        final Calendar calendar = Calendar.getInstance();

        calendar.setTime(calculateBeginSlot(date));
        calendar.add(Calendar.DATE, 1);
        calendar.add(Calendar.MILLISECOND, -1);

        return calendar.getTime();
    }
}
