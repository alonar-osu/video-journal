package com.example.android.videojournal;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

public class VideoViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = VideoViewHolder.class.getSimpleName();

    RelativeLayout media_container;
    View parent;
    ImageView thumbnailView;
    String videoPath;

    public VideoViewHolder(View itemView) {
        super(itemView);
        parent = itemView;
        media_container = itemView.findViewById(R.id.media_container);
        thumbnailView = (ImageView) itemView.findViewById(R.id.thumbnail);

        itemView.setTag(this);

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
}