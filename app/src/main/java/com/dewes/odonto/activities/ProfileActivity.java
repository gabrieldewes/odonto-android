package com.dewes.odonto.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dewes.odonto.R;
import com.dewes.odonto.api.client.AccountResource;
import com.dewes.odonto.api.client.Callback;
import com.dewes.odonto.domain.Principal;
import com.dewes.odonto.util.ImageHelper;

import retrofit2.Call;

public class ProfileActivity extends AppCompatActivity {

    private View contentProfile;

    private View progressView;

    private ImageView ivUserProfilePhoto;
    private ImageView ivHeaderCover;

    private TextView tvUserProfileName;
    private TextView tvUserProfileBio;
    private TextView tvUserProfileEmail;
    //private TextView tvUserProfileUsername;
    private TextView tvUserProfileRoles;

    private Call currentCall;

    private Resources res;

    private String login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        res = getResources();

        login = getIntent().getStringExtra("login");

        ivUserProfilePhoto    = (ImageView) findViewById(R.id.user_profile_photo);
        ivHeaderCover         = (ImageView) findViewById(R.id.header_cover_image);
        tvUserProfileName     = (TextView)  findViewById(R.id.user_profile_name);
        tvUserProfileBio      = (TextView)  findViewById(R.id.user_profile_short_bio);
        tvUserProfileEmail    = (TextView)  findViewById(R.id.user_profile_email);
        //tvUserProfileUsername = (TextView)  findViewById(R.id.user_profile_username);
        tvUserProfileRoles    = (TextView)  findViewById(R.id.user_profile_roles);

        contentProfile = findViewById(R.id.contentProfile);
        progressView = findViewById(R.id.progressBar);

        showProgress(true);

        loadProfile(login);
    }

    private void loadProfile(String login) {
        currentCall = AccountResource.getInstance().findProfileByLogin(login, new Callback<Principal>() {
            @Override
            public void onResult(Principal principal) {
                Log.d("API", "onResult "+ principal);
                showProgress(false);
                if (principal != null) {
                    String fullName = (principal.getFirstName() +" "+ principal.getLastName()).trim();
                    tvUserProfileName.setText(fullName);
                    tvUserProfileBio.setText(principal.getBio());
                    tvUserProfileEmail.setText(principal.getEmail());
                    //tvUserProfileUsername.setText(principal.getUsername());
                    //tvUserProfileRoles.setText(principal.getRoles().get(0).replaceAll("ROLE_", ""));
                    tvUserProfileRoles.setText(com.dewes.odonto.domain.Utils.rolesToHuman(principal.getRoles()));
                    new ImageHelper(ivUserProfilePhoto).execute(principal.getAvatarUrl());
                    new ImageHelper(ivHeaderCover, true).execute(principal.getAvatarUrl());
                }
            }

            @Override
            public void onError() {
                showProgress(false);
                Snackbar.make(contentProfile, res.getString(R.string.error_no_connection), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public static Intent getIntent(Context context, String login) {
        return new Intent(context, ProfileActivity.class)
                .putExtra("login", login);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (currentCall != null)
            currentCall.cancel();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });

            contentProfile.setVisibility(show ? View.GONE : View.VISIBLE);
            contentProfile.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    contentProfile.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });
        }
        else {
            contentProfile.setVisibility(show ? View.GONE : View.VISIBLE);
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
}
