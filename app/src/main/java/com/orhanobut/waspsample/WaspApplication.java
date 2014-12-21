package com.orhanobut.waspsample;

import android.app.Application;

import com.github.nr4bt.wasp.LogLevel;
import com.github.nr4bt.wasp.Wasp;

/**
 * @author Orhan Obut
 */
public class WaspApplication extends Application {

    private static MyService service;

    @Override
    public void onCreate() {
        super.onCreate();

        service = new Wasp.Builder(this)
                .setEndpoint("https://api.github.com")
                .setLogLevel(LogLevel.ALL)
                .build()
                .create(MyService.class);
    }

    public static MyService getService() {
        return service;
    }
}
