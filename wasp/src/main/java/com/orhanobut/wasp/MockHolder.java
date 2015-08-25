package com.orhanobut.wasp;

class MockHolder {

  private final int statusCode;
  private final String path;

  MockHolder(int statusCode, String path) {
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
