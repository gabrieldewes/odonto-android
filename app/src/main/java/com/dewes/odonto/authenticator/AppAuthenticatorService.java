package com.dewes.odonto.authenticator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AppAuthenticatorService extends Service {
    @Override
    public IBinder onBind(Intent intent) {

        AppAuthenticator authenticator = new AppAuthenticator(this);
        return authenticator.getIBinder();
    }
}
