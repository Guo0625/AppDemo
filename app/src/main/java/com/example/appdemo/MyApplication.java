package com.example.appdemo;

import android.content.Context;

import com.vimedia.core.kinetic.api.DNApplication;


public class MyApplication extends DNApplication {
    /**
     * onCreate lifecycle.
     */
    @Override
    public void onCreate() {
        super.onCreate();
//        DNSDK.applicationOnCreate(this);
    }

    /**
     * onLowMemory lifecycle.
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    /**
     * attachBaseContext lifecycle.
     */
    @Override
    protected void attachBaseContext(final Context base) {
        super.attachBaseContext(base);
//        DNSDK.applicationAttachBaseContext(this, base);
    }

}