package com.example.android.videojournal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
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

        String realPath = getRealPathFromURI(context, videoUri);
       // String realPath = getPath(context, videoUri);
        values.put(SQLiteDBHelper.VIDEOINFO_COLUMN_PATH, realPath);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String currDateTime = sdf.format(new Date());
        values.put(SQLiteDBHelper.VIDEOINFO_COLUMN_DATE, currDateTime);
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



/*
    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        String result = uri+"";
        // DocumentProvider
        //  if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
        if (isKitKat && (result.contains("media.documents"))) {
            String[] ary = result.split("/");
            int length = ary.length;
            String imgary = ary[length-1];
            final String[] dat = imgary.split("%3A");
            final String docId = dat[1];
            final String type = dat[0];
            Uri contentUri = null;
            if ("image".equals(type)) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            } else if ("video".equals(type)) {
            } else if ("audio".equals(type)) {
            }
            final String selection = "_id=?";
            final String[] selectionArgs = new String[] {
                    dat[1]
            };
            return getDataColumn(context, contentUri, selection, selectionArgs);
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
   */


    protected static ArrayList<String> getPathsFromDB(Context context) {
        SQLiteDatabase db = new SQLiteDBHelper(context).getWritableDatabase();
        ArrayList<String> videoPathsList = new ArrayList<>();
        String query = "SELECT path FROM " + VIDEO_TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            videoPathsList.add(cursor.getString(cursor.getColumnIndex(VIDEOINFO_COLUMN_PATH)));
        }
        return videoPathsList;
    }



}
