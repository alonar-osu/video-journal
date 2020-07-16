package com.example.android.videojournal.notifications;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.android.videojournal.activities.DailyVideoFeedActivity;

/**
 * Broadcast receiver for notifications service
 * to prompt user for daily videos
 */
public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = AlarmReceiver.class.getSimpleName();
    private static final int REC_NOTIF_ID = 100;

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent repeatingIntent = new Intent(context, DailyVideoFeedActivity.class);
        repeatingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, REC_NOTIF_ID,
                repeatingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        String title = "Time for Video Journal Entry";
        String contentText = "Let's take a new daily video";
        NotificationUtils.showNotification(context, pendingIntent, title, contentText);
    }

}
