package com.example.android.videojournal.ActionHelpers;

import android.app.Activity;
import android.content.Context;

import com.example.android.videojournal.data.AppDatabase;
import com.example.android.videojournal.utilities.AppExecutors;

public class VideoWeeklyUpdater {

    private static final String TAG = VideoWeeklyUpdater.class.getSimpleName();

    public VideoWeeklyUpdater() { }

    public static void updateWeeklyVideo(final Activity activity, final AppDatabase mDb) {

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                // delete existing weekly video
                VideoDeleter vidDeleter = new VideoDeleter(activity, mDb);
                vidDeleter.deleteCurrentMergedVideo();

                VideoCombiner combineVids = new VideoCombiner(activity, mDb);
                if (combineVids.thisWeeksVideoCount() > 0) {
                    // combine
                    final String combinedVideoPath = combineVids.combineVideosForWeek();
                    // add combined video
                    VideoAdder vidAdder = new VideoAdder(activity, mDb);
                    if (combinedVideoPath.length() > 0) {
                        vidAdder.addVideo(combinedVideoPath, true);
                    }
                }
            }
        });
    }

}
