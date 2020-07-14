package com.example.android.videojournal;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.videojournal.data.VideoEntry;
import com.example.android.videojournal.formatting.DateConverter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = VideoAdapter.class.getSimpleName();

    static ArrayList mVideoEntries;
    Context context;
    boolean mWeeklyVideo;


    public VideoAdapter(Context context, ArrayList videoEntries, boolean weekly) {
       this.context = context;
       this.mVideoEntries = videoEntries;
       this.mWeeklyVideo = weekly;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.feed_entry, viewGroup, false);
        return new VideoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        Log.d(TAG, "view #" + position); // view's number
        long startTime = System.currentTimeMillis(); // getting time in onBindViewHolder method

        // get video info
        VideoEntry videoEntry = (VideoEntry) mVideoEntries.get(position);
        String videoPath = videoEntry.getVideopath();

        // date to show
        Date videoDate = videoEntry.getDate();
        String dateText = "";
        if (mWeeklyVideo) {
            dateText += "Week of " + DateConverter.precedingSundayDateAsString(videoDate);
        } else {
            dateText += DateConverter.dateToString(videoDate);
        }

        ((VideoViewHolder) holder).dateView.setText(dateText);

        // get video thumbnail from internal storage
        String thumbnailFileName = videoEntry.getThumbnailFileName();
        Bitmap videoThumbnail = loadBitmapFromStorage(thumbnailFileName, context);

        if (videoThumbnail != null) {
            ((VideoViewHolder) holder).thumbnailView.setAdjustViewBounds(true);
            // set bitmap thumbnail
            ((VideoViewHolder) holder).thumbnailView.setImageBitmap(videoThumbnail);
        } else {
            Log.d(TAG, "Video thumbnail does not exist");
        }
        ((VideoViewHolder) holder).videoPath = videoPath;
        ((VideoViewHolder) holder).thumbnailPath = videoEntry.getThumbnailPath();
        ((VideoViewHolder) holder).position = position;

        Log.i(TAG, "bindView time: " + (System.currentTimeMillis() - startTime)); // time in method
    }

    @Override
    public int getItemCount() {
        return mVideoEntries.size();
    }

    public static ArrayList<VideoEntry> getVideos() {
        return mVideoEntries;
    }

    private Bitmap loadBitmapFromStorage(String filename, Context context) {
        Bitmap thumbnail = null;
        try {
            ContextWrapper cw = new ContextWrapper(context);
            File thumbnailPath = cw.getDir("thumbnails", Context.MODE_PRIVATE);
            File file = new File(thumbnailPath, filename);
            thumbnail = BitmapFactory.decodeStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return thumbnail;
    }

}
