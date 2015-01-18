package com.orhanobut.wasp.utils;

import java.util.Map;

/**
 * @author Orhan Obut
 */
public class SimpleInterceptor implements RequestInterceptor {

    @Override
    public void onHeadersAdded(Map<String, String> headers) {
    }

    @Override
    public void onQueryParamsAdded(Map<String, Object> params) {
    }

    @Override
    public WaspRetryPolicy getRetryPolicy() {
        return null;
    }

    @Override
    public AuthToken getAuthToken() {
        return null;
    }

}
