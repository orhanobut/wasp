package com.orhanobut.wasp;

import java.util.Map;

/**
 * @author Orhan Obut
 */
public interface RequestInterceptor {

    /**
     * For each request, these headers will be added
     *
     * @return map which contains headers
     */
    Map<String, String> getHeaders();

    /**
     * For each request, these query params will be added
     *
     * @return map which contains query params
     */
    Map<String, String> getQueryParams();

    /**
     * For each request, these retry policy will be set
     *
     * @return defaultRetryPolicy
     */
    WaspRetryPolicy getRetryPolicy();

}
