package com.example.android.videojournal;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

public class VideoViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = VideoViewHolder.class.getSimpleName();

    FrameLayout media_container;
    View parent;
    ImageView thumbnailView;
    ImageView playIconView;
    String videoPath;
    String thumbnailPath;
    int position;

    public VideoViewHolder(View itemView) {
        super(itemView);
        parent = itemView;
        media_container = itemView.findViewById(R.id.media_container);
        thumbnailView = (ImageView) itemView.findViewById(R.id.thumbnail);
        playIconView = (ImageView) itemView.findViewById(R.id.play_icon);

        itemView.setTag(this);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, PlayVideoActivity.class);
                intent.putExtra("VIDEO_PATH", videoPath);
                intent.putExtra("THUMBNAIL_PATH", thumbnailPath);
                intent.putExtra("POSITION", position);
                context.startActivity(intent);
            }
        });
    }
}