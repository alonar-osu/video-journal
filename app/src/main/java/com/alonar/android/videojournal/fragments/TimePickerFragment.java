package com.alonar.android.videojournal.fragments;


import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.text.format.DateFormat;
import android.widget.TimePicker;

import com.alonar.android.videojournal.notifications.NotificationUtils;

import java.util.Calendar;

/**
 * Time picker to show in a dialog opened from settings screen
 */
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    public TimePickerFragment() {
    }

    /**
     * Sets current time as default values on time picker
     * and returns the time picker dialog with
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // current time as default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    /**
     * Activates notifications for time chosen on time picker
     */
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        NotificationUtils.setUpReminderNotification(getContext(), hourOfDay, minute);
    }
}
