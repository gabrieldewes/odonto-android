package com.dewes.odonto.services;

import android.content.Context;
import android.support.annotation.NonNull;

import com.dewes.odonto.domain.Principal;
import com.dewes.odonto.util.Preferences;

/**
 * Created by Dewes on 18/06/2017.
 */

public class AuthService {

    private Context context;

    private static AuthService INSTANCE;

    private Preferences prefs;

    private AuthService(@NonNull Context context) {
        this.context = context;
        this.prefs = Preferences.getInstance(this.context);
    }

    public static AuthService getInstance(@NonNull Context context, boolean forceInstantiation) {
        if (INSTANCE == null || forceInstantiation) {
            INSTANCE = new AuthService(context);
        }
        return INSTANCE;
    }

    public boolean isAuthenticated() {
        return this.prefs.contains("token");
    }

    public String getToken() {
        return prefs.get("token", "");
    }

    public void putToken(String token) {
        prefs.put("token", token);
    }

    public void removeToken() {
        prefs.remove("token");
    }

}
