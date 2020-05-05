package com.example.android.videojournal;

public class VideoEntry {

    private String mVideopath;
    private String mDate;
    private int mVideoHeight;
    private int mVideoWidth;
    private String mThumbnailPath;
    private String mThumbnailFileName;

    public VideoEntry(String cVideopath, String cDate, int cVideoHeight, int cVideoWidth, String cThumbnailPath, String cThumbnailFileName) {
        mVideopath = cVideopath;
        mDate = cDate;
        mVideoHeight = cVideoHeight;
        mVideoWidth = cVideoWidth;
        mThumbnailPath = cThumbnailPath;
        mThumbnailFileName = cThumbnailFileName;
    }

    public String getVideopath() {
        return mVideopath;
    }

    public String getDate() {
        return mDate;
    }

    public int getVideoHeight() {
        return mVideoHeight;
    }

    public int getmVideoWidth() {
        return mVideoWidth;
    }

    public String getmThumbnailPath() {return mThumbnailPath; }

    public String getmThumbnailFileName() {return mThumbnailFileName; }
}
