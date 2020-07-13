package com.example.android.videojournal;


import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TimePicker;

import androidx.preference.DialogPreference;
import androidx.preference.PreferenceDialogFragmentCompat;

public class TimePreferenceFragmentCompat extends PreferenceDialogFragmentCompat {

    private static final String TAG = TimePreferenceFragmentCompat.class.getSimpleName();

    private TimePicker mTimePicker;

    public static TimePreferenceFragmentCompat newInstance(
            String key) {
        final TimePreferenceFragmentCompat
                fragment = new TimePreferenceFragmentCompat();
        final Bundle b = new Bundle(1);
        b.putString(ARG_KEY, key);
        fragment.setArguments(b);

        return fragment;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        mTimePicker = (TimePicker) view.findViewById(R.id.edit);

        if (mTimePicker == null) {
            throw new IllegalStateException("Dialog view must contain" +
                    " a TimePicker with id 'edit'");
        }

        Integer minutesAfterMidnight = null;
        DialogPreference preference = getPreference();
        if (preference instanceof TimePreference) {
            minutesAfterMidnight = ((TimePreference) preference).getTime();
        }

        // Set the time to the TimePicker
        if (minutesAfterMidnight != null) {
            int hours = minutesAfterMidnight / 60;
            int minutes = minutesAfterMidnight % 60;
            boolean is24hour = DateFormat.is24HourFormat(getContext());

            mTimePicker.setIs24HourView(is24hour);
            mTimePicker.setCurrentHour(hours);
            mTimePicker.setCurrentMinute(minutes);
        }
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            // generate value to save
            int hours = mTimePicker.getCurrentHour();
            int minutes = mTimePicker.getCurrentMinute();
            int minutesAfterMidnight = (hours * 60) + minutes;

            // Get the related Preference and save the value
            DialogPreference preference = getPreference();
            if (preference instanceof TimePreference) {
                TimePreference timePreference = ((TimePreference) preference);

                if (timePreference.callChangeListener(
                        minutesAfterMidnight)) {
                    // Save the value
                    timePreference.setTime(minutesAfterMidnight);

                    //updateNotificationTime();
                    NotificationUtils.updateNotificationTime(getContext());

                    // update time pref's summary with chosen time
                    timePreference.setSummary(TimeFormater.formatTime(hours, minutes));
                }
            }
        }
    }

}
