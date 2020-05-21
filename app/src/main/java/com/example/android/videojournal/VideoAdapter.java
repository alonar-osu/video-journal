package com.example.android.videojournal;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = VideoAdapter.class.getSimpleName();

    ArrayList mVideoEntries;
    Context context;

    public VideoAdapter(Context context, ArrayList videoEntries) {
       this.context = context;
       this.mVideoEntries = videoEntries;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rowlayout, viewGroup, false);
        return new VideoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        Log.d(TAG, "view #" + position); // view's number
        long startTime = System.currentTimeMillis(); // getting time in onBindViewHolder method

        // get video info
        VideoEntry videoEntry = (VideoEntry) mVideoEntries.get(position);

        String videoPath = videoEntry.getVideopath();
        // get video thumbnail from internal storage
        String thumbnailFileName = videoEntry.getmThumbnailFileName();
        Bitmap videoThumbnail = loadBitmapFromStorage(thumbnailFileName, context);

        if (videoThumbnail != null) {
            ((VideoViewHolder) holder).thumbnailView.setImageBitmap(videoThumbnail);
        } else {
            Log.d(TAG, "Video thumbnail does not exist");
        }
        ((VideoViewHolder) holder).videoPath = videoPath;

        Log.i(TAG, "bindView time: " + (System.currentTimeMillis() - startTime)); // time in method
    }

    @Override
    public int getItemCount() {
        return mVideoEntries.size();
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
