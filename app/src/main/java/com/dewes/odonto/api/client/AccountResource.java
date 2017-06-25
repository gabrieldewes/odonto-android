package com.dewes.odonto.api.client;

import android.util.Log;

import com.dewes.odonto.domain.Principal;
import com.dewes.odonto.domain.User;
import com.dewes.odonto.domain.Status;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;

import static com.dewes.odonto.api.client.Utils.parseErrorBody;

/**
 * Created by Dewes on 13/06/2017.
 */

public class AccountResource {

    private static AccountResource INSTANCE;

    private AccountApi accountApi;

    private AccountResource() {
        this.accountApi = ServiceGenerator.createService(AccountApi.class);
    }

    public static AccountResource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AccountResource();
        }
        return INSTANCE;
    }

    public Call greetings(final Callback<Status> callback) {
        Call<Status> call = this.accountApi.greetings();
        //Log.d("API", "Calling "+ call.request().url());
        call.enqueue(new retrofit2.Callback<Status>() {
            @Override
            public void onResponse(Call<Status> call, Response<Status> response) {
                callback.onResult(response.body());
                if (response.errorBody() != null)
                    parseErrorBody(response.errorBody());
            }

            @Override
            public void onFailure(Call<Status> call, Throwable t) {
                t.printStackTrace();
                if (!call.isCanceled())
                    callback.onError();
            }
        });
        return call;
    }

    public Call me(final Callback<Principal> callback) {
        Call<Principal> call = this.accountApi.me();
        call.enqueue(new retrofit2.Callback<Principal>() {
            @Override
            public void onResponse(Call<Principal> call, Response<Principal> response) {
                callback.onResult(response.body());
                if (response.errorBody() != null)
                    parseErrorBody(response.errorBody());
            }

            @Override
            public void onFailure(Call<Principal> call, Throwable t) {
                t.printStackTrace();
                if (!call.isCanceled())
                    callback.onError();
            }
        });
        return call;
    }

}
