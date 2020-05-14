package com.example.android.videojournal;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_journal);
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
