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

/*
        DialogPreference preference = getPreference();
        Integer minutesAfterMidnight = null;
        if (preference instanceof TimePreference) {
            minutesAfterMidnight =
                    ((TimePreference) preference).getTime();
        }
        */

   //     TimePreference timepref = (TimePreference) findPreference(R.string.pref_reminder_time_key);
        // instead of below, actually get a Time Preference object

        // get custom preference instance
        // then
        // TimePreference timePreference = (TimePreference) preference;
        // timePreference.setSummary("test 5h10");

/*
        PreferenceScreen prefScreen = getPreferenceScreen();
     //   int count = prefScreen.getPreferenceCount();
     //   Log.d(TAG, "count of prefs: " + count);

       // for (int i = 0; i < count + 1; i++) {
            Preference p = prefScreen.getPreference(1);
            if (p instanceof com.example.android.videojournal.TimePreference) {
                Log.d(TAG, "found timepreference!");
                //setPreferenceSummary(p, getContext());
        //    }
        }
            */

      //  DialogPreference preference = getPreference();
     //   if (preference instanceof TimePreference) {


    }

/*
    private void setPreferenceSummary(Preference preference, Context context) {
        if (preference instanceof TimePreference) {
            TimePreference timePreference = (TimePreference) preference;
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            int minutesAfterMidnight = sharedPreferences.getInt(getString(R.string.pref_reminder_time_key), 60);
            // get hours and mins from savedTime
            int hours = minutesAfterMidnight / 60;
            int minutes = minutesAfterMidnight % 60;
            timePreference.setSummary("test 5h10");
            // "" + hours + "H" + minutes
        }
    }
    */

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
