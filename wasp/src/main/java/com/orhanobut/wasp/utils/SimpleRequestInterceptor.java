package com.orhanobut.wasp.utils;

import java.util.Map;

/**
 * Instead of implementing request interceptor, this class can used for simplicity.
 * Only required methods can be overwrite
 */
public class SimpleRequestInterceptor implements RequestInterceptor {

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
