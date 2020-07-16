package com.example.android.videojournal.data;

import android.content.Context;
import android.util.Log;

import com.example.android.videojournal.formatting.DateFormater;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

/**
 * Database class using Room persistence library
 * Makes singleton instance
 * Uses VideoEntry objects as data access objects (DAO)
 */
@Database(entities = {VideoEntry.class}, version = 1, exportSchema = false)
@TypeConverters(DateFormater.class)
public abstract class VideoDatabase extends RoomDatabase {

    private static final String TAG = VideoDatabase.class.getSimpleName();

    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "videojournal";

    private static VideoDatabase mInstance;

    public static VideoDatabase getInstance(Context context) {
        if (mInstance == null) {
            synchronized (LOCK) {
                Log.d(TAG, "Creating new database instance");
                mInstance = Room.databaseBuilder(context.getApplicationContext(),
                        VideoDatabase.class, VideoDatabase.DATABASE_NAME)
                        .build();
            }
        }
        Log.d(TAG, "Getting the database instance");
        return mInstance;
    }

    /**
     * For accessing VideoDao interface that contains
     * methods for performing db queries
     */
    public abstract VideoDao videoDao();
}
