package com.example.android.videojournal;

import androidx.room.TypeConverter;
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
}
