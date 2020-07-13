package com.example.android.videojournal.utilities;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.example.android.videojournal.R;
import com.example.android.videojournal.utilities.AlarmReceiver;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class NotificationUtils {

    static final String CHANNEL_ID_REC_NOTIF = "record_reminder";
    private static int REC_NOTIF_ID = 100;

    public static void createNotificationChannel(Context context) {
        // create channel only on API 26+ otherwise not in support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_REC_NOTIF, name, importance);
            channel.setDescription(description);
            //register channel with system
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void setUpReminderNotification(Context context, int hour, int minute, Class receiver) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        // DEBUG
        Toast.makeText(context, "Reminder notif: time set at hours= " + hour + " and min= " + minute, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(context, receiver);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REC_NOTIF_ID, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public static void updateNotificationTime(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        // if activate checkmark is on
        if (sharedPreferences.getBoolean(context.getString(R.string.pref_activate_reminder_key), context.getResources().getBoolean(R.bool.pref_activate_reminder_default))) {
            int minutesAfterMidnight = sharedPreferences.getInt(context.getString(R.string.pref_reminder_time_key), 60);
            // get hours and mins from savedTime
            int hours = minutesAfterMidnight / 60;
            int minutes = minutesAfterMidnight % 60;
            setUpReminderNotification(context, hours, minutes, AlarmReceiver.class);
        }
    }

}
