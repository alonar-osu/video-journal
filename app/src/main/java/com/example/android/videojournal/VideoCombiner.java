package com.example.android.videojournal;


import android.content.Context;
import android.util.Log;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import androidx.core.content.ContextCompat;


public class VideoCombiner {

    private static final String TAG = VideoCombiner.class.getSimpleName();
    public final static String FILE_START_NAME = "vj";
    public final static String VIDEO_EXTENSION = ".mp4";

    Context context;
    private AppDatabase mDb;

    public VideoCombiner(Context context, AppDatabase db) {

        this.context = context;
        mDb = db;
    }

    public String combineVideosForWeek() {

        final List<VideoEntry> weekAgoEntries = mDb.videoDao().loadVideosForMerge(getPreviousWeekDate(), new Date());
        final String[] videos = new String[weekAgoEntries.size()];
        for (int i = 0; i < weekAgoEntries.size(); i++) {
            videos[i] = weekAgoEntries.get(i).getVideopath();
        }

        String mergedVideoPath = "";
        // merge videos
        try {
            mergedVideoPath = mergeVideos(videos);
            Log.d(TAG, "Merging: resulting combined video path= " + mergedVideoPath);
            return mergedVideoPath;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mergedVideoPath;
    }

    private String mergeVideos(String[] videos) throws IOException {

        Movie[] inMovies = new Movie[videos.length];
        int index = 0;
        for (String video: videos) {
            Log.d(TAG, "Merging: vid path=" + video);
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
        Log.d(TAG, "Merging: audioTracks size = " + audioTracks.size() + " videoTracks size = " + videoTracks.size());

        if (audioTracks.size() > 0) {
            result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
        }
        if (videoTracks.size() > 0) {
            result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
        }

        String filename = FILE_START_NAME + todaysDateAsString() + VIDEO_EXTENSION;
        String videoCombinedPath = generateFilePath(filename);
        Log.d(TAG, "Merging: videoCombinedPath= " + videoCombinedPath);

        Container out = new DefaultMp4Builder().build(result);
        Log.d(TAG, "Merging: before FileChannel fc");
        FileChannel fc = new RandomAccessFile(videoCombinedPath, "rw").getChannel();
        Log.d(TAG, "Merging: before out.writeContainer");
        out.writeContainer(fc);
        fc.close();

        Log.d(TAG, "Merging: at end of combining videos");

        return videoCombinedPath;
    }

    private String generateFilePath(String fileName) {

        Log.d(TAG, "Merging: in generateFilePath() method");

        File[] externalStorageVolumes =
                ContextCompat.getExternalFilesDirs(context, null);
        File primaryExternalStorage = externalStorageVolumes[0];
        Log.d(TAG, "Merging: primaryExternalStorage = " + primaryExternalStorage);
        String dirPath = "" + primaryExternalStorage;

        String filePath = dirPath + "/" + fileName;
        return filePath;
    }

    private String todaysDateAsString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        return sdf.format(new Date());
    }

    private Date getPreviousWeekDate(){
        return new Date(System.currentTimeMillis()-7*24*60*60*1000);
    }


}
