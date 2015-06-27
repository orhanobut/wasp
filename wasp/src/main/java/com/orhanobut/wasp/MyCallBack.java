package com.orhanobut.wasp;

/**
 * @author Orhan Obut
 */
public interface MyCallBack<T> {

  void onSuccess(WaspResponse response, T t);

  void onError(WaspError error);
}
