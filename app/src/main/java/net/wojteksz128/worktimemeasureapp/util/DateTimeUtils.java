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
        return new Date(comeEvent.getEndDate().getTime() - comeEvent.getStartDate().getTime());
    }

    public static Date mergeComeEventsDuration(WorkDayEvents workDay) {
        long millisSum = 0;

        for (ComeEvent comeEvent : workDay.getEvents()) {
            millisSum += comeEvent.getDuration() != null ? comeEvent.getDuration().getTime() : 0;
        }

        return new Date(millisSum);
    }
}
