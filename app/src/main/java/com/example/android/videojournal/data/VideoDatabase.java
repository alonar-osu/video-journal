package com.example.android.videojournal.data;

import android.content.Context;
import android.util.Log;

import com.example.android.videojournal.formatting.DateConverter;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {VideoEntry.class}, version = 1, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class VideoDatabase extends RoomDatabase {

    private static final String TAG = VideoDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "videojournal";
    private static VideoDatabase sInstance;

    public static VideoDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        VideoDatabase.class, VideoDatabase.DATABASE_NAME)
                        .build();
            }
        }
        Log.d(TAG, "Getting the database instance");
        return sInstance;
    }

    public abstract VideoDao videoDao();
}
