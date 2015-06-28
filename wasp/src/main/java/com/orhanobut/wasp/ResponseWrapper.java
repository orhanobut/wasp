package com.orhanobut.wasp;

/**
 * @author Orhan Obut
 */
final class ResponseWrapper<T> {

  private final Callback<T> callback;
  private final T response;
  private final WaspResponse waspResponse;

  public ResponseWrapper(Callback<T> callback, WaspResponse waspResponse, T response) {
    this.callback = callback;
    this.response = response;
    this.waspResponse = waspResponse;
  }

  void submitResponse() {
    callback.onSuccess(waspResponse, response);
  }
}
