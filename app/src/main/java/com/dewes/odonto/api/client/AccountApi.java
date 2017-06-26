package com.dewes.odonto.api.client;

import com.dewes.odonto.domain.Principal;
import com.dewes.odonto.domain.User;
import com.dewes.odonto.domain.UserCredentials;
import com.dewes.odonto.domain.Status;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Dewes on 13/06/2017.
 */

public interface AccountApi {

    @GET("accounts/me")
    Call<Principal> me();

    @GET("accounts/{login}")
    Call<Principal> findProfileByLogin(@Path("login") String login);

    @POST("accounts/register")
    Call<Status<List<Status<User>>>> register(@Body User user);

}
