package com.dewes.odonto.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.dewes.odonto.R;
import com.dewes.odonto.api.client.AccountResource;
import com.dewes.odonto.api.client.Callback;
import com.dewes.odonto.domain.Principal;
import com.dewes.odonto.domain.Status;
import com.dewes.odonto.services.AuthService;

import retrofit2.Call;

/**
 * Created by Dewes on 18/06/2017.
 */

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private FrameLayout fragmentContainer;

    private AuthService authService;

    private TextView tvTitle;
    private TextView tvSubtitle;

    private View progressView;
    private View homeContent;
    private SwipeRefreshLayout swipeRefreshLayout;

    private Call currentCall;

    private AccountResource accountResource;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();

        if (view != null) {

            accountResource = new AccountResource();

            authService = AuthService.getInstance(view.getContext(), true);
            Principal principal = authService.getPrincipal();

            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            tvSubtitle = (TextView) view.findViewById(R.id.tvSubtitle);

            progressView = view.findViewById(R.id.progressBar);
            homeContent = view.findViewById(R.id.homeContent);
            swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);

            swipeRefreshLayout.setOnRefreshListener(this);

            showProgress(true);

            currentCall = accountResource.me(new Callback<Status>() {
                @Override
                public void onResult(Status status) {
                    showProgress(false);
                    tvTitle.setText(status.getStatus());
                    tvSubtitle.setText(status.getMessage());
                }

                @Override
                public void onError() {
                    showProgress(false);
                    Snackbar.make(fragmentContainer, getResources().getString(R.string.error_no_connection), Snackbar.LENGTH_LONG).show();
                }
            });

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        fragmentContainer = (FrameLayout) view.findViewById(R.id.fragmentHome);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (currentCall != null)
            currentCall.cancel();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (currentCall != null)
            currentCall.cancel();
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

            homeContent.setVisibility(show ? View.GONE : View.VISIBLE);
            homeContent.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    homeContent.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });
        }
        else {
            homeContent.setVisibility(show ? View.GONE : View.VISIBLE);
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onRefresh() {
        showProgress(true);

        currentCall = accountResource.me(new Callback<Status>() {
            @Override
            public void onResult(Status status) {
                swipeRefreshLayout.setRefreshing(false);
                showProgress(false);
                tvTitle.setText(status.getStatus());
                tvSubtitle.setText(status.getMessage());
            }

            @Override
            public void onError() {
                swipeRefreshLayout.setRefreshing(false);
                showProgress(false);
                Snackbar.make(fragmentContainer, getResources().getString(R.string.error_no_connection), Snackbar.LENGTH_LONG).show();
            }
        });

    }
}