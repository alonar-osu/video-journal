package com.example.android.videojournal.utilities;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;

public class AppExecutors {

    private static final Object LOCK = new Object();
    private static AppExecutors mInstance;
    private final Executor mDiskIO;
    private final Executor mMainThread;
    private final Executor mNetworkIO;

    private AppExecutors(Executor diskIO, Executor networkIO, Executor mainThread) {
        mDiskIO = diskIO;
        mNetworkIO = networkIO;
        mMainThread = mainThread;
    }

    public static AppExecutors getInstance() {
        if (mInstance == null) {
            synchronized (LOCK) {
                mInstance = new AppExecutors(Executors.newSingleThreadExecutor(),
                        Executors.newFixedThreadPool(3),
                        new MainThreadExecutor());
            }
        }
        return mInstance;
    }

    public Executor diskIO() {
        return mDiskIO;
    }

    public Executor mainThread() {
        return mMainThread;
    }

    public Executor networkIO() {
        return mNetworkIO;
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
