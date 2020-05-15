package com.example.android.videojournal;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceManager;
import android.util.Log;
import androidx.preference.DialogPreference;

import androidx.fragment.app.DialogFragment;
import androidx.preference.DialogPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

public class SettingsFragment extends PreferenceFragmentCompat  {

    private static final String TAG = SettingsFragment.class.getSimpleName();

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
        String summary = "";
        if (hours >= 12) {
            if (hours >= 13) summary += hours - 12;
            else summary += hours;
            summary += " : " + minutes + " PM";
        } else {
            if (hours == 0) summary += hours + 12;
            else summary += hours;
            summary += " : " + minutes + " AM";
        }
        timePreference.setSummary(summary);
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

}
