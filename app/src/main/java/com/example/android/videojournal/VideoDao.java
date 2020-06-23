package com.example.android.videojournal;


import java.util.Date;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface VideoDao {

    @Query("SELECT * FROM journalvideo")
    LiveData<List<VideoEntry>> loadAllVideos();

    @Query("SELECT * FROM journalvideo WHERE date BETWEEN :daystart AND :dayend AND combinedVideo = 0")
    List<VideoEntry> loadVideosForMerge(Date daystart, Date dayend);


    @Insert
    void insertVideo(VideoEntry videoEntry);

    @Delete
    void deleteVideo(VideoEntry videoEntry);

}


