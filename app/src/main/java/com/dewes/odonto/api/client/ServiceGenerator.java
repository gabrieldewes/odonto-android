package com.dewes.odonto.api.client;

import android.content.Context;
import android.util.Base64;

import com.dewes.odonto.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Dewes on 13/06/2017.
 */

public class ServiceGenerator {

    private Context context;

    //private static String API_URL = "http://104.131.172.28/api/";
    private static String API_URL = "http://10.0.0.10/odonto/api/";
    //private static final String API_URL = "http://192.168.0.101/odonto/api/";

    private static final String CLIENT_ID = "android";
    private static final String CLIENT_SECRET = "android-secret";

    private static Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

    private static Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create(gson));

    private static String getClientCredentials() {
        String credentials = CLIENT_ID + ":" + CLIENT_SECRET;
        return "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
    }

    static <S> S createService(Class<S> service) {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        Request.Builder requestBuilder = original.newBuilder()
                                .header("Authorization", getClientCredentials())
                                .header("Content-Type", "application/json")
                                .method(original.method(), original.body());
                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    }
                }).build();
        return retrofitBuilder
                .client(httpClient)
                .build()
                .create(service);
    }

    static <S> S createService(Class<S> service, final String token) {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        Request.Builder requestBuilder = original.newBuilder()
                                .header("Authorization", getClientCredentials())
                                .header("X-API-TOKEN", token)
                                .header("Content-Type", "application/json")
                                .method(original.method(), original.body());
                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    }
                }).build();
        return retrofitBuilder
                .client(httpClient)
                .build()
                .create(service);
    }

}
