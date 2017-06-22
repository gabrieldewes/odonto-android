package com.dewes.odonto.api.client;

import android.util.Log;

import com.dewes.odonto.domain.Status;
import com.dewes.odonto.domain.Token;
import com.dewes.odonto.domain.UserCredentials;
import com.dewes.odonto.util.Preferences;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Gabriel on 22/06/2017.
 */

public class AuthResource {

    private AuthApi authApi;

    public AuthResource(String token) {
        this.authApi = ServiceGenerator.createService(AuthApi.class, token);
    }

    public AuthResource() {
        this.authApi = ServiceGenerator.createService(AuthApi.class);
    }

    public Call authenticate(String login, String password, final Callback<Status<Token>> callback) {
        UserCredentials uc = new UserCredentials(login, password);
        Call<Status<Token>> call = this.authApi.callForToken(uc);
        //Log.d("API", "Calling "+ call.request().url());
        call.enqueue(new retrofit2.Callback<Status<Token>>() {
            @Override
            public void onResponse(Call<Status<Token>> call, Response<Status<Token>> response) {
                callback.onResult(response.body());
            }

            @Override
            public void onFailure(Call<Status<Token>> call, Throwable t) {
                t.printStackTrace();
                callback.onError();
            }
        });
        return call;
    }

    public Call logout(final Callback<Status> callback) {
        Call<Status> call = this.authApi.callForLogout();
        call.enqueue(new retrofit2.Callback<Status>() {
            @Override
            public void onResponse(Call<Status> call, Response<Status> response) {
                callback.onResult(response.body());
            }

            @Override
            public void onFailure(Call<Status> call, Throwable t) {
                t.printStackTrace();
                callback.onError();
            }
        });
        return call;
    }
}
