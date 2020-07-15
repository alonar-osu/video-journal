package com.example.android.videojournal.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.android.videojournal.R;
import com.example.android.videojournal.formatting.TimeFormater;
import com.example.android.videojournal.notifications.AlarmReceiver;
import com.example.android.videojournal.notifications.NotificationUtils;
import com.example.android.videojournal.other.TimePreference;

import androidx.fragment.app.DialogFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = SettingsFragment.class.getSimpleName();

    private int mHours;
    private int mMinutes;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_journal);
        setTimePickerPreferenceSummary();
    }

    @Override
    public void onStart() {
        super.onStart();
        setupSharedPreferences();
    }

    private void setTimePickerPreferenceSummary() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        int minutesAfterMidnight = NotificationUtils.getTimeFromPreferences(sharedPreferences, getContext());
        // show chosen time in summary of pref
        Preference timePreference = findPreference(getString(R.string.pref_reminder_time_key));
        String summary = TimeFormater.formatTime(minutesAfterMidnight);
        if (summary != null) timePreference.setSummary(summary);
        else Log.d(TAG, "summary in setTimePickerPreferenceSummary() is null");
    }

    private void setupSharedPreferences() {
        Activity activity = getActivity();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        getTimeFromSharedPrefs(sharedPreferences);
        if (NotificationUtils.reminderIsChecked(sharedPreferences, getContext())) {
            NotificationUtils.setUpReminderNotification(activity, mHours, mMinutes, AlarmReceiver.class);
        }
        // preference change listener
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    // launch custom time picker pref dialog
    @Override
    public void onDisplayPreferenceDialog(Preference preference) {

        DialogFragment dialogFragment = null;
        if (preference instanceof TimePreference) {
            dialogFragment = TimePreferenceFragmentCompat.newInstance(preference.getKey());
        }
        if (dialogFragment != null) {
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(this.getFragmentManager(), "PreferenceFragmentDialog");
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Activity activity = getActivity();
        // activate checkmark status changed
        if (key.equals(getString(R.string.pref_activate_reminder_key))) {
            // checked - turn on notification
            if (sharedPreferences.getBoolean(key, getResources().getBoolean(R.bool.pref_activate_reminder_default))) {
                getTimeFromSharedPrefs(sharedPreferences);
                NotificationUtils.setUpReminderNotification(activity, mHours, mMinutes, AlarmReceiver.class);
            } else { // unchecked - turn off notifications
                NotificationUtils.turnOffNotifications(activity);
            }
        }
    }

    private void getTimeFromSharedPrefs(SharedPreferences sharedPreferences) {
        int minutesAfterMidnight = NotificationUtils.getTimeFromPreferences(sharedPreferences, getContext());
        mHours = TimeFormater.findHoursFromTotalMinutes(minutesAfterMidnight);
        mMinutes = TimeFormater.findMinutesFromTotalMinutes(minutesAfterMidnight);
    }

    @Override
    public void onStop() {
        super.onStop();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

}
