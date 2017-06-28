package com.dewes.odonto.listeners;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

/**
 * Created by Dewes on 25/06/2017.
 */

public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {

    public static String TAG = EndlessRecyclerOnScrollListener.class.getSimpleName();

    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    int lastVisibleItem, visibleItemCount, totalItemCount;

    private int current_page = 1;

    private LinearLayoutManager mLinearLayoutManager;

    public EndlessRecyclerOnScrollListener(LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLinearLayoutManager.getItemCount();
        //lastVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
        //firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
        lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();

        //Log.d("API", "visibleItemCount "+ visibleItemCount +" totalItemCount "+ totalItemCount +" lastVisibleItem "+ lastVisibleItem);

        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }

        /*if (!loading && (totalItemCount - visibleItemCount)
                <= (lastVisibleItem + visibleThreshold)) {*/
        if (!loading && (totalItemCount >= visibleThreshold)
                && lastVisibleItem == (totalItemCount - 1)) {

            current_page++;

            onLoadMore(current_page);

            loading = true;
        }

    }

    public abstract void onLoadMore(int current_page);

    public void reset() {
        previousTotal = 0;
        loading = true;
        lastVisibleItem = visibleItemCount = totalItemCount = 0;
        current_page = 1;
    }
}
