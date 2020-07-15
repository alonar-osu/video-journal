package com.example.android.videojournal.recyclerview;

import android.content.Context;
import android.graphics.Point;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.FrameLayout;

import com.example.android.videojournal.R;
import com.example.android.videojournal.data.VideoEntry;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// adapted from https://codingwithmitch.com/blog/playing-video-recyclerview-exoplayer-android/

public class VideoRecyclerView extends RecyclerView implements VideoListener {

    private static final String TAG = VideoRecyclerView.class.getSimpleName();

    private ImageView mThumbnailView;
    private ImageView mPlayIconView;
    private View mViewHolderParent;
    private FrameLayout mFrameLayout;
    private PlayerView mVideoSurfaceView;
    private SimpleExoPlayer mVideoPlayer;
    private int mVideoSurfaceDefaultHeight = 0;
    private int mScreenDefaultHeight = 0;
    private Context mContext;
    private int mPlayPosition = -1;
    private boolean mIsVideoViewAdded;
    private ArrayList<VideoEntry> mVideoEntries = new ArrayList<>();

    public VideoRecyclerView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public VideoRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {

        mContext = context.getApplicationContext();
        initVideoSurfaceSize();
        initVideoPlayer(context);

        // detect when first video frame rendered
        mVideoPlayer.getVideoComponent().addVideoListener(this);

        addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if(mThumbnailView != null) { // show old thumbnail
                        mThumbnailView.setVisibility(VISIBLE);
                        mPlayIconView.setVisibility(VISIBLE);
                    }
                    playVideo();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        addOnChildAttachStateChangeListener(new OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) { }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                if (mViewHolderParent != null && mViewHolderParent.equals(view)) {
                    resetVideoView();
                }
            }
        });

        mVideoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) { }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) { }

            @Override
            public void onLoadingChanged(boolean isLoading) { }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch(playbackState) {

                    case Player.STATE_BUFFERING:
                        Log.d(TAG, "onPlayerStateChanged: Buffering video");
                        break;

                    case Player.STATE_ENDED:
                        Log.d(TAG, "onPlayerStateChanged: Video ended");
                        mVideoPlayer.seekTo(0);
                        mThumbnailView.setVisibility(VISIBLE);
                        break;

                    case Player.STATE_IDLE:
                        break;

                    case Player.STATE_READY:
                        Log.d(TAG, "onPlayerStateChanged: Ready to play");

                        if(!mIsVideoViewAdded) {
                            addVideoView();
                        }
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) { }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) { }

            @Override
            public void onPlayerError(ExoPlaybackException error) { }

            @Override
            public void onPositionDiscontinuity(int reason) { }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) { }

            @Override
            public void onSeekProcessed() { }
        });
    }

    private void initVideoPlayer(Context context) {
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTracksSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTracksSelectionFactory);

        mVideoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
        mVideoSurfaceView.setUseController(false);
        mVideoSurfaceView.setPlayer(mVideoPlayer);
        mVideoPlayer.setVolume(0f); // mute
    }

    private void initVideoSurfaceSize() {
        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        mVideoSurfaceDefaultHeight = point.x;
        mScreenDefaultHeight = point.y;
        mVideoSurfaceView = new PlayerView(mContext);
        mVideoSurfaceView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
    }

    public void playVideo() {
        int startPosition = ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
        int endPosition = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
        if (startPosition < 0 || endPosition < 0) return; // some error

        int targetPosition;
        if (startPosition != endPosition) {
            // > 1 items on screen
            int startPositionVideoHeight = getVisibleVideoSurfaceHeight(startPosition);
            int endPositionVideoHeight = getVisibleVideoSurfaceHeight(endPosition);
            targetPosition = startPositionVideoHeight > endPositionVideoHeight ? startPosition : endPosition;
        } else { // 1 item on screen
            targetPosition = startPosition;
        }

        if (targetPosition == mPlayPosition) return; // video already playing
        mPlayPosition = targetPosition;
        if (mVideoSurfaceView == null) return;

        removeVideoView(mVideoSurfaceView); // remove any surfaceviews from previously playing videos

        int currentPosition = targetPosition - ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();

        if (!setViewsFromHolder(currentPosition)) return;
        mVideoSurfaceView.setPlayer(mVideoPlayer);
        prepareVideoSourceAndPlayer(targetPosition);
    }

    private void prepareVideoSourceAndPlayer(int targetPosition) {
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mContext,
                Util.getUserAgent(mContext, "Video Journal"));
        String mediaUrl = mVideoEntries.get(targetPosition).getVideopath();
        if (mediaUrl != null) {
            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(mediaUrl));
            mVideoPlayer.prepare(videoSource);
            mVideoPlayer.setPlayWhenReady(true);
        }
    }

    private boolean setViewsFromHolder(int currentPosition) {
        View child = getChildAt(currentPosition);
        if (child == null) return true;

        VideoViewHolder holder = (VideoViewHolder) child.getTag();
        if (holder == null) {
            mPlayPosition = -1;
            return false;
        }
        mThumbnailView = holder.mThumbnailView;
        mPlayIconView = holder.mPlayIconView;
        mViewHolderParent = holder.itemView;
        mFrameLayout = holder.itemView.findViewById(R.id.media_container);
        return true;
    }

    private int getVisibleVideoSurfaceHeight(int playPosition) {
        int at = playPosition - ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();

        View child = getChildAt(at);
        if (child == null) return 0;

        int[] location = new int[2];
        child.getLocationInWindow(location);

        if (location[1] < 0) {
            return location[1] + mVideoSurfaceDefaultHeight;
        } else {
            return mScreenDefaultHeight - location[1];
        }
    }

    private void removeVideoView(PlayerView videoView) {

        ViewGroup parent = (ViewGroup) videoView.getParent();
        if (parent == null) return;

        int index = parent.indexOfChild(videoView);
        if (index >= 0) {
            parent.removeViewAt(index);
            mIsVideoViewAdded = false;
        }
        mThumbnailView.setVisibility(VISIBLE);
        mPlayIconView.setVisibility(VISIBLE);
    }

    private void addVideoView() {

        mFrameLayout.addView(mVideoSurfaceView);
        mIsVideoViewAdded = true;
        mVideoSurfaceView.setElevation(0);
        mVideoSurfaceView.requestFocus();
        mVideoSurfaceView.setVisibility(VISIBLE);
        mVideoSurfaceView.setAlpha(1);
    }

    private void resetVideoView() {

        if (mIsVideoViewAdded) {
            removeVideoView(mVideoSurfaceView);
            mPlayPosition = -1;
            mThumbnailView.setVisibility(VISIBLE);
            mPlayIconView.setVisibility(VISIBLE);
            mVideoSurfaceView.setVisibility(INVISIBLE);
        }
    }

    public void releasePlayer() {
        if (mVideoPlayer != null) {
            mVideoPlayer.release();
            mVideoPlayer = null;
        }
        mViewHolderParent = null;

        if (mVideoPlayer != null) {
            mVideoPlayer.getVideoComponent().removeVideoListener(this);
        }
    }

    public void setVideoEntries(ArrayList<VideoEntry> videoEntries) {
        mVideoEntries = videoEntries;
    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
    }

    @Override
    public void onRenderedFirstFrame() {
        mThumbnailView.setVisibility(INVISIBLE);
        mPlayIconView.setVisibility(INVISIBLE);
    }


}
