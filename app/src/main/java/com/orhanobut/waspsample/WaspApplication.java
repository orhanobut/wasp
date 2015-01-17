package com.orhanobut.waspsample;

import android.app.Application;

import com.orhanobut.wasp.LogLevel;
import com.orhanobut.wasp.RequestInterceptor;
import com.orhanobut.wasp.Wasp;
import com.orhanobut.wasp.WaspRetryPolicy;

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

            @Override
            public WaspRetryPolicy getRetryPolicy() {
                return new WaspRetryPolicy(45000, 3, 1.5f);
            }
        };

        service = new Wasp.Builder(this)
                .setEndpoint("http://httpbin.org")
                .setLogLevel(LogLevel.ALL)
                //.enableCookieHandler(CookiePolicy.ACCEPT_ALL)
                //.trustCertificates()
                //.trustCertificates(R.raw.mytruststore, "123456")
                .build()
                .create(MyService.class);

    }

    public static MyService getService() {
        return service;
    }
}
