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

    private Gson gson;

    public Preferences(@NonNull Context context) {
        this.context = context;
        this.sharedPreferences = context.getApplicationContext().getSharedPreferences("USER_INFO", MODE_PRIVATE);
        this.editableSharedPreferences = sharedPreferences.edit();
        this.gson = new Gson();
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

    public Object get(String key, Object defaultValue, Class klass) {
        String value = this.sharedPreferences.getString(key, "{}");
        Object obj = this.gson.fromJson(value, klass);
        if (obj != null)
            return obj;
        return defaultValue;
    }

    public void put(String key, String value) {
        editableSharedPreferences.putString(key, value).apply();
    }

    public void put(String key, Object value) {
        editableSharedPreferences.putString(key, gson.toJson(value)).apply();
    }

    public void remove(String key) {
        editableSharedPreferences.remove(key).apply();
    }


}
