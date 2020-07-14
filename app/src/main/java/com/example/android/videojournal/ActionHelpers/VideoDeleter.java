package com.example.android.videojournal.ActionHelpers;

import android.content.Context;

import com.example.android.videojournal.VideoAdapter;
import com.example.android.videojournal.data.AppDatabase;
import com.example.android.videojournal.data.VideoEntry;
import com.example.android.videojournal.formatting.DateConverter;
import com.example.android.videojournal.utilities.AppExecutors;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;


public class VideoDeleter {

    private static final String TAG = VideoDeleter.class.getSimpleName();
    private AppDatabase mDb;
    private Context context;

    public VideoDeleter(Context context, AppDatabase db) {
        mDb = db;
        this.context = context;
    }

    public void deleteJournalEntryByPosition(final String videoPath, final String thumbnailPath, final int position) {
        deleteVideo(videoPath);
        deleteThumbnail(thumbnailPath);
        deleteEntryFromDB(position);
    }

    public void deleteCurrentMergedVideo() {

        Date today = new Date();
        Date precedingSunday = DateConverter.precedingSundayDate(today);
        VideoEntry currentMergedVideo = mDb.videoDao().loadCurrentCombinedVideo(precedingSunday, today);

        if (currentMergedVideo != null) {
            String videoPath = currentMergedVideo.getVideopath();
            deleteVideo(videoPath);
            String thumbnailPath = currentMergedVideo.getThumbnailPath();
            deleteThumbnail(thumbnailPath);
            mDb.videoDao().deleteVideo(currentMergedVideo);
        }
    }

    private void deleteVideo (String videoPath) {

        File videoFile = new File(videoPath);
        boolean videoDeleted = videoFile.delete();
        if(!videoDeleted){
            context.deleteFile(videoFile.getName());
        }
    }

    private void deleteThumbnail(String thumbnailPath) {

        File thumbnailFile = new File(thumbnailPath);
        boolean thumbnailDeleted = thumbnailFile.delete();
        if (!thumbnailDeleted) {
            context.deleteFile(thumbnailFile.getName());
        }
    }

    private void deleteEntryFromDB(final int position) {

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                ArrayList<VideoEntry> videoEntries = VideoAdapter.getVideos();
                mDb.videoDao().deleteVideo(videoEntries.get(position));
            }
        });
    }


}
