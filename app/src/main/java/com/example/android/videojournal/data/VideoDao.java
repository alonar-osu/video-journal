package com.example.android.videojournal.data;


import java.util.Date;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface VideoDao {

    @Query("SELECT * FROM journalvideo WHERE mCombinedVideo = 0")
    LiveData<List<VideoEntry>> loadAllNonCombinedVideos();

    @Query("SELECT * FROM journalvideo WHERE mCombinedVideo = 1")
    LiveData<List<VideoEntry>> loadAllCombinedVideos();

    @Query("SELECT * FROM journalvideo WHERE mDate BETWEEN :daystart AND :dayend AND mCombinedVideo = 0")
    List<VideoEntry> loadVideosForMerge(Date daystart, Date dayend);

    @Query("SELECT * FROM journalvideo WHERE mDate BETWEEN :daystart AND :dayend AND mCombinedVideo = 1")
    VideoEntry loadCurrentCombinedVideo(Date daystart, Date dayend);

    @Insert
    void insertVideo(VideoEntry videoEntry);

    @Delete
    void deleteVideo(VideoEntry videoEntry);

}


