package com.dewes.odonto.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.dewes.odonto.domain.Principal;
import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Dewes on 18/06/2017.
 */

public class Preferences {

    private Context context;
    private static Preferences INSTANCE;

    private SharedPreferences sharedPreferences = null;
    private SharedPreferences.Editor editableSharedPreferences = null;


    private Preferences(@NonNull Context context) {
        this.context = context;
        this.sharedPreferences = context.getApplicationContext().getSharedPreferences("USER_INFO", MODE_PRIVATE);
    }

    public static Preferences getInstance(@NonNull Context context) {
        if (INSTANCE == null)
            INSTANCE = new Preferences(context);
        return INSTANCE;
    }

    public boolean contains(String key) {
        return sharedPreferences.contains(key);
    }

    public String get(String key, String defaultValue) {
        return this.sharedPreferences.getString(key, defaultValue);
    }

    public void put(String key, String value) {
        this.editableSharedPreferences = sharedPreferences.edit();
        this.editableSharedPreferences.putString(key, value).apply();
    }

    public void remove(String key) {
        this.editableSharedPreferences = sharedPreferences.edit();
        this.editableSharedPreferences.remove(key).apply();
    }


}
