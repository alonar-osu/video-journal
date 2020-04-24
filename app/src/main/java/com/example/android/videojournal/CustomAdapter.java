package com.example.android.videojournal;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.util.ArrayList;


import androidx.recyclerview.widget.RecyclerView;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private static final String TAG = CustomAdapter.class.getSimpleName();

    ArrayList mVideoEntries;
    Context context;

    public CustomAdapter(Context context, ArrayList videoEntries) {
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
        Log.d(TAG, "#" + position);
        // debugging slow scrolling in recyclerview - looks like doing too much work in this method
        long startTime = System.currentTimeMillis();

        // TODO: see if need to make this method faster. debug slow scrolling on first run of app
        VideoEntry videoEntry = (VideoEntry) mVideoEntries.get(position);

        String videoPath = videoEntry.getVideopath();
        Log.d(TAG, "Path is: " + videoPath);
        int videoheight = videoEntry.getVideoHeight();
        int videowidth = videoEntry.getmVideoWidth();

        Bitmap bitmap = null;
        try {
           bitmap = ThumbnailUtils.createVideoThumbnail(videoPath,  MediaStore.Images.Thumbnails.MINI_KIND);

        } catch (Exception e) {
            Log.d(TAG, "Exception happened when getting bitmap");
        }
        if (bitmap != null) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, videowidth, videoheight,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
            holder.image.setImageBitmap(bitmap);
        } else {
            Log.d(TAG, "Video thumbnail does not exist");
        }

        // debugging
        Log.i(TAG, "bindView time: " + (System.currentTimeMillis() - startTime));
    }

    @Override
    public int getItemCount() {
        return mVideoEntries.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        public MyViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
        }

        /*
        void bind (int position) {
           // image.setImageResource(galleryImages.get(position));
            image.setImageResource(dinoImages.get(position));
        }
        */
    }

}
