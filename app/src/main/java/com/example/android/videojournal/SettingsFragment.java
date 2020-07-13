package com.example.android.videojournal;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.example.android.videojournal.formatting.TimeFormater;
import com.example.android.videojournal.utilities.AlarmReceiver;
import com.example.android.videojournal.utilities.NotificationUtils;
import com.example.android.videojournal.visualization.TimePreference;

import androidx.fragment.app.DialogFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import static android.content.Context.ALARM_SERVICE;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = SettingsFragment.class.getSimpleName();
    private static final int DEFAULT_NOTIF_TIME_MINS = 60;
    private static final int REC_NOTIF_ID = 100;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_journal);

        setTimePickerPreferenceSummary();

    }

    private void setTimePickerPreferenceSummary() {
        Preference timePreference = findPreference(getString(R.string.pref_reminder_time_key));
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        int minutesAfterMidnight = sharedPreferences.getInt(getString(R.string.pref_reminder_time_key), 60);
        // get hours and mins from savedTime
        int hours = minutesAfterMidnight / 60;
        int minutes = minutesAfterMidnight % 60;
        // show chosen time in summary of pref
        timePreference.setSummary(TimeFormater.formatTime(hours, minutes));
    }

    // launch custom time picker pref dialog
    @Override
    public void onDisplayPreferenceDialog(Preference preference) {

        DialogFragment dialogFragment = null;
        if (preference instanceof TimePreference) {

            dialogFragment = TimePreferenceFragmentCompat
                    .newInstance(preference.getKey());
        }

        if (dialogFragment != null) {
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(this.getFragmentManager(),
                            "PreferenceFragmentDialog");
        }
        else {
            super.onDisplayPreferenceDialog(preference);
        }
    }

    public void setupSharedPreferences() {
        Activity activity = getActivity();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);

        boolean reminderActive = sharedPreferences.getBoolean(getString(R.string.pref_activate_reminder_key),
                getResources().getBoolean(R.bool.pref_activate_reminder_default));
        int minutesAfterMidnight = sharedPreferences.getInt(getString(R.string.pref_reminder_time_key), DEFAULT_NOTIF_TIME_MINS);
        // get hours and mins from savedTime
        int hours = minutesAfterMidnight / 60;
        int minutes = minutesAfterMidnight % 60;
        if (reminderActive) {
            NotificationUtils.setUpReminderNotification(activity, hours, minutes, AlarmReceiver.class);
        }

        // register listener for preference changes
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Activity activity = getActivity();
        // activate checkmark status changed
        if (key.equals(getString(R.string.pref_activate_reminder_key))) {
            // was checked - turn on notification
            if (sharedPreferences.getBoolean(key, getResources().getBoolean(R.bool.pref_activate_reminder_default))) {
                int minutesAfterMidnight = sharedPreferences.getInt(getString(R.string.pref_reminder_time_key), DEFAULT_NOTIF_TIME_MINS);
                // get hours and mins from savedTime
                int hours = minutesAfterMidnight / 60;
                int minutes = minutesAfterMidnight % 60;
                NotificationUtils.setUpReminderNotification(activity, hours, minutes, AlarmReceiver.class);
            } else { // was unchecked - turn off notifications
                Intent intent = new Intent(activity, AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, REC_NOTIF_ID, intent, 0);
                AlarmManager alarmManager = (AlarmManager) activity.getSystemService(ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // register the preference change listener
        setupSharedPreferences();
    }

    @Override
    public void onStop() {
        super.onStop();
        // unregister the preference change listener
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }



}
