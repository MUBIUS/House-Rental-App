package com.example.houserentalapp.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateUtils {

    private static final SimpleDateFormat displayFormat =
            new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
    private static final SimpleDateFormat timeFormat =
            new SimpleDateFormat("hh:mm a", Locale.getDefault());
    private static final SimpleDateFormat fullFormat =
            new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
    private static final SimpleDateFormat dbFormat =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat monthYearFormat =
            new SimpleDateFormat("MMM yyyy", Locale.getDefault());

    public static String formatDate(long timestamp) {
        return displayFormat.format(new Date(timestamp));
    }

    public static String formatTime(long timestamp) {
        return timeFormat.format(new Date(timestamp));
    }

    public static String formatDateTime(long timestamp) {
        return fullFormat.format(new Date(timestamp));
    }

    public static String formatMonthYear(long timestamp) {
        return monthYearFormat.format(new Date(timestamp));
    }

    public static String formatForDb(long timestamp) {
        return dbFormat.format(new Date(timestamp));
    }

    /** Returns a relative time string: "Just now", "2 hrs ago", "3 days ago", etc. */
    public static String getRelativeTime(long timestamp) {
        long now = System.currentTimeMillis();
        long diff = now - timestamp;

        if (diff < 60_000) return "Just now";
        if (diff < 3_600_000) {
            long mins = TimeUnit.MILLISECONDS.toMinutes(diff);
            return mins + " min" + (mins > 1 ? "s" : "") + " ago";
        }
        if (diff < 86_400_000) {
            long hrs = TimeUnit.MILLISECONDS.toHours(diff);
            return hrs + " hr" + (hrs > 1 ? "s" : "") + " ago";
        }
        if (diff < 604_800_000) {
            long days = TimeUnit.MILLISECONDS.toDays(diff);
            return days + " day" + (days > 1 ? "s" : "") + " ago";
        }
        return displayFormat.format(new Date(timestamp));
    }

    public static String getChatTime(long timestamp) {
        long now = System.currentTimeMillis();
        long diff = now - timestamp;
        if (diff < 86_400_000) {
            return timeFormat.format(new Date(timestamp)); // Today: show time
        }
        return displayFormat.format(new Date(timestamp)); // Older: show date
    }

    public static boolean isOverdue(long dueDateTimestamp) {
        return System.currentTimeMillis() > dueDateTimestamp;
    }

    public static long addMonths(long timestamp, int months) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        cal.add(Calendar.MONTH, months);
        return cal.getTimeInMillis();
    }

    public static long addDays(long timestamp, int days) {
        return timestamp + (long) days * 86_400_000;
    }

    public static long now() {
        return System.currentTimeMillis();
    }

    public static long startOfToday() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }
}
