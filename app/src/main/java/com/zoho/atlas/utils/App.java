package com.zoho.atlas.utils;

import android.annotation.SuppressLint;
import android.app.Application;


public class App extends Application {
    @SuppressLint("StaticFieldLeak")
    public static BasicUtils appUtils;


    @Override
    public void onCreate() {
        super.onCreate();
        appUtils = new BasicUtils(this);
    }
}
