package com.example.android.videojournal;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "journalvideo")
public class VideoEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String videopath;
    private String date;
    private int videoHeight;
    private int videoWidth;
    private String thumbnailPath;
    private String thumbnailFileName;

    @Ignore
    public VideoEntry(String videopath, String date, int videoHeight, int videoWidth, String thumbnailPath, String thumbnailFileName) {
        this.videopath = videopath;
        this.date = date;
        this.videoHeight = videoHeight;
        this.videoWidth = videoWidth;
        this.thumbnailPath = thumbnailPath;
        this.thumbnailFileName = thumbnailFileName;
    }

    public VideoEntry(int id, String videopath, String date, int videoHeight, int videoWidth, String thumbnailPath, String thumbnailFileName) {
        this.id = id;
        this.videopath = videopath;
        this.date = date;
        this.videoHeight = videoHeight;
        this.videoWidth = videoWidth;
        this.thumbnailPath = thumbnailPath;
        this.thumbnailFileName = thumbnailFileName;
    }

    public int getId() {return id; }

    public String getVideopath() {
        return videopath;
    }

    public String getDate() {
        return date;
    }

    public int getVideoHeight() {
        return videoHeight;
    }

    public int getVideoWidth() {
        return videoWidth;
    }

    public String getThumbnailPath() {return thumbnailPath; }

    public String getThumbnailFileName() {return thumbnailFileName; }
}
