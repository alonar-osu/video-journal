package com.example.android.videojournal.data;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import java.util.Date;

/**
 * VideoEntry class allows making objects for storing video-related info.
 * VideoEntry objects are also used for db DAO using Room
 */
@Entity(tableName = "journalvideo")
public class VideoEntry {

    @PrimaryKey(autoGenerate = true)
    private int mId;
    private String mVideopath;
    private Date mDate;
    private int mVideoHeight;
    private int mVideoWidth;
    private String mThumbnailPath;
    private String mThumbnailFileName;
    private int mCombinedVideo; // 1 = combined

    @Ignore
    public VideoEntry(String videopath, Date date, int videoHeight, int videoWidth, String thumbnailPath, String thumbnailFileName, int combinedVideo) {
        mVideopath = videopath;
        mDate = date;
        mVideoHeight = videoHeight;
        mVideoWidth = videoWidth;
        mThumbnailPath = thumbnailPath;
        mThumbnailFileName = thumbnailFileName;
        mCombinedVideo = combinedVideo;
    }

    // includes mId
    public VideoEntry(int id, String videopath, Date date, int videoHeight, int videoWidth, String thumbnailPath, String thumbnailFileName, int combinedVideo) {
        mId = id;
        mVideopath = videopath;
        mDate = date;
        mVideoHeight = videoHeight;
        mVideoWidth = videoWidth;
        mThumbnailPath = thumbnailPath;
        mThumbnailFileName = thumbnailFileName;
        mCombinedVideo = combinedVideo;
    }

    public int getId() {return mId; }

    public String getVideopath() {
        return mVideopath;
    }

    public Date getDate() {
        return mDate;
    }

    public int getVideoHeight() {
        return mVideoHeight;
    }

    public int getVideoWidth() {
        return mVideoWidth;
    }

    public String getThumbnailPath() {return mThumbnailPath; }

    public String getThumbnailFileName() {return mThumbnailFileName; }

    public int getCombinedVideo() {
        return mCombinedVideo;
    }
}
