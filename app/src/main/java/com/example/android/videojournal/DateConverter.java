package com.example.android.videojournal;

import androidx.room.TypeConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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

}
