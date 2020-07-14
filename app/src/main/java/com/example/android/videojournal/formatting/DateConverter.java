package com.example.android.videojournal.formatting;

import androidx.room.TypeConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateConverter {

    // for reading from db
    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    // for writing to db
    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    public static String dateToString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("MMMM dd yyyy");
        return dateFormat.format(date);
    }

    public static String precedingSundayDateAsString(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
        DateFormat dateFormat = new SimpleDateFormat("MMMM dd yyyy");
        return "" + dateFormat.format(c.getTime());
    }

    public static Date precedingSundayDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
        return c.getTime();
    }

    public static String todaysDateForFileNameAsString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss", Locale.getDefault());
        return sdf.format(new Date());
    }

    public static String todaysDateForWeeklyPreview() {
        SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd yyyy", Locale.US);
        Date now = new Date();
        return formatter.format(now);
    }

}
