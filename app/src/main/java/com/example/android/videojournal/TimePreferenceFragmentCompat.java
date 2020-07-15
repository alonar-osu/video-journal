package com.example.android.videojournal;


import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TimePicker;

import com.example.android.videojournal.formatting.TimeFormater;
import com.example.android.videojournal.utilities.NotificationUtils;
import com.example.android.videojournal.visualization.TimePreference;

import androidx.preference.DialogPreference;
import androidx.preference.PreferenceDialogFragmentCompat;

public class TimePreferenceFragmentCompat extends PreferenceDialogFragmentCompat {

    private static final String TAG = TimePreferenceFragmentCompat.class.getSimpleName();
    private TimePicker mTimePicker;

    public static TimePreferenceFragmentCompat newInstance(String key) {
        final TimePreferenceFragmentCompat fragment = new TimePreferenceFragmentCompat();
        final Bundle b = new Bundle(1);
        b.putString(ARG_KEY, key);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        setTimeFromPreferenceOnTimePicker(view);
    }

    private void setTimeFromPreferenceOnTimePicker(View view) {
        mTimePicker = (TimePicker) view.findViewById(R.id.edit);
        if (mTimePicker == null) {
            throw new IllegalStateException("Dialog view must contain a TimePicker with id 'edit'");
        }
        Integer minutesAfterMidnight = getTimeFromPreference();
        setTimeOnTimePicker(minutesAfterMidnight);
    }

    private Integer getTimeFromPreference() {
        Integer minutesAfterMidnight = null;
        DialogPreference preference = getPreference();
        if (preference instanceof TimePreference) {
            minutesAfterMidnight = ((TimePreference) preference).getTime();
        }
        return minutesAfterMidnight;
    }

    private void setTimeOnTimePicker(Integer minutesAfterMidnight) {
        if (minutesAfterMidnight != null) {
            int hours = TimeFormater.findHoursFromTotalMinutes(minutesAfterMidnight);
            int minutes = TimeFormater.findMinutesFromTotalMinutes(minutesAfterMidnight);
            boolean is24hour = DateFormat.is24HourFormat(getContext());

            mTimePicker.setIs24HourView(is24hour);
            mTimePicker.setCurrentHour(hours);
            mTimePicker.setCurrentMinute(minutes);
        }
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            updateTimePreference(getTimeFromTimePicker());
        }
    }

    private int getTimeFromTimePicker() {
        int hours = mTimePicker.getCurrentHour();
        int minutes = mTimePicker.getCurrentMinute();
        return TimeFormater.findTotalMinutesFromHoursAndMins(hours, minutes);
    }

    private void updateTimePreference(int minutesAfterMidnight) {
        DialogPreference preference = getPreference();
        if (preference instanceof TimePreference) {
            TimePreference timePreference = ((TimePreference) preference);

            if (timePreference.callChangeListener(minutesAfterMidnight)) {
                timePreference.setTime(minutesAfterMidnight);
                NotificationUtils.updateNotificationTime(getContext());
                timePreference.setSummary(TimeFormater.formatTime(minutesAfterMidnight));
            }
        }
    }

}
