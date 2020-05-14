package com.example.android.videojournal;

import android.content.Context;
import android.content.res.TypedArray;

import android.util.AttributeSet;

import androidx.preference.DialogPreference;

public class TimePreference extends DialogPreference {

    private int mTime;
    private int mDialogLayoutResId = R.layout.pref_dialog_time;

    public TimePreference(Context context) {
        this(context, null);
    }
    public TimePreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public TimePreference(Context context, AttributeSet attrs,
                          int defStyleAttr) {
        this(context, attrs, defStyleAttr, defStyleAttr);
    }

    public TimePreference(Context context, AttributeSet attrs,
                          int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public int getTime() {
        return mTime;
    }
    public void setTime(int time) {
        mTime = time;
        // Save to Shared Preferences
        persistInt(time);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, 0);
    }
    @Override
    protected void onSetInitialValue(boolean restorePersistedValue,
                                     Object defaultValue) {
        setTime(restorePersistedValue ?
                getPersistedInt(mTime) : (int) defaultValue);
    }

    @Override
    public int getDialogLayoutResource() {
        return mDialogLayoutResId;
    }


}
