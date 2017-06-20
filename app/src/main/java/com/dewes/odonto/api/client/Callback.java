package com.dewes.odonto.api.client;

/**
 * Created by Dewes on 20/05/2017.
 */

public interface Callback<S> {
    void onResult(S s);
    void onError();
}
