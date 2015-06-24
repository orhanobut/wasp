package com.orhanobut.wasp;

/**
 * @author Emmar Kardeslik
 */
class WaspMock {

  private final int statusCode;
  private final String path;

  WaspMock(int statusCode, String path) {
    this.statusCode = statusCode;
    this.path = path;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public String getPath() {
    return path;
  }
}
