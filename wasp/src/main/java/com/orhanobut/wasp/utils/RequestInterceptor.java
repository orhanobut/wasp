package com.orhanobut.wasp.utils;

import java.util.Map;

/**
 * @author Orhan Obut
 */
public interface RequestInterceptor {

    /**
     * For each request, these headers will be added
     *
     * @param headers
     * @return map which contains headers
     */
    void onHeadersAdded(Map<String, String> headers);

    /**
     * For each request, these query params will be added
     *
     * @param params
     * @return map which contains query params
     */
    void onQueryParamsAdded(Map<String, Object> params);

    /**
     * For each request, these retry policy will be set
     *
     * @return defaultRetryPolicy
     */
    WaspRetryPolicy getRetryPolicy();


    /**
     * @return AuthToken object. This will be used to determine if the authtoken
     * should be used in every calls or just filtered ones
     */
    AuthToken getAuthToken();

}
