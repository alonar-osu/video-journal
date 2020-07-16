package com.example.android.videojournal.formatting;

import androidx.room.TypeConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Methods for date-related formating and conversions
 */
public class DateFormater {

    /**
     * Used for reading from db
     * Converts from a numeric date value in db to Date
     * @param timestamp date as a Long, from db
     * @return date as Date object
     */
    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    /**
     * Used for writing from db
     * @param date as Date object
     * @return a Long, numberic date
     */
    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    /**
     * Returns given date as string
     */
    public static String dateToString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("MMMM dd yyyy");
        return dateFormat.format(date);
    }

    /**
     * Returns date as String of the first Sunday before a given date
     * @param date - the date of the video
     * @return String representing date of first Sunday before given date
     */
    public static String precedingSundayDateAsString(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
        DateFormat dateFormat = new SimpleDateFormat("MMMM dd yyyy");
        return "" + dateFormat.format(c.getTime());
    }

    /**
     * Returns date as Date of the first Sunday before a given date
     * @param date - the date of the video
     * @return Date object representing the first Sunday before given date
     */
    public static Date precedingSundayDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
        return c.getTime();
    }

    /**
     * Returns today's date as String
     * Used for file names
     */
    public static String todaysDateForFileNameAsString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss", Locale.getDefault());
        return sdf.format(new Date());
    }

    /**
     * Returns today's date as String in a format for weekly video in feed
     */
    public static String todaysDateForWeeklyPreview() {
        SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd yyyy", Locale.US);
        Date now = new Date();
        return formatter.format(now);
    }

}
