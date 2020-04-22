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

    ArrayList videoPaths;
    Context context;

    public CustomAdapter(Context context, ArrayList videoPaths) {
       this.context = context;
       this.videoPaths = videoPaths;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowlayout, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Log.d(TAG, "#" + position);


        String videoPath = (String) videoPaths.get(position);
        Log.d(TAG, "Path is: " + videoPath);

        // obtain video width and height for thumbnail
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(videoPath);
        int videowidth = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        int videoheight = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        retriever.release();

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

    }

    @Override
    public int getItemCount() {
        return videoPaths.size();
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
