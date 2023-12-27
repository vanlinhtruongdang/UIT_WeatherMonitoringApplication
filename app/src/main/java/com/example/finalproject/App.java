package com.example.finalproject;

import android.app.Application;

import com.tencent.mmkv.MMKV;

import dagger.hilt.android.HiltAndroidApp;
import timber.log.Timber;

@HiltAndroidApp
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MMKV.initialize(this);
        Timber.plant(new Timber.DebugTree());
    }
}
