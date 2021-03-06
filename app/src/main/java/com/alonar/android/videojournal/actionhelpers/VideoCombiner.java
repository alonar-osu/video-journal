package com.alonar.android.videojournal.actionhelpers;

import android.content.Context;
import android.util.Log;

import com.coremedia.iso.boxes.Container;
import com.alonar.android.videojournal.data.VideoDatabase;
import com.alonar.android.videojournal.data.VideoEntry;
import com.alonar.android.videojournal.formatting.DateFormatter;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import androidx.core.content.ContextCompat;

import static com.alonar.android.videojournal.utilities.Constants.WEEKLY_FILE_START_NAME;
import static com.alonar.android.videojournal.utilities.Constants.VIDEO_EXTENSION;

/**
 * Allows combining regular videos recorded during the week since last Sunday
 * and currently in db into one weekly video
 */
public class VideoCombiner {

    private static final String TAG = VideoCombiner.class.getSimpleName();

    private final VideoDatabase mDb;
    private final Context mContext;
    private List<VideoEntry> mWeekAgoEntries;

    public VideoCombiner(Context context, VideoDatabase db) {
        mContext = context;
        mDb = db;
    }

    /**
     * Number of regular videos in the past week
     * @return number of regular non-merged videos recorded since last Sunday
     * and currently in db
     */
    public int thisWeeksVideoCount() {
        getThisWeeksVideos();
        return mWeekAgoEntries.size();
    }

    /**
     * Fetches paths for all regular videos from this week from db
     * and merges videos using mp4parser library
     * @return absolute path to merged video file
     */
    public String combineVideosForWeek() {

        if (mWeekAgoEntries == null) getThisWeeksVideos();

        final String[] videos = new String[mWeekAgoEntries.size()];
        for (int i = 0; i < mWeekAgoEntries.size(); i++) {
            videos[i] = mWeekAgoEntries.get(i).getVideopath();
        }

        String mergedVideoPath = "";
        try {
            mergedVideoPath = mergeVideos(videos);
            return mergedVideoPath;
        } catch (IOException e) {
            Log.e(TAG, "Exception in combineVideosForWeek()");
            e.printStackTrace();
        }
        return mergedVideoPath;
    }

    private String mergeVideos(String[] videos) throws IOException {

        return saveVideoFile(buildCombinedVideo(videos));
    }

    private Movie buildCombinedVideo(String[] videos) throws IOException {
        Movie[] inMovies = new Movie[videos.length];
        int index = 0;
        for (String video: videos) {
            inMovies[index] = MovieCreator.build(video);
            index++;
        }
        List<Track> videoTracks = new LinkedList<Track>();
        List<Track> audioTracks = new LinkedList<Track>();
        for (Movie m : inMovies) {
            for (Track t : m.getTracks()) {
                if (t.getHandler().equals("soun")) {
                    audioTracks.add(t);
                }
                if (t.getHandler().equals("vide")) {
                    videoTracks.add(t);
                }
            }
        }
        Movie result = new Movie();
        result = addVideoTracks(result, audioTracks);
        result = addVideoTracks(result, videoTracks);
        return result;
    }

    private Movie addVideoTracks(Movie movie, List<Track> tracks) throws IOException {
        if (tracks.size() > 0) {
            movie.addTrack(new AppendTrack(tracks.toArray(new Track[tracks.size()])));
        }
        return movie;
    }

    private String saveVideoFile(Movie result) throws IOException {
        String filename = WEEKLY_FILE_START_NAME +
                DateFormatter.todaysDateForFileNameAsString() + VIDEO_EXTENSION;
        String videoCombinedPath = generateFilePath(filename);

        Container out = new DefaultMp4Builder().build(result);
        FileChannel fc = new RandomAccessFile(videoCombinedPath, "rw").getChannel();
        out.writeContainer(fc);
        fc.close();
        return videoCombinedPath;
    }


    private void getThisWeeksVideos() {
        Date today = new Date();
        Date precedingSunday = DateFormatter.precedingSundayDate(today);
        mWeekAgoEntries = mDb.videoDao().loadVideosForMerge(precedingSunday, today);
    }

    private String generateFilePath(String fileName) {
        File[] externalStorageVolumes =
                ContextCompat.getExternalFilesDirs(mContext, null);
        File primaryExternalStorage = externalStorageVolumes[0];
        String dirPath = "" + primaryExternalStorage;
        return dirPath + "/" + fileName;
    }

}
