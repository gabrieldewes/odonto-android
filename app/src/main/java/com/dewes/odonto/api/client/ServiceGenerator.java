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

    private static String API_URL = "http://104.131.172.28/api/";

    private static final String CLIENT_ID = "android";
    private static final String CLIENT_SECRET = "android-secret";

    private static Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

    private static Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create(gson));

    public ServiceGenerator(Context context) {
        this.context = context;
        this.API_URL = context.getString(R.string.API_URL);
    }

    public static <S> S createService(Class<S> service) {
        String credentials = CLIENT_ID + ":" + CLIENT_SECRET;
        final String basic = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        Request.Builder requestBuilder = original.newBuilder()
                                .header("Authorization", basic)
                                .header("Content-Type", "application/json")
                                .method(original.method(), original.body());
                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    }
                }).build();
        Retrofit retrofit = retrofitBuilder.client(httpClient).build();
        return retrofit.create(service);
    }

    public static <S> S createService(Class<S> service, final Long userId) {
        String credentials = CLIENT_ID + ":" + CLIENT_SECRET;
        final String basic = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();

                        HttpUrl originalHttpUrl = original.url();

                        HttpUrl url = originalHttpUrl.newBuilder()
                                .addQueryParameter("userId", String.valueOf(userId))
                                .build();

                        Request.Builder requestBuilder = original.newBuilder()
                                .url(url)
                                .header("Authorization", basic)
                                .header("Content-Type", "application/json")
                                .method(original.method(), original.body());
                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    }
                }).build();
        Retrofit retrofit = retrofitBuilder.client(httpClient).build();
        return retrofit.create(service);
    }

}
