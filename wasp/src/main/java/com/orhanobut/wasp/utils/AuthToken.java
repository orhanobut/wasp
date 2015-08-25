package com.orhanobut.wasp.utils;

/**
 * This class stores the token and auth attributes.
 */
public final class AuthToken {

  private final String token;
  private final boolean filterEnabled;

  public AuthToken(String token) {
    this(token, false);
  }

  public AuthToken(String token, boolean filterEnabled) {
    this.token = token;
    this.filterEnabled = filterEnabled;
  }

  public String getToken() {
    return token;
  }

  public boolean isFilterEnabled() {
    return filterEnabled;
  }
}
