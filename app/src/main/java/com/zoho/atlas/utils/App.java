package com.zoho.atlas.utils;

import android.annotation.SuppressLint;
import android.app.Application;

import com.google.gson.Gson;


public class App extends Application {
    public static Gson gson;

    private static App sApp;
    @SuppressLint("StaticFieldLeak")
    public static BasicUtils appUtils;
    @Override
    public void onCreate() {
        super.onCreate();
        appUtils = new BasicUtils(this);
    }
    public static synchronized App getInstance() {
        return sApp;
    }


}
