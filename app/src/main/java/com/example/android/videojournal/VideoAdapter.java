package com.example.android.videojournal;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;


import androidx.recyclerview.widget.RecyclerView;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.MyViewHolder> {

    private static final String TAG = VideoAdapter.class.getSimpleName();

    ArrayList mVideoEntries;
    Context context;

    public VideoAdapter(Context context, ArrayList videoEntries) {
       this.context = context;
       this.mVideoEntries = videoEntries;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowlayout, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        // DEBUGGING: showing view's number
        Log.d(TAG, "view #" + position);
        // DEBUGGING: getting time in onBindViewHolder method
        long startTime = System.currentTimeMillis();

        // get video info
        VideoEntry videoEntry = (VideoEntry) mVideoEntries.get(position);

        String videoPath = videoEntry.getVideopath();
        // get video thumbnail from internal storage
        String thumbnailFileName = videoEntry.getmThumbnailFileName();
        Bitmap videoThumbnail = loadBitmapFromStorage(thumbnailFileName, context);

        if (videoThumbnail != null) {
            holder.image.setImageBitmap(videoThumbnail);
        } else {
            Log.d(TAG, "Video thumbnail does not exist");
        }
        holder.videoPath = videoPath;

        // DEBUGGING: getting time in onBindViewHolder method
        Log.i(TAG, "bindView time: " + (System.currentTimeMillis() - startTime));
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

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        String videoPath;

        public MyViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, PlayVideoActivity.class);
                    intent.putExtra("VIDEO_PATH", videoPath);
                    context.startActivity(intent);
                }
            });
        }




        /*
        void bind (int position) {
           // image.setImageResource(galleryImages.get(position));
            image.setImageResource(dinoImages.get(position));
        }
        */
    }

}
