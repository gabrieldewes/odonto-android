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
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.dewes.odonto.R;
import com.dewes.odonto.activities.MainActivity;
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

    private FrameLayout fragmentContainer;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private EndlessRecyclerOnScrollListener endlessScrollListener;
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

        if (view != null) {
            res = getResources();
            Bundle args = getArguments();

            if (args != null) {
                archive = args.getBoolean("archive", false);
            }

            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

            swipeRefreshLayout.setOnRefreshListener(this);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(fragmentContainer.getContext(), LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(true);
            recyclerView.setItemViewCacheSize(20);
            recyclerView.setDrawingCacheEnabled(true);
            recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

            endlessScrollListener = new EndlessRecyclerOnScrollListener((LinearLayoutManager) layoutManager) {
                @Override
                public void onLoadMore(int current_page) {
                    appendCards(current_page);
                }
            };
            recyclerView.setOnScrollListener(endlessScrollListener);

            fetchCards();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card, container, false);
        fragmentContainer  = (FrameLayout)        view.findViewById(R.id.fragmentCards);
        recyclerView       = (RecyclerView)       view.findViewById(R.id.recycler);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        progressView       =                      view.findViewById(R.id.fragment_card_progress);
        emptyView          =                      view.findViewById(R.id.empty_view);
        return view;
    }

    @Override
    public void onRefresh() {
        fetchCards();
    }

    private void fetchCards() {
        showProgress(true);
        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
        currentCall = CardResource.getInstance().findAll(archive, 1, new Callback<List<Card>>() {
            @Override
            public void onResult(List<Card> cards) {
                Log.d("API", "onResult "+ cards);
                showProgress(false);

                if (cards != null) {

                    if (cards.isEmpty()) {
                        //recyclerView.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                    }
                    else {
                        endlessScrollListener.reset();
                        cardAdapter = new CardAdapter(getContext(), cards);
                        recyclerView.setAdapter(cardAdapter);
                        //recyclerView.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onError() {
                showProgress(false);
                showMessage(res.getString(R.string.error_no_connection));
                //Snackbar.make(getActivity().findViewById(R.id.placeSnackbar), res.getString(R.string.error_no_connection), Snackbar.LENGTH_LONG).show();
                //Snackbar.make(fragmentContainer, res.getString(R.string.error_no_connection), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void appendCards(int page) {
        Log.d("API", "Loading page "+ page);
        currentCall = CardResource.getInstance().findAll(archive, page, new Callback<List<Card>>() {
            @Override
            public void onResult(List<Card> cards) {
                Log.d("API", "onResult "+ cards);

                if (cards != null && !cards.isEmpty()) {
                    cardAdapter.reloadList(cards);
                }
            }

            @Override
            public void onError() {
                showMessage(res.getString(R.string.error_no_connection));
                //Snackbar.make(fragmentContainer, res.getString(R.string.error_no_connection), Snackbar.LENGTH_LONG).show();
            }
        });
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