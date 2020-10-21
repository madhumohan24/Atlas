package com.zoho.atlas.api;

import com.zoho.atlas.keys.key;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class APIClient {

    public static String BASE_URL;

    public static Retrofit getClient(String type) {
        if(type.equals(key.DATA)){
            BASE_URL = key.COUNTRYDETAILS;
        }else{
            BASE_URL = key.OPENWEATHERAPI;
        }

        OkHttpClient client;
        client = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60 / 2, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .cache(null)
                .build();

        return new Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build();
    }
}
