package com.example.android.videojournal.recyclerview;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.videojournal.R;
import com.example.android.videojournal.data.VideoEntry;
import com.example.android.videojournal.formatting.DateFormater;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = VideoAdapter.class.getSimpleName();

    private static ArrayList mVideoEntries;
    private Context mContext;
    private boolean mWeeklyVideo;


    public VideoAdapter(Context context, ArrayList videoEntries, boolean weekly) {
        mContext = context;
        mVideoEntries = videoEntries;
        mWeeklyVideo = weekly;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.feed_entry, viewGroup, false);
        return new VideoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        VideoEntry videoEntry = (VideoEntry) mVideoEntries.get(position);
        showDateForVideo(videoEntry, (VideoViewHolder) holder);
        setVideoThumbnail(videoEntry, (VideoViewHolder) holder);
        setInfoToHolder(videoEntry, (VideoViewHolder) holder, position);
    }

    private void showDateForVideo(VideoEntry videoEntry, VideoViewHolder holder) {
        Date videoDate = videoEntry.getDate();
        String dateText = "";
        if (mWeeklyVideo) {
            dateText += "Week of " + DateFormater.precedingSundayDateAsString(videoDate);
        } else {
            dateText += DateFormater.dateToString(videoDate);
        }
        holder.mDateView.setText(dateText);
    }

    private void setVideoThumbnail(VideoEntry videoEntry, VideoViewHolder holder) {
        String thumbnailFileName = videoEntry.getThumbnailFileName();
        Bitmap videoThumbnail = loadBitmapFromStorage(thumbnailFileName, mContext);

        if (videoThumbnail != null) {
            holder.mThumbnailView.setAdjustViewBounds(true);
            holder.mThumbnailView.setImageBitmap(videoThumbnail);
        } else {
            Log.d(TAG, "Video thumbnail does not exist");
        }
    }

    private void setInfoToHolder(VideoEntry videoEntry, VideoViewHolder holder, final int position) {
        holder.mVideoPath = videoEntry.getVideopath();
        holder.mThumbnailPath = videoEntry.getThumbnailPath();
        holder.mItemPosition = position;
    }

    private Bitmap loadBitmapFromStorage(String filename, Context context) {
        Bitmap thumbnail = null;
        try {
            ContextWrapper cw = new ContextWrapper(context);
            File thumbnailPath = cw.getDir("thumbnails", Context.MODE_PRIVATE);
            File file = new File(thumbnailPath, filename);
            thumbnail = BitmapFactory.decodeStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Exception in loadBitmapFromStorage()");
            e.printStackTrace();
        }
        return thumbnail;
    }

    @Override
    public int getItemCount() {
        return mVideoEntries.size();
    }

    public static ArrayList<VideoEntry> getVideos() {
        return mVideoEntries;
    }

}
