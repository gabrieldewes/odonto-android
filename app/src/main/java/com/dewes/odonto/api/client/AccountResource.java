package com.dewes.odonto.api.client;

import android.util.Log;

import com.dewes.odonto.domain.Principal;
import com.dewes.odonto.domain.User;
import com.dewes.odonto.domain.Status;
import java.util.List;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Dewes on 13/06/2017.
 */

public class AccountResource {

    private AccountApi accountApi;

    public AccountResource() {
        this.accountApi = ServiceGenerator.createService(AccountApi.class);
    }

    public AccountResource(String token) {
        this.accountApi = ServiceGenerator.createService(AccountApi.class, token);
    }

    public Call greetings(final Callback<Status> callback) {
        Call<Status> call = this.accountApi.greetings();
        //Log.d("API", "Calling "+ call.request().url());
        call.enqueue(new retrofit2.Callback<Status>() {
            @Override
            public void onResponse(Call<Status> call, Response<Status> response) {
                callback.onResult(response.body());
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

    public Call register(String firstName, String lastName, String email, String username, String password,
                         final Callback<Status<List<Status<User>>>> callback) {
        User user = new User(firstName, lastName, email, username, password);
        Call<Status<List<Status<User>>>> call = this.accountApi.register(user);
        call.enqueue(new retrofit2.Callback<Status<List<Status<User>>>>() {
            @Override
            public void onResponse(Call<Status<List<Status<User>>>> call, Response<Status<List<Status<User>>>> response) {
                callback.onResult(response.body());
            }

            @Override
            public void onFailure(Call<Status<List<Status<User>>>> call, Throwable t) {
                t.printStackTrace();
                callback.onError();
            }
        });
        return call;
    }

}
