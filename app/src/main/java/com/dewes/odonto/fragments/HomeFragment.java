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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.dewes.odonto.R;
import com.dewes.odonto.api.client.AccountResource;
import com.dewes.odonto.api.client.Callback;
import com.dewes.odonto.domain.Status;

import java.util.concurrent.TimeUnit;

import retrofit2.Call;

/**
 * Created by Dewes on 18/06/2017.
 */

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private FrameLayout fragmentContainer;

    private View progressView;
    private View homeContent;
    private SwipeRefreshLayout swipeRefreshLayout;

    private Call currentCall;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();

        if (view != null) {

            progressView = view.findViewById(R.id.progressBar);
            homeContent = view.findViewById(R.id.homeContent);
            swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);

            swipeRefreshLayout.setOnRefreshListener(this);

            showProgress(false);
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
        showProgress(false);
        swipeRefreshLayout.setRefreshing(false);
    }
}