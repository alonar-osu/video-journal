package com.example.android.videojournal.actionhelpers;

import android.content.Context;

import com.example.android.videojournal.formatting.DateFormatter;
import com.example.android.videojournal.recyclerview.VideoAdapter;
import com.example.android.videojournal.data.VideoDatabase;
import com.example.android.videojournal.data.VideoEntry;
import com.example.android.videojournal.utilities.AppExecutors;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

/**
 * Allows deleting any specific regular video
 * And allows deleting current combined weekly video
 */
public class VideoDeleter {

    private final VideoDatabase mDb;
    private final Context mContext;

    public VideoDeleter(Context context, VideoDatabase db) {
        mDb = db;
        mContext = context;
    }

    /**
     * Deletes video file, its thumbnail file, and video info from db
     * @param videoPath absolute path to video file
     * @param thumbnailPath absolute path to thumbnail file
     * @param position position of video view from recyclerview holder
     */
    public void deleteJournalEntryByPosition(final String videoPath,
                                             final String thumbnailPath, final int position) {
        deleteVideo(videoPath);
        deleteThumbnail(thumbnailPath);
        deleteEntryFromDB(position);
    }

    /**
     * Checks database for a combined video added after last Sunday
     * Deletes video file using its path, deletes its thumbnail and deletes its
     * associated info from db
     */
    public void deleteCurrentMergedVideo() {

        Date today = new Date();
        Date precedingSunday = DateFormatter.precedingSundayDate(today);
        VideoEntry currentMergedVideo = mDb.videoDao()
                .loadCurrentCombinedVideo(precedingSunday, today);

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
            mContext.deleteFile(videoFile.getName());
        }
    }

    private void deleteThumbnail(String thumbnailPath) {

        File thumbnailFile = new File(thumbnailPath);
        boolean thumbnailDeleted = thumbnailFile.delete();
        if (!thumbnailDeleted) {
            mContext.deleteFile(thumbnailFile.getName());
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
