package com.alonar.android.videojournal.notifications;

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

import com.alonar.android.videojournal.R;
import com.alonar.android.videojournal.formatting.TimeFormatter;

import java.util.Calendar;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import static android.content.Context.ALARM_SERVICE;
import static com.alonar.android.videojournal.utilities.Constants.CHANNEL_ID_REC_NOTIF;
import static com.alonar.android.videojournal.utilities.Constants.REC_NOTIF_ID;
import static com.alonar.android.videojournal.utilities.Constants.DEFAULT_NOTIF_PREF_VALUE;

/**
 * Methods related to reminder notifications for daily videos
 */
public class NotificationUtils {

    /**
     * Creates notification channel on API 26+ otherwise not in support library
     * Channel for video reminder notifications
     */
    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_REC_NOTIF, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Sets up notification to run at set time via AlarmManager
     * at an interval of once per day
     * @param hour for when to send notification
     * @param minute for when to send notification
     */
    public static void setUpReminderNotification(Context context, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REC_NOTIF_ID, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    /**
     * Checks if reminder is activated and will set notification at selected time
     */
    public static void updateNotificationTime(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (reminderIsChecked(sharedPreferences, context)) {
            int minutesAfterMidnight = getTimeFromPreferences(sharedPreferences, context);
            int hours = TimeFormatter.findHoursFromTotalMinutes(minutesAfterMidnight);
            int minutes = TimeFormatter.findMinutesFromTotalMinutes(minutesAfterMidnight);
            setUpReminderNotification(context, hours, minutes);
        }
    }

    /**
     * Returns whether user selected to activate daily reminder notifications or not
     */
    public static boolean reminderIsChecked(SharedPreferences sharedPreferences, Context context) {
        return sharedPreferences.getBoolean(context.getString(R.string.pref_activate_reminder_key),
                context.getResources().getBoolean(R.bool.pref_activate_reminder_default));
    }

    public static int getTimeFromPreferences(SharedPreferences sharedPreferences, Context context) {
        return sharedPreferences.getInt(context.getString(R.string.pref_reminder_time_key),
                DEFAULT_NOTIF_PREF_VALUE);
    }

    /**
     * Builds notification to show to user
     */
    public static void showNotification(Context context, PendingIntent pendingIntent,
                                        String title, String contentText) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_REC_NOTIF)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_create_entry)
                .setContentTitle(title)
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(REC_NOTIF_ID, builder.build());
    }

    /**
     * Cancels pending intent for notification service to stop notifications
     * Used when user un-checks reminder in Settings
     */
    public static void turnOffNotifications(Activity activity) {
        Intent intent = new Intent(activity, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, REC_NOTIF_ID, intent, 0);
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

}
