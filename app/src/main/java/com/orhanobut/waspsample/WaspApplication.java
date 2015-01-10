package com.orhanobut.waspsample;

import android.app.Application;

import com.android.volley.toolbox.HttpClientStack;
import com.orhanobut.wasp.LogLevel;
import com.orhanobut.wasp.RequestInterceptor;
import com.orhanobut.wasp.Wasp;
import com.squareup.okhttp.OkHttpClient;

import java.util.Map;

/**
 * @author Orhan Obut
 */
public class WaspApplication extends Application {

    private static MyService service;

    @Override
    public void onCreate() {
        super.onCreate();
        
        RequestInterceptor interceptor = new RequestInterceptor() {
            @Override
            public Map<String, String> getHeaders() {
                return null;
            }

            @Override
            public Map<String, String> getQueryParams() {
                return null;
            }
        };

        service = new Wasp.Builder(this)
                .setEndpoint("http://httpbin.org")
                .setLogLevel(LogLevel.ALL)
                .build()
                .create(MyService.class);
    }

    public static MyService getService() {
        return service;
    }
}
