package com.example.android.videojournal.utilities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import com.example.android.videojournal.R;
import com.example.android.videojournal.formatting.TimeFormater;

import java.util.Calendar;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import static android.content.Context.ALARM_SERVICE;

public class NotificationUtils {

    private static final String TAG = NotificationUtils.class.getSimpleName();
    private static final String CHANNEL_ID_REC_NOTIF = "record_reminder";
    private static int REC_NOTIF_ID = 100;
    private static final int DEFAULT_NOTIF_PREF_VALUE = 725;

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // channel on API 26+ otherwise not in support library
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_REC_NOTIF, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void setUpReminderNotification(Context context, int hour, int minute, Class receiver) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        // Toast.makeText(context, "Notification set at hours= " + hour + " and min= " + minute, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(context, receiver);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REC_NOTIF_ID, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public static void updateNotificationTime(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (reminderIsChecked(sharedPreferences, context)) {
            int minutesAfterMidnight = getTimeFromPreferences(sharedPreferences, context);
            int hours = TimeFormater.findHoursFromTotalMinutes(minutesAfterMidnight);
            int minutes = TimeFormater.findMinutesFromTotalMinutes(minutesAfterMidnight);
            setUpReminderNotification(context, hours, minutes, AlarmReceiver.class);
        }
    }

    public static boolean reminderIsChecked(SharedPreferences sharedPreferences, Context context) {
        return sharedPreferences.getBoolean(context.getString(R.string.pref_activate_reminder_key),
                context.getResources().getBoolean(R.bool.pref_activate_reminder_default));
    }

    public static int getTimeFromPreferences(SharedPreferences sharedPreferences, Context context) {
        return sharedPreferences.getInt(context.getString(R.string.pref_reminder_time_key),
                DEFAULT_NOTIF_PREF_VALUE);
    }

    public static void showNotification(Context context, PendingIntent pendingIntent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_REC_NOTIF)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_create_entry)
                .setContentTitle("Time for Video Journal Entry")
                .setContentText("Let's take a new daily video")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(REC_NOTIF_ID, builder.build());
    }

    public static void turnOffNotifications(Activity activity) {
        Intent intent = new Intent(activity, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, REC_NOTIF_ID, intent, 0);
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

}
