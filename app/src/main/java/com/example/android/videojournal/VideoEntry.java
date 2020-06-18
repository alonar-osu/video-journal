package com.example.android.videojournal;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "journalvideo")
public class VideoEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String videopath;
    private Date date;
    private int videoHeight;
    private int videoWidth;
    private String thumbnailPath;
    private String thumbnailFileName;
    private int combinedVideo;

    @Ignore
    public VideoEntry(String videopath, Date date, int videoHeight, int videoWidth, String thumbnailPath, String thumbnailFileName, int combinedVideo) {
        this.videopath = videopath;
        this.date = date;
        this.videoHeight = videoHeight;
        this.videoWidth = videoWidth;
        this.thumbnailPath = thumbnailPath;
        this.thumbnailFileName = thumbnailFileName;
        this.combinedVideo = combinedVideo;
    }

    public VideoEntry(int id, String videopath, Date date, int videoHeight, int videoWidth, String thumbnailPath, String thumbnailFileName, int combinedVideo) {
        this.id = id;
        this.videopath = videopath;
        this.date = date;
        this.videoHeight = videoHeight;
        this.videoWidth = videoWidth;
        this.thumbnailPath = thumbnailPath;
        this.thumbnailFileName = thumbnailFileName;
        this.combinedVideo = combinedVideo;
    }

    public int getId() {return id; }

    public String getVideopath() {
        return videopath;
    }

    public Date getDate() {
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

    public int getCombinedVideo() {
        return combinedVideo;
    }
}
