package com.example.android.videojournal;

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

    private ImageView thumbnailView;
    private ImageView playIconView;
    private View viewHolderParent;
    private FrameLayout frameLayout;
    private PlayerView videoSurfaceView;
    private SimpleExoPlayer videoPlayer;

    private ArrayList<VideoEntry> mVideoEntries = new ArrayList<>();
    private int videoSurfaceDefaultHeight = 0;
    private int screenDefaultHeight = 0;
    private Context context;
    private int playPosition = -1;
    private boolean isVideoViewAdded;

    public VideoRecyclerView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public VideoRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {

        this.context = context.getApplicationContext();
        initVideoSurfaceSize();
        initVideoPlayer(context);

        // detect when first video frame rendered
        videoPlayer.getVideoComponent().addVideoListener(this);

        addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if(thumbnailView != null) { // show old thumbnail
                        thumbnailView.setVisibility(VISIBLE);
                        playIconView.setVisibility(VISIBLE);
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
                if (viewHolderParent != null && viewHolderParent.equals(view)) {
                    resetVideoView();
                }
            }
        });

        videoPlayer.addListener(new Player.EventListener() {
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
                        videoPlayer.seekTo(0);
                        thumbnailView.setVisibility(VISIBLE);
                        break;

                    case Player.STATE_IDLE:
                        break;

                    case Player.STATE_READY:
                        Log.d(TAG, "onPlayerStateChanged: Ready to play");

                        if(!isVideoViewAdded) {
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

        videoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
        videoSurfaceView.setUseController(false);
        videoSurfaceView.setPlayer(videoPlayer);
        videoPlayer.setVolume(0f); // mute
    }

    private void initVideoSurfaceSize() {
        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        videoSurfaceDefaultHeight = point.x;
        screenDefaultHeight = point.y;
        videoSurfaceView = new PlayerView(this.context);
        videoSurfaceView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
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

        if (targetPosition == playPosition) return; // video already playing
        playPosition = targetPosition;
        if (videoSurfaceView == null) return;

        removeVideoView(videoSurfaceView); // remove any surfaceviews from previously playing videos

        int currentPosition = targetPosition - ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();

        if (!setViewsFromHolder(currentPosition)) return;
        videoSurfaceView.setPlayer(videoPlayer);
        prepareVideoSourceAndPlayer(targetPosition);
    }

    private void prepareVideoSourceAndPlayer(int targetPosition) {
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, "Video Journal"));
        String mediaUrl = mVideoEntries.get(targetPosition).getVideopath();
        if (mediaUrl != null) {
            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(mediaUrl));
            videoPlayer.prepare(videoSource);
            videoPlayer.setPlayWhenReady(true);
        }
    }

    private boolean setViewsFromHolder(int currentPosition) {
        View child = getChildAt(currentPosition);
        if (child == null) return true;

        VideoViewHolder holder = (VideoViewHolder) child.getTag();
        if (holder == null) {
            playPosition = -1;
            return false;
        }
        thumbnailView = holder.thumbnailView;
        playIconView = holder.playIconView;
        viewHolderParent = holder.itemView;
        frameLayout = holder.itemView.findViewById(R.id.media_container);
        return true;
    }

    private int getVisibleVideoSurfaceHeight(int playPosition) {
        int at = playPosition - ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();

        View child = getChildAt(at);
        if (child == null) return 0;

        int[] location = new int[2];
        child.getLocationInWindow(location);

        if (location[1] < 0) {
            return location[1] + videoSurfaceDefaultHeight;
        } else {
            return screenDefaultHeight - location[1];
        }
    }

    private void removeVideoView(PlayerView videoView) {

        ViewGroup parent = (ViewGroup) videoView.getParent();
        if (parent == null) return;

        int index = parent.indexOfChild(videoView);
        if (index >= 0) {
            parent.removeViewAt(index);
            isVideoViewAdded = false;
        }
        thumbnailView.setVisibility(VISIBLE);
        playIconView.setVisibility(VISIBLE);
    }

    private void addVideoView() {

        frameLayout.addView(videoSurfaceView);
        isVideoViewAdded = true;
        videoSurfaceView.setElevation(0);
        videoSurfaceView.requestFocus();
        videoSurfaceView.setVisibility(VISIBLE);
        videoSurfaceView.setAlpha(1);
    }

    private void resetVideoView() {

        if (isVideoViewAdded) {
            removeVideoView(videoSurfaceView);
            playPosition = -1;
            thumbnailView.setVisibility(VISIBLE);
            playIconView.setVisibility(VISIBLE);
            videoSurfaceView.setVisibility(INVISIBLE);
        }
    }

    public void releasePlayer() {
        if (videoPlayer != null) {
            videoPlayer.release();
            videoPlayer = null;
        }
        viewHolderParent = null;

        if (videoPlayer != null) {
            videoPlayer.getVideoComponent().removeVideoListener(this);
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
        thumbnailView.setVisibility(INVISIBLE);
        playIconView.setVisibility(INVISIBLE);
    }


}
