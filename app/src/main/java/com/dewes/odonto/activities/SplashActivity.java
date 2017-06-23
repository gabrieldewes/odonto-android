package com.dewes.odonto.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.dewes.odonto.authenticator.AuthenticatorActivity;
import com.dewes.odonto.services.AuthService;

public class SplashActivity extends AppCompatActivity {

    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authService = AuthService.getInstance(this, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /*
                SplashActivity.this.finish();
                if (authService.isAuthenticated()) {
                    SplashActivity.this.startActivity(
                            new Intent(SplashActivity.this, MainActivity.class));
                }
                else {
                    SplashActivity.this.startActivity(
                            new Intent(SplashActivity.this, LoginActivity.class));
                }
                */
                SplashActivity.this.startActivity(
                        new Intent(SplashActivity.this, AuthenticatorActivity.class));
            }
        }, 1000);
    }
}
