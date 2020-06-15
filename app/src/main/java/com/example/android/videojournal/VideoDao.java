package com.example.android.videojournal;


import java.util.ArrayList;
import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface VideoDao {

    @Query("SELECT * FROM journalvideo")
    List<VideoEntry> loadAllVideos();

    @Insert
    void insertVideo(VideoEntry videoEntry);

    @Delete
    void deleteVideo(VideoEntry videoEntry);

}
