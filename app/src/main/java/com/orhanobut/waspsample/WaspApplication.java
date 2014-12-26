package com.orhanobut.waspsample;

import android.app.Application;

import com.orhanobut.wasp.LogLevel;
import com.orhanobut.wasp.Wasp;

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
