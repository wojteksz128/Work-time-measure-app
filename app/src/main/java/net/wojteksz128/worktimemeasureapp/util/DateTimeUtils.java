package net.wojteksz128.worktimemeasureapp.util;

import android.annotation.SuppressLint;

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
}
