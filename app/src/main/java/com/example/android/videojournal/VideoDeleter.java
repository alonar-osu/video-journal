package com.example.android.videojournal;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;


public class VideoDeleter {

    private static final String TAG = VideoDeleter.class.getSimpleName();
    private AppDatabase mDb;
    Context context;

    public VideoDeleter(Context context, AppDatabase db) {
        mDb = db;
        this.context = context;
    }

    public void deleteJournalEntry(final String videoPath, final String thumbnailPath, final int position) {

        /*
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.app_name);
        builder.setMessage("Delete this entry?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                // delete
                deleteVideo (videoPath);
                deleteThumbnail(thumbnailPath);
                deleteEntryFromDB(position);

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                /// no need to delete
                Log.d(TAG, "Delete: not deleting");
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
        */



        deleteVideo (videoPath);
        deleteThumbnail(thumbnailPath);
        deleteEntryFromDB(position);


    }

    public void deleteVideo (String videoPath) {

        File videoFile = new File(videoPath);
        boolean videoDeleted = videoFile.delete();
        if(!videoDeleted){
            context.deleteFile(videoFile.getName());
        }
    }

    public void deleteThumbnail(String thumbnailPath) {

        File thumbnailFile = new File(thumbnailPath);
        boolean thumbnailDeleted = thumbnailFile.delete();
        if (!thumbnailDeleted) {
            context.deleteFile(thumbnailFile.getName());
        }
    }

    public void deleteEntryFromDB(final int position) {

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                ArrayList<VideoEntry> videoEntries = VideoAdapter.getVideos();
                mDb.videoDao().deleteVideo(videoEntries.get(position));
            }
        });
    }


}
