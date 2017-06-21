package com.dewes.odonto.api.client;

import android.util.Log;

import com.dewes.odonto.domain.Principal;
import com.dewes.odonto.domain.User;
import com.dewes.odonto.domain.UserCredentials;
import com.dewes.odonto.domain.Status;

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

    public Call me(final Callback<Status> callback) {
        Call<Status> call = this.accountApi.me();
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

    public Call authenticate(String login, String password, final Callback<Status<Principal>> callback) {
        UserCredentials uc = new UserCredentials(login, password);
        Call<Status<Principal>> call = this.accountApi.authenticate(uc);
        call.enqueue(new retrofit2.Callback<Status<Principal>>() {
            @Override
            public void onResponse(Call<Status<Principal>> call, Response<Status<Principal>> response) {
                callback.onResult(response.body());
            }

            @Override
            public void onFailure(Call<Status<Principal>> call, Throwable t) {
                t.printStackTrace();
                callback.onError();
            }
        });
        return call;
    }

    public Call register(String firstName, String lastName, String email, String username, String password,
                         final Callback<Status<User>> callback) {
        User user = new User(firstName, lastName, email, username, password);
        Call<Status<User>> call = this.accountApi.register(user);
        call.enqueue(new retrofit2.Callback<Status<User>>() {
            @Override
            public void onResponse(Call<Status<User>> call, Response<Status<User>> response) {
                callback.onResult(response.body());
            }

            @Override
            public void onFailure(Call<Status<User>> call, Throwable t) {
                t.printStackTrace();
                callback.onError();
            }
        });
        return call;
    }

}
