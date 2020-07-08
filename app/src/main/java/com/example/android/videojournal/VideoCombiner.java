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
    public final static String FILE_START_NAME = "comb_vj";
    public final static String VIDEO_EXTENSION = ".mp4";

    Context context;
    private AppDatabase mDb;
    List<VideoEntry> mWeekAgoEntries;

    public VideoCombiner(Context context, AppDatabase db) {

        this.context = context;
        mDb = db;
    }

    public boolean haveVideos() {
        Log.d(TAG, "crash debug: in haveVideos()");
        mWeekAgoEntries = mDb.videoDao().loadVideosForMerge(getPreviousWeekDate(), new Date());
        return mWeekAgoEntries.size() > 0;
    }

    public String combineVideosForWeek() {
        Log.d(TAG, "crash debug: in combineVideosForWeek() 1");

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
        Log.d(TAG, "crash debug: in combineVideosForWeek() 2");
        return mergedVideoPath;
    }

    private String mergeVideos(String[] videos) throws IOException {
        Log.d(TAG, "crash debug: in mergeVideos() 1");
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
        Log.d(TAG, "crash debug: in mergeVideos() 2");
        return videoCombinedPath;
    }

    private String generateFilePath(String fileName) {
        Log.d(TAG, "crash debug: generateFilePath() 1");
        File[] externalStorageVolumes =
                ContextCompat.getExternalFilesDirs(context, null);
        File primaryExternalStorage = externalStorageVolumes[0];
        String dirPath = "" + primaryExternalStorage;

        String filePath = dirPath + "/" + fileName;
        Log.d(TAG, "crash debug: generateFilePath() 2");
        return filePath;
    }

    private String todaysDateAsString() {
        Log.d(TAG, "crash debug: todaysDateAsString() 1");
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss", Locale.getDefault());
        return sdf.format(new Date());
    }

    public static Date getPreviousWeekDate(){
        Log.d(TAG, "crash debug: getPreviousWeekDate() 1");
        return new Date(System.currentTimeMillis()-7*24*60*60*1000);
    }


}
