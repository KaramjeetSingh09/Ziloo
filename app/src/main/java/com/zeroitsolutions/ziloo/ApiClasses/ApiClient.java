package com.zeroitsolutions.ziloo.ApiClasses;

import android.content.Context;

import com.danikula.videocache.BuildConfig;
import com.zeroitsolutions.ziloo.Constants;
import com.zeroitsolutions.ziloo.SimpleClasses.Functions;
import com.zeroitsolutions.ziloo.SimpleClasses.Variable;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance(Context context) {

        if (retrofit == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

            httpClient.connectTimeout(1, TimeUnit.MINUTES)
                    .readTimeout(1, TimeUnit.MINUTES)
                    .writeTimeout(1, TimeUnit.MINUTES)
                    .build();
            httpClient.addInterceptor(chain -> {
                Request.Builder requestBuilder = chain.request().newBuilder();
                requestBuilder.header("Content-Type", "application/json");
                requestBuilder.header("Api-Key", Constants.API_KEY);
                requestBuilder.header("User-Id", Functions.getSharedPreference(context).getString(Variable.U_ID, "null"));
                requestBuilder.header("Auth-Token", Functions.getSharedPreference(context).getString(Variable.AUTH_TOKEN, "null"));
                requestBuilder.header("device", "android");
                requestBuilder.header("version", BuildConfig.VERSION_NAME);
                requestBuilder.header("ip", Functions.getSharedPreference(context).getString(Variable.DEVICE_IP, null));
                requestBuilder.header("device-token", Functions.getSharedPreference(context).getString(Variable.DEVICE_TOKEN, null));
                return chain.proceed(requestBuilder.build());
            });

            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(httpClient.build()).build();

        }
        return retrofit;
    }
}
