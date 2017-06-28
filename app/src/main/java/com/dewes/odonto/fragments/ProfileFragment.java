package com.dewes.odonto.fragments;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.dewes.odonto.R;
import com.dewes.odonto.activities.SplashActivity;
import com.dewes.odonto.api.client.AccountResource;
import com.dewes.odonto.api.client.AuthResource;
import com.dewes.odonto.api.client.Callback;
import com.dewes.odonto.authenticator.AccountConstants;
import com.dewes.odonto.authenticator.AppAuthenticator;
import com.dewes.odonto.domain.Principal;
import com.dewes.odonto.domain.Status;
import com.dewes.odonto.util.ImageHelper;

import java.util.Random;

import retrofit2.Call;

/**
 * Created by Dewes on 18/06/2017.
 */

public class ProfileFragment extends Fragment {

    private FrameLayout fragmentContainer = null;

    private View progressView;

    private ImageView ivUserProfilePhoto;
    private ImageView ivHeaderCover;

    private TextView tvUserProfileName;
    private TextView tvUserProfileBio;
    private TextView tvUserProfileEmail;
    private TextView tvUserProfileUsername;
    private TextView tvUserProfileRoles;

    private Call currentCall;

    private Resources res;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();

        if (view != null) {
            showProgress(true);

            res = getResources();

            ivUserProfilePhoto    = (ImageView) view.findViewById(R.id.user_profile_photo);
            ivHeaderCover         = (ImageView) view.findViewById(R.id.header_cover_image);
            tvUserProfileName     = (TextView)  view.findViewById(R.id.user_profile_name);
            tvUserProfileBio      = (TextView)  view.findViewById(R.id.user_profile_short_bio);
            tvUserProfileEmail    = (TextView)  view.findViewById(R.id.user_profile_email);
            tvUserProfileUsername = (TextView)  view.findViewById(R.id.user_profile_username);
            tvUserProfileRoles    = (TextView)  view.findViewById(R.id.user_profile_roles);

            Button btLogout = (Button) view.findViewById(R.id.btLogout);

            loadProfile();

            btLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doLogout();
                }
            });

        }
    }

    private void loadProfile() {
        currentCall = AccountResource.getInstance().me(new Callback<Principal>() {
            @Override
            public void onResult(Principal principal) {
                showProgress(false);
                if (principal != null) {
                    String fullName = (principal.getFirstName() +" "+ principal.getLastName()).trim();
                    tvUserProfileName.setText(fullName);
                    tvUserProfileBio.setText(principal.getBio());
                    tvUserProfileEmail.setText(principal.getEmail());
                    tvUserProfileUsername.setText(principal.getUsername());
                    //tvUserProfileRoles.setText(principal.getRoles().get(0).replaceAll("ROLE_", ""));
                    tvUserProfileRoles.setText(com.dewes.odonto.domain.Utils.rolesToHuman(principal.getRoles()));
                    new ImageHelper(ivUserProfilePhoto).execute(principal.getAvatarUrl());
                    new ImageHelper(ivHeaderCover, true).execute(principal.getAvatarUrl());
                }
            }

            @Override
            public void onError() {
                showProgress(false);
                showMessage(res.getString(R.string.error_no_connection));
                //Snackbar.make(fragmentContainer, res.getString(R.string.error_no_connection), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void doLogout() {
        showProgress(true);
        AuthResource.getInstance().revoke(new Callback<Status>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
            @Override
            public void onResult(Status status) {
                Log.d("API", "onResult "+ status);
                AccountManager mAccountManager = AccountManager.get(getContext());
                final Account account = mAccountManager.getAccountsByType(AccountConstants.ACCOUNT_TYPE)[0];
                final AccountManagerFuture<Bundle> future = mAccountManager.removeAccount(account, getActivity(),
                        new AccountManagerCallback<Bundle>() {
                            @Override
                            public void run(AccountManagerFuture<Bundle> future) {
                                try {
                                    Bundle bnd = future.getResult();
                                    Log.d("API", "removeAccount " + bnd);
                                    showProgress(false);
                                    Toast.makeText(getContext(), res.getString(R.string.success_logged_out), Toast.LENGTH_LONG).show();
                                    getActivity().finish();
                                    getActivity().startActivity(new Intent(getActivity(), SplashActivity.class));
                                }
                                catch (Exception e) {
                                    showProgress(false);
                                    e.printStackTrace();
                                    Log.d("API", "Exception "+ e.getMessage());
                                }
                            }
                        }, null);
            }

            @Override
            public void onError() {
                showProgress(false);
                showMessage(res.getString(R.string.error_no_connection));
                //Snackbar.make(fragmentContainer, res.getString(R.string.error_no_connection), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        fragmentContainer = (FrameLayout) view.findViewById(R.id.fragmentProfile);
        progressView = view.findViewById(R.id.progressBar);
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (currentCall != null)
            currentCall.cancel();
    }

    private void showMessage(String message) {
        Snackbar.make(getActivity().findViewById(R.id.placeSnackbar), message, Snackbar.LENGTH_LONG)
                .show();
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

            fragmentContainer.setVisibility(show ? View.GONE : View.VISIBLE);
            fragmentContainer.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    fragmentContainer.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });
        }
        else {
            fragmentContainer.setVisibility(show ? View.GONE : View.VISIBLE);
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

}