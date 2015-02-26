package com.orhanobut.waspsample;

import android.app.Application;
import android.util.Log;

import com.orhanobut.wasp.Wasp;
import com.orhanobut.wasp.WaspOkHttpStack;
import com.orhanobut.wasp.utils.OkHttpLogInterceptor;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * @author Orhan Obut
 */
@SuppressWarnings("unused")
public class WaspApplication extends Application {

    private static MyService service;

    public static MyService getService() {
        return service;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Interceptor okHttpInterceptor = new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                long t1 = System.nanoTime();
                Response response = chain.proceed(chain.request());
                long t2 = System.nanoTime();
                Log.d("WaspSample", "Network time: " + (t2 - t1) / 1e6d + " ms");
                return response;
            }
        };

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.networkInterceptors().add(okHttpInterceptor);
        okHttpClient.networkInterceptors().add(new OkHttpLogInterceptor());

        service = new Wasp.Builder(this)
                .setEndpoint("http://httpbin.org")
                //.setLogLevel(LogLevel.FULL)
                .setWaspHttpStack(new WaspOkHttpStack(okHttpClient))
                .build()
                .create(MyService.class);

    }
}
