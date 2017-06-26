package com.dewes.odonto.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import com.dewes.odonto.R;
import com.dewes.odonto.adapters.ActionAdapter;
import com.dewes.odonto.api.client.Callback;
import com.dewes.odonto.api.client.CardResource;
import com.dewes.odonto.domain.Action;
import com.dewes.odonto.domain.Card;
import com.dewes.odonto.listeners.EndlessRecyclerOnScrollListener;

import java.util.List;

import retrofit2.Call;

public class ActionActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private Card card;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ActionAdapter actionAdapter;
    private View progressView;
    private View emptyView;
    private EndlessRecyclerOnScrollListener endlessScrollListener;
    private Call currentCall;
    private Resources res;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action);

        if (getIntent().getExtras() == null)
            finish();

        res = getResources();
        card = (Card) getIntent().getExtras().get("card");

        recyclerView       = (RecyclerView)       findViewById(R.id.recycler);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        progressView       =                      findViewById(R.id.progressView);
        emptyView          =                      findViewById(R.id.emptyView);

        swipeRefreshLayout.setOnRefreshListener(this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        endlessScrollListener = new EndlessRecyclerOnScrollListener( (LinearLayoutManager) layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                Log.d("API", "Loading more "+ current_page);
                currentCall = CardResource.getInstance().getActions(card.getId(), current_page, new Callback<List<Action>>() {
                    @Override
                    public void onResult(List<Action> actions) {
                        Log.d("API", "onResult "+ actions);

                        if (actions != null && !actions.isEmpty()) {
                            actionAdapter.reloadList(actions);
                        }
                    }

                    @Override
                    public void onError() {
                        showProgress(false);
                        Snackbar.make(recyclerView, res.getString(R.string.error_no_connection), Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        };
        recyclerView.setOnScrollListener(endlessScrollListener);

        fetchActions();
    }

    private void fetchActions() {
        showProgress(true);
        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
        currentCall = CardResource.getInstance().getActions(card.getId(), 1, new Callback<List<Action>>() {
            @Override
            public void onResult(List<Action> actions) {
                Log.d("API", "onResult "+ actions);

                showProgress(false);

                if (actions != null) {
                    if (actions.isEmpty()) {
                        //recyclerView.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                    }
                    else {
                        endlessScrollListener.reset();
                        actionAdapter = new ActionAdapter(ActionActivity.this, card, actions);
                        recyclerView.setAdapter(actionAdapter);
                        //recyclerView.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onError() {
                showProgress(false);
                Snackbar.make(recyclerView, getResources().getText(R.string.error_no_connection), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public static Intent getIntent(Context context, Card card) {
        return new Intent(context, ActionActivity.class).putExtra("card", card);
    }

    @Override
    public void onRefresh() {
        fetchActions();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

            recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
            recyclerView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });
        }
        else {
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
