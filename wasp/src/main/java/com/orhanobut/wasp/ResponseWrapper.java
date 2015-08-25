package com.orhanobut.wasp;

final class ResponseWrapper<T> {

  private final Callback<T> callback;
  private final T response;
  private final Response waspResponse;

  public ResponseWrapper(Callback<T> callback, Response waspResponse, T response) {
    this.callback = callback;
    this.response = response;
    this.waspResponse = waspResponse;
  }

  void submitResponse() {
    callback.onSuccess(waspResponse, response);
  }
}
