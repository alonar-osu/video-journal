package com.example.android.videojournal;

public class VideoEntry {

    private String mVideopath;
    private String mDate;
    private int mVideoHeight;
    private int mVideoWidth;

    public VideoEntry(String cVideopath, String cDate, int cVideoHeight, int cVideoWidth) {
        mVideopath = cVideopath;
        mDate = cDate;
        mVideoHeight = cVideoHeight;
        mVideoWidth = cVideoWidth;
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
}
