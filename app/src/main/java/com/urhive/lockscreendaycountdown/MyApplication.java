package com.urhive.lockscreendaycountdown;

import android.app.Application;

/**
 * Created by Chirag Bhatia on 04-09-2016.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Stetho.initializeWithDefaults(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
