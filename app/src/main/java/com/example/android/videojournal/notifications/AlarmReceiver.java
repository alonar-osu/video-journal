package com.example.android.videojournal.notifications;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.android.videojournal.R;
import com.example.android.videojournal.activities.DailyVideoFeedActivity;
import static com.example.android.videojournal.utilities.Constants.REC_NOTIF_ID;

/**
 * Broadcast receiver for notifications service
 * to prompt user for daily videos
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent repeatingIntent = new Intent(context, DailyVideoFeedActivity.class);
        repeatingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, REC_NOTIF_ID,
                repeatingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        String title = context.getString(R.string.remind_message_title);
        String contentText = context.getString(R.string.remind_message_text);
        NotificationUtils.showNotification(context, pendingIntent, title, contentText);
    }

}
