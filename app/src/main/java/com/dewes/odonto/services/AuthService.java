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

    public AuthService(@NonNull Context context) {
        this.context = context;

        if (this.prefs == null)
            this.prefs = Preferences.getInstance(context);
    }

    public static AuthService getInstance(@NonNull Context context) {
        if (INSTANCE == null)
            INSTANCE = new AuthService(context);
        return INSTANCE;
    }

    public static AuthService getInstance(@NonNull Context context, boolean forceInstantiation) {
        if (forceInstantiation) {
            INSTANCE = new AuthService(context);
        }
        return INSTANCE;
    }

    public boolean isAuthenticated() {
        return this.prefs.contains("principal");
    }

    public Principal getPrincipal() {
        return (Principal) prefs.get("principal", null, Principal.class);
    }

    public void putPrincipal(Principal principal) {
        prefs.put("principal", principal);
    }

    public Long getCurrentUserId() {
        if (this.isAuthenticated()) {
            return this.getPrincipal().getId();
        }
        return -1L;
    }

    public void logout() {
        prefs.remove("principal");
    }
}
