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
    static final String CHANNEL_ID_REC_NOTIF = "record_reminder";
    private int REC_NOTIF_ID = 100;


    @Override
    public void onReceive(Context context, Intent intent) {

        Intent repeatingIntent = new Intent(context, DailyVideoFeedActivity.class);
        repeatingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //called activity will replace current

        PendingIntent pendingIntent = PendingIntent.getActivity(context, REC_NOTIF_ID, repeatingIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_REC_NOTIF)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_create_entry)
                .setContentTitle("Time for Video Journal Entry")
                .setContentText("Let's take a new daily video")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(REC_NOTIF_ID, builder.build());
    }

    }
