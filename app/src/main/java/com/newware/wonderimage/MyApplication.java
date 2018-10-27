package com.newware.wonderimage;

import android.app.Application;

/**
 * Created by Bhuvaneshvar Nath Srivastava on 28-07-2018.
 * Copyright (c) 2018
 **/
public class MyApplication extends Application {
    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}
