package com.example.android.videojournal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import androidx.loader.content.CursorLoader;

public class SQLiteDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "videoinfo_database";
    private static final String VIDEO_TABLE_NAME = "videopaths";
    private static final String VIDEOINFO_COLUMN_ID = "_id";
    private static final String VIDEOINFO_COLUMN_DATE = "datetime";
    private static final String VIDEOINFO_COLUMN_PATH = "path";
    private static final String VIDEOINFO_COLUMN_HEIGHT = "videoheight";
    private static final String VIDEOINFO_COLUMN_WIDTH = "videowidth";

    public SQLiteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + VIDEO_TABLE_NAME + " (" +
                VIDEOINFO_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                VIDEOINFO_COLUMN_PATH + " TEXT, " +
                VIDEOINFO_COLUMN_DATE + " TEXT," +
                VIDEOINFO_COLUMN_HEIGHT + " INTEGER," +
                VIDEOINFO_COLUMN_WIDTH + " INTEGER" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + VIDEO_TABLE_NAME);
        onCreate(db);
    }

    protected void saveToDB(Context context, Uri videoUri){
       SQLiteDatabase db = new SQLiteDBHelper(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        String realPath = getRealPathFromURI(context, videoUri);

        // get dimensions
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(realPath);
        int videowidth = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        int videoheight = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        retriever.release();

        values.put(SQLiteDBHelper.VIDEOINFO_COLUMN_PATH, realPath);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String currDateTime = sdf.format(new Date());
        values.put(SQLiteDBHelper.VIDEOINFO_COLUMN_DATE, currDateTime);
        values.put(SQLiteDBHelper.VIDEOINFO_COLUMN_HEIGHT, videoheight);
        values.put(SQLiteDBHelper.VIDEOINFO_COLUMN_WIDTH, videowidth);
        long newRowId = db.insert(SQLiteDBHelper.VIDEO_TABLE_NAME, null, values);

        Toast.makeText(context, "The new Row Id is " + newRowId, Toast.LENGTH_LONG).show();
    }


    private String getRealPathFromURI(Context context, Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    protected static ArrayList<VideoEntry> getVideoEntriesFromDB(Context context) {
        ArrayList<VideoEntry> videoEntryList = new ArrayList<>();
        SQLiteDatabase db = new SQLiteDBHelper(context).getWritableDatabase();
        String query = "SELECT path, datetime, videoheight, videowidth FROM " + VIDEO_TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            String videoPath = cursor.getString(cursor.getColumnIndex(VIDEOINFO_COLUMN_PATH));
            String date = cursor.getString(cursor.getColumnIndex(VIDEOINFO_COLUMN_DATE));
            int videoHeight = cursor.getInt(cursor.getColumnIndex(VIDEOINFO_COLUMN_HEIGHT));
            int videoWidth = cursor.getInt(cursor.getColumnIndex(VIDEOINFO_COLUMN_WIDTH));

            VideoEntry videoEntry = new VideoEntry(videoPath,date,videoHeight,videoWidth);
            videoEntryList.add(videoEntry);
        }
        return videoEntryList;
    }



}
