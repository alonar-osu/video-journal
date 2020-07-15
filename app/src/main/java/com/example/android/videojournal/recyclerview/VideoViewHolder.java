package com.example.android.videojournal.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.videojournal.R;
import com.example.android.videojournal.activities.PlayVideoActivity;

import androidx.recyclerview.widget.RecyclerView;

public class VideoViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = VideoViewHolder.class.getSimpleName();

    private FrameLayout mMedia_container;
    private View mParent;
    ImageView mThumbnailView;
    ImageView mPlayIconView;
    TextView mDateView;
    String mVideoPath;
    String mThumbnailPath;
    int mItemPosition;

    VideoViewHolder(View itemView) {
        super(itemView);
        mParent = itemView;
        mMedia_container = itemView.findViewById(R.id.media_container);
        mThumbnailView = (ImageView) itemView.findViewById(R.id.thumbnail);
        mPlayIconView = (ImageView) itemView.findViewById(R.id.play_icon);
        mDateView = (TextView) itemView.findViewById(R.id.date_tv);
        launchVideoWhenClicked(itemView);
    }

    private void launchVideoWhenClicked(View itemView) {
        itemView.setTag(this);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, PlayVideoActivity.class);
                intent.putExtra("VIDEO_PATH", mVideoPath);
                intent.putExtra("THUMBNAIL_PATH", mThumbnailPath);
                intent.putExtra("POSITION", mItemPosition);
                context.startActivity(intent);
            }
        });
    }

}