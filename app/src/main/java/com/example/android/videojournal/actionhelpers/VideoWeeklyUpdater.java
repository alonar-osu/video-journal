package com.example.android.videojournal.actionhelpers;

import android.app.Activity;

import com.example.android.videojournal.data.VideoDatabase;
import com.example.android.videojournal.utilities.AppExecutors;

/**
 * Allows updating combined video from current week by deleting
 * existing video and adding a new merged video
 */
public class VideoWeeklyUpdater {

    private static final String TAG = VideoWeeklyUpdater.class.getSimpleName();

    public VideoWeeklyUpdater() { }

    /**
     * Updates weekly video by deleting current combined video, merging the
     * regular videos from this week, and adding the new combined video
     * to db
     * @param activity from where called
     * @param mDb instance of database to use
     */
    public static void updateWeeklyVideo(final Activity activity, final VideoDatabase mDb) {

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
