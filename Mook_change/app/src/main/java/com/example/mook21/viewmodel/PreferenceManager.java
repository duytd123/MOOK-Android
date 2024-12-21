package com.example.mook21.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {

    private static final String PREF_NAME = "onboarding_pref";
    private static final String KEY_FIRST_LAUNCH = "is_first_launch";

    private final SharedPreferences sharedPreferences;

    public PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean isFirstLaunch() {
        return sharedPreferences.getBoolean(KEY_FIRST_LAUNCH, true);
    }

    public void setFirstLaunch(boolean isFirstLaunch) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_FIRST_LAUNCH, isFirstLaunch);
        editor.apply();
    }
}
