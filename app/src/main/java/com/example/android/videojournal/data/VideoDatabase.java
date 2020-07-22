package com.example.android.videojournal.data;

import android.content.Context;
import android.util.Log;

import com.example.android.videojournal.formatting.DateFormatter;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import static com.example.android.videojournal.utilities.Constants.DATABASE_NAME;

/**
 * Database class using Room persistence library
 * Makes singleton instance
 * Uses VideoEntry objects as data access objects (DAO)
 */
@Database(entities = {VideoEntry.class}, version = 1, exportSchema = false)
@TypeConverters(DateFormatter.class)
public abstract class VideoDatabase extends RoomDatabase {

    private static final String TAG = VideoDatabase.class.getSimpleName();

    private static final Object LOCK = new Object();
    private static VideoDatabase sInstance;

    public static VideoDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        VideoDatabase.class, DATABASE_NAME)
                        .build();
            }
        }
        Log.d(TAG, "Getting the database instance");
        return sInstance;
    }

    /**
     * For accessing VideoDao interface that contains
     * methods for performing db queries
     */
    public abstract VideoDao videoDao();
}
