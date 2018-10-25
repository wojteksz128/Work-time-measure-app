package net.wojteksz128.worktimemeasureapp.util;

import android.annotation.SuppressLint;

import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEvent;
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayEvents;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeUtils {

    public static String formatDate(String format, Date date) {
        return formatDate(format, date, TimeZone.getDefault());
    }

    public static String formatDate(String format, Date date, TimeZone timeZone) {
        @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat(format);
        formatter.setTimeZone(timeZone);
        return formatter.format(date);
    }

    public static Date calculateDuration(ComeEvent comeEvent) {
        final long startTime = comeEvent.getStartDate().getTime();
        final long endTime = comeEvent.getEndDate() != null ? comeEvent.getEndDate().getTime() : System.currentTimeMillis();
        return new Date(endTime - startTime);
    }

    public static Date mergeComeEventsDuration(WorkDayEvents workDay) {
        long millisSum = 0;

        for (ComeEvent comeEvent : workDay.getEvents()) {
            millisSum += comeEvent.getDuration() != null ? comeEvent.getDuration().getTime() : calculateDuration(comeEvent).getTime();
        }

        return new Date(millisSum);
    }
}
