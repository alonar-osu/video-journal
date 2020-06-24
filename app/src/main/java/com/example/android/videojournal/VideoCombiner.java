package com.example.android.videojournal;


import android.content.Context;

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
    List<VideoEntry> mWeekAgoEntries;

    public VideoCombiner(Context context, AppDatabase db) {

        this.context = context;
        mDb = db;
    }

    public boolean haveVideos() {
        mWeekAgoEntries = mDb.videoDao().loadVideosForMerge(getPreviousWeekDate(), new Date());
        return mWeekAgoEntries.size() > 0;
    }

    public String combineVideosForWeek() {

        final String[] videos = new String[mWeekAgoEntries.size()];
        for (int i = 0; i < mWeekAgoEntries.size(); i++) {
            videos[i] = mWeekAgoEntries.get(i).getVideopath();
        }

        String mergedVideoPath = "";
        try {
            mergedVideoPath = mergeVideos(videos);
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

        if (audioTracks.size() > 0) {
            result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
        }
        if (videoTracks.size() > 0) {
            result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
        }

        String filename = FILE_START_NAME + todaysDateAsString() + VIDEO_EXTENSION;
        String videoCombinedPath = generateFilePath(filename);

        Container out = new DefaultMp4Builder().build(result);
        FileChannel fc = new RandomAccessFile(videoCombinedPath, "rw").getChannel();
        out.writeContainer(fc);
        fc.close();

        return videoCombinedPath;
    }

    private String generateFilePath(String fileName) {

        File[] externalStorageVolumes =
                ContextCompat.getExternalFilesDirs(context, null);
        File primaryExternalStorage = externalStorageVolumes[0];
        String dirPath = "" + primaryExternalStorage;

        String filePath = dirPath + "/" + fileName;
        return filePath;
    }

    private String todaysDateAsString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        return sdf.format(new Date());
    }

    public static Date getPreviousWeekDate(){
        return new Date(System.currentTimeMillis()-7*24*60*60*1000);
    }


}
