package com.codeslayers.hack.travelcheap.api;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by mukulsoftwap on 10/7/2016.
 */

public abstract class APIServices {
    protected static final String END_POINT = "http://54.187.61.210:3000/v1/";
    protected static final String UBER_END_POINT = "https://api.uber.com/v1/estimates/price?";
    protected static final String MAP_END_POINT = "https://maps.googleapis.com/maps/api/directions/json?";

    protected static int TIMEOUT=20;
    protected final Retrofit retrofit;
    protected final Callback<Response> dummyCallback = new Callback<Response>() {
        @Override
        public void onResponse(retrofit.Response<Response> response, Retrofit retrofit) {
        }

        @Override
        public void onFailure(Throwable t) {
        }
    };

    public APIServices() {
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(TIMEOUT, TimeUnit.SECONDS);
        client.setReadTimeout(TIMEOUT,TimeUnit.SECONDS);
        client.setWriteTimeout(TIMEOUT,TimeUnit.SECONDS);
       /* if (BuildConfig.DEBUG) {
            StethoUtil.addNetworkInterceptors(client.networkInterceptors());
            OkHttpLoggingInterceptor.addNetworkInterceptors(client.interceptors());
        }*/
        addHeaderInterceptor(client.interceptors());
        retrofit = new Retrofit.Builder()
                .baseUrl(END_POINT)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private void addHeaderInterceptor(List<Interceptor> interceptors) {
        interceptors.add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request request = original.newBuilder()
                        .header("Accept-Language", Locale.getDefault().toString())
                        .build();

                return chain.proceed(request);
            }
        });
    }
}

