package com.orhanobut.waspsample;

import android.app.Application;

import com.orhanobut.wasp.Wasp;
import com.orhanobut.wasp.utils.AuthToken;
import com.orhanobut.wasp.utils.LogLevel;
import com.orhanobut.wasp.utils.RequestInterceptor;
import com.orhanobut.wasp.utils.SimpleInterceptor;
import com.orhanobut.wasp.utils.WaspRetryPolicy;

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
            public void onHeadersAdded(Map<String, String> headers) {
            }

            @Override
            public void onQueryParamsAdded(Map<String, Object> params) {
            }

            @Override
            public WaspRetryPolicy getRetryPolicy() {
                return new WaspRetryPolicy(45000, 3, 1.5f);
            }

            @Override
            public AuthToken getAuthToken() {
                return null;
            }
        };

        RequestInterceptor interceptor1 = new SimpleInterceptor() {

            @Override
            public void onHeadersAdded(Map<String, String> headers) {
                super.onHeadersAdded(headers);
                headers.put("InterceptorHeaderKey", "InterceptorHeaderValue");
            }

            @Override
            public void onQueryParamsAdded(Map<String, Object> params) {
                super.onQueryParamsAdded(params);
                params.put("name","something");
            }

            @Override
            public WaspRetryPolicy getRetryPolicy() {
                return new WaspRetryPolicy(45000, 3, 1.5f);
            }

            @Override
            public AuthToken getAuthToken() {
                return new AuthToken("asdfad", true);
            }
        };

        service = new Wasp.Builder(this)
                .setEndpoint("http://httpbin.org")
                .setLogLevel(LogLevel.ALL)
                        //.enableCookies(CookiePolicy.ACCEPT_ALL)
                        //.trustCertificates()
                        //.trustCertificates(R.raw.mytruststore, "123456")
                .setRequestInterceptor(interceptor1)
                .build()
                .create(MyService.class);

    }

    public static MyService getService() {
        return service;
    }
}
