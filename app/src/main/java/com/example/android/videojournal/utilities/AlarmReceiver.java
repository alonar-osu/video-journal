package com.example.android.videojournal.utilities;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.android.videojournal.DailyVideoFeedActivity;
import com.example.android.videojournal.R;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;


public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = AlarmReceiver.class.getSimpleName();
    private static int REC_NOTIF_ID = 100;

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent repeatingIntent = new Intent(context, DailyVideoFeedActivity.class);
        repeatingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, REC_NOTIF_ID,
                repeatingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationUtils.showNotification(context, pendingIntent);
    }

}
