package com.alonar.android.videojournal.data;


import java.util.Date;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

/**
 * Interface for db using Room persistence library with
 * data access object (DAO)
 */

@Dao
public interface VideoDao {

    /**
     * Retrieves all regular non-combined videos from db
     * Uses LiveData observer to update on changes
     * @return list of VideoEntry objects
     */
    @Query("SELECT * FROM journalvideo WHERE mCombinedVideo = 0")
    LiveData<List<VideoEntry>> loadAllNonCombinedVideos();

    /**
     * Retrieves all combined weekly videos from db
     * Uses LiveData observer to update on changes
     * @return list of VideoEntry objects
     */
    @Query("SELECT * FROM journalvideo WHERE mCombinedVideo = 1")
    LiveData<List<VideoEntry>> loadAllCombinedVideos();

    /**
     * Retrieves all regular non-combined videos from database that were added
     * in a given period (last week for merge)
     * @param daystart should be last Sunday, for merge of week's videos
     * @param dayend should be today's date, for merge of week's videos
     * @return list of VideoEntry objects
     */
    @Query("SELECT * FROM journalvideo WHERE mDate BETWEEN :daystart AND :dayend AND mCombinedVideo = 0")
    List<VideoEntry> loadVideosForMerge(Date daystart, Date dayend);

    /**
     * Retrieves combined weekly video from db added in a given period (last week for merge).
     * There should be 1 combined video per week
     * @param daystart should be last Sunday, for weekly video
     * @param dayend should be today's date, for weekly video
     * @return VideoEntry object with weekly video's info
     */
    @Query("SELECT * FROM journalvideo WHERE mDate BETWEEN :daystart AND :dayend AND mCombinedVideo = 1")
    VideoEntry loadCurrentCombinedVideo(Date daystart, Date dayend);

    /**
     * Add video info to db using VideoEntry object
     * @param videoEntry contains video info
     */
    @Insert
    void insertVideo(VideoEntry videoEntry);

    /**
     * Delete specific video from db
     * @param videoEntry is for video to delete
     */
    @Delete
    void deleteVideo(VideoEntry videoEntry);

}


