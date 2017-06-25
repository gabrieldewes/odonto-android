package com.dewes.odonto.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
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

import java.util.List;

public class ActionActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private Card card;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ActionAdapter actionAdapter;

    private View progressView;
    private View emptyView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action);

        if (getIntent().getExtras() == null)
            finish();

        card = (Card) getIntent().getExtras().get("card");

        recyclerView       = (RecyclerView)       findViewById(R.id.recycler);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        progressView       =                      findViewById(R.id.progressView);
        emptyView          =                      findViewById(R.id.emptyView);

        showProgress(true);

        swipeRefreshLayout.setOnRefreshListener(this);

        RecyclerView.LayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layout);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        CardResource.getInstance().getActions(card.getId(), new Callback<List<Action>>() {
            @Override
            public void onResult(List<Action> actions) {
                showProgress(false);
                actionAdapter = new ActionAdapter(ActionActivity.this, actions);
                recyclerView.setAdapter(actionAdapter);

                Log.d("API", "API ACTIONS "+ actions.size());

                if (actions.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                }
                else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError() {
                Snackbar.make(recyclerView, getResources().getText(R.string.error_no_connection), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public static Intent getIntent(Context context, Card card) {
        return new Intent(context, ActionActivity.class).putExtra("card", card);
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
        }
        else {
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onRefresh() {

    }
}
