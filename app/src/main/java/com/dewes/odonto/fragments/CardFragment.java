package com.dewes.odonto.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.dewes.odonto.R;
import com.dewes.odonto.adapters.CardAdapter;
import com.dewes.odonto.api.client.Callback;
import com.dewes.odonto.api.client.CardResource;
import com.dewes.odonto.domain.Card;
import com.dewes.odonto.listeners.EndlessRecyclerOnScrollListener;

import java.util.List;

import retrofit2.Call;

/**
 * Created by Dewes on 18/06/2017.
 */

public class CardFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private FrameLayout fragmentContainer = null;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CardAdapter cardAdapter;
    private View progressView;
    private View emptyView;

    boolean archive = false;
    private Call currentCall;

    private Resources res;

    public static CardFragment getInstance(boolean archive) {
        CardFragment cardFragment = new CardFragment();
        Bundle args = new Bundle();
        args.putBoolean("archive", archive);
        cardFragment.setArguments(args);
        return cardFragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final View view = getView();
        res = getResources();

        Bundle args = getArguments();
        if (args != null) {
            archive = args.getBoolean("archive", false);
        }

        if (view != null) {

            recyclerView       = (RecyclerView)       view.findViewById(R.id.recycler);
            swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
            progressView       =                      view.findViewById(R.id.fragment_card_progress);
            emptyView          =                      view.findViewById(R.id.empty_view);

            showProgress(true);

            swipeRefreshLayout.setOnRefreshListener(this);

            RecyclerView.LayoutManager layout = new LinearLayoutManager(fragmentContainer.getContext(), LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layout);
            recyclerView.setHasFixedSize(true);
            recyclerView.setItemViewCacheSize(20);
            recyclerView.setDrawingCacheEnabled(true);
            recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

            currentCall = CardResource.getInstance().findAll(archive, new Callback<List<Card>>() {
                @Override
                public void onResult(List<Card> cards) {
                    showProgress(false);

                    if (cards != null) {
                        cardAdapter = new CardAdapter(view.getContext(), cards);
                        recyclerView.setAdapter(cardAdapter);

                        if (cards.isEmpty()) {
                            recyclerView.setVisibility(View.GONE);
                            emptyView.setVisibility(View.VISIBLE);
                        }
                        else {
                            recyclerView.setVisibility(View.VISIBLE);
                            emptyView.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onError() {
                    showProgress(false);
                    Snackbar.make(fragmentContainer, res.getString(R.string.error_no_connection), Snackbar.LENGTH_LONG).show();
                }
            });

            recyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener( (LinearLayoutManager) layout) {
                @Override
                public void onLoadMore(int current_page) {
                    Log.d("API", "Load More "+ current_page);
                }
            });
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card, container, false);
        fragmentContainer = (FrameLayout) view.findViewById(R.id.fragmentCards);
        return view;
    }

    @Override
    public void onRefresh() {
        currentCall = CardResource.getInstance().findAll(archive, new Callback<List<Card>>() {
            @Override
            public void onResult(List<Card> cards) {
                showProgress(false);
                swipeRefreshLayout.setRefreshing(false);

                if (cards != null) {
                    cardAdapter = new CardAdapter(getContext(), cards);
                    recyclerView.setAdapter(cardAdapter);

                    if (cards.isEmpty()) {
                        recyclerView.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                    }
                    else {
                        recyclerView.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onError() {
                Snackbar.make(fragmentContainer, res.getString(R.string.error_no_connection), Snackbar.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
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
}