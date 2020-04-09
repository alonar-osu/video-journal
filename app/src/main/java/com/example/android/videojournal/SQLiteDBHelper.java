package com.example.android.videojournal;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.widget.Toast;

public class SQLiteDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "videoinfo_database";
    private static final String VIDEO_TABLE_NAME = "videopaths";
    private static final String VIDEOINFO_COLUMN_ID = "_id";
    private static final String VIDEOINFO_COLUMN_DATE = "datetime";
    private static final String VIDEOINFO_COLUMN_PATH = "path";

    public SQLiteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + VIDEO_TABLE_NAME + " (" +
                VIDEOINFO_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                VIDEOINFO_COLUMN_PATH + " TEXT, " +
                VIDEOINFO_COLUMN_DATE + " TEXT" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + VIDEO_TABLE_NAME);
        onCreate(db);
    }

    protected void saveToDB(Context context, Uri videoUri){
       SQLiteDatabase db = new SQLiteDBHelper(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SQLiteDBHelper.VIDEOINFO_COLUMN_PATH, videoUri.toString());
        // TODO: update to get current date
        values.put(SQLiteDBHelper.VIDEOINFO_COLUMN_DATE, "2020-04-08");
        long newRowId = db.insert(SQLiteDBHelper.VIDEO_TABLE_NAME, null, values);

        Toast.makeText(context, "The new Row Id is " + newRowId, Toast.LENGTH_LONG).show();
    }
}
