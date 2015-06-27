package com.orhanobut.wasp;

/**
 * @author Orhan Obut
 */
final class ResponseWrapper<T> {

  private final MyCallBack<T> callback;
  private final T response;
  private final WaspResponse waspResponse;

  public ResponseWrapper(MyCallBack<T> callback, WaspResponse waspResponse, T response) {
    this.callback = callback;
    this.response = response;
    this.waspResponse = waspResponse;
  }

  void submitResponse() {
    callback.onSuccess(waspResponse, response);
  }
}
